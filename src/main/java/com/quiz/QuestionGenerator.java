package com.quiz;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class QuestionGenerator {
    private OpenRouterClient client;
    private DifficultyLevel difficulty;
    private Set<String> askedTopics = new HashSet<>();
    private static final String[] TOPICS = {
        "Software Development Methodologies",
        "Design Patterns and Architecture",
        "Testing and Quality Assurance",
        "DevOps and Deployment",
        "Programming Paradigms",
        "Data Structures and Algorithms",
        "Software Security",
        "Database Systems",
        "Version Control",
        "Requirements Engineering",
        "operating systems",
        "computer networks",
        "artificial intelligence",
        "machine learning",
        "data science",
        "web development",
        "cloud computing",
        "software engineering principles",
        "object-oriented programming",
        "system design",
        "software testing",
        "software maintenance",
    };
    private static final String SYSTEM_PROMPT_TEMPLATE = 
        "You are a software engineering quiz generator. Generate a unique %s level multiple choice question about software engineering. " +
        "The question must be about this specific topic: %s. " +
        "The question should be appropriate for the difficulty level:\n" +
        "- EASY: Basic concepts and fundamentals\n" +
        "- MEDIUM: Intermediate concepts and practical applications\n" +
        "- HARD: Advanced topics and complex principles\n\n" +
        "Format your response exactly like this example:\n" +
        "Q: What is encapsulation in OOP?\n" +
        "A) Bundling data and methods that operate on that data within a single unit\n" +
        "B) Breaking down a problem into smaller sub-problems\n" +
        "C) Inheriting properties from parent class\n" +
        "D) Converting one data type to another\n" +
        "Correct: A";

    public QuestionGenerator(String apiKey, DifficultyLevel difficulty) {
        try {
            this.client = new OpenRouterClient(apiKey);
            this.difficulty = difficulty;
        } catch (Exception e) {
            String errorMsg = "Failed to initialize OpenRouter client: " + e.getMessage();
            showErrorDialog("API Error", errorMsg, null);
            throw new RuntimeException(errorMsg, e);
        }
    }

    public Question generateQuestion() throws Exception {
        try {
            // Get a random unasked topic
            String topic = getRandomUnaskedTopic();
            if (topic == null) {
                // If all topics have been asked, reset the tracking
                askedTopics.clear();
                topic = getRandomUnaskedTopic();
            }
            
            String prompt = String.format(SYSTEM_PROMPT_TEMPLATE, difficulty.getDisplayName().toUpperCase(), topic);
            String response = client.createChatCompletion(prompt);
            System.out.println("AI Response:\n" + response);
            
            // Mark this topic as asked
            askedTopics.add(topic);
            
            return parseResponse(response);
        } catch (IOException e) {
            String errorMsg = "Error generating question: " + e.getMessage();
            showErrorDialog("API Error", errorMsg, null);
            throw e;
        }
    }

    private String getRandomUnaskedTopic() {
        List<String> availableTopics = Arrays.stream(TOPICS)
            .filter(topic -> !askedTopics.contains(topic))
            .collect(Collectors.toList());
        
        if (availableTopics.isEmpty()) {
            return null;
        }
        
        Random random = new Random();
        return availableTopics.get(random.nextInt(availableTopics.size()));
    }

    private void showErrorDialog(String title, String message, String details) {
        JDialog dialog = new JDialog((Frame)null, title, true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel messageLabel = new JLabel("<html><body style='width: 300px;'>" + message + "</body></html>");
        messageLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        messagePanel.add(messageLabel);
        
        if (details != null && !details.isEmpty()) {
            messagePanel.add(Box.createVerticalStrut(10));
            JTextArea detailsArea = new JTextArea(details);
            detailsArea.setEditable(false);
            detailsArea.setLineWrap(true);
            detailsArea.setWrapStyleWord(true);
            detailsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
            
            JScrollPane scrollPane = new JScrollPane(detailsArea);
            scrollPane.setPreferredSize(new Dimension(300, 100));
            scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
            messagePanel.add(scrollPane);
        }
        
        dialog.add(messagePanel, BorderLayout.CENTER);
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> dialog.dispose());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(okButton);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.pack();
        dialog.setSize(Math.max(350, dialog.getWidth()), 
                      Math.min(400, dialog.getHeight()));
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private Question parseResponse(String response) throws Exception {
        try {
            String[] parts = response.split("\n");
            if (parts.length < 6) {
                throw new IllegalArgumentException("Invalid response format: Expected 6 lines, got " + parts.length);
            }

            String questionText = parts[0].substring(3); // Remove "Q: "
            String[] options = new String[4];
            for (int i = 0; i < 4; i++) {
                options[i] = parts[i + 1].substring(3); // Remove "A) ", "B) ", etc.
            }
            String correct = parts[5].substring(9); // Remove "Correct: "
            int correctIndex = correct.charAt(0) - 'A';

            if (correctIndex < 0 || correctIndex >= 4) {
                throw new IllegalArgumentException("Invalid answer index: " + correct.charAt(0));
            }

            return new Question(questionText, options, correctIndex);
        } catch (Exception e) {
            String errorMsg = "Failed to parse AI response: " + e.getMessage();
            showErrorDialog("Parse Error", errorMsg, "Response received:\n" + response);
            throw e;
        }
    }
} 