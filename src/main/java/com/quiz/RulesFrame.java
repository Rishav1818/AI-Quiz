package com.quiz;

import javax.swing.*;
import java.awt.*;

public class RulesFrame extends JFrame {
    private JPanel mainPanel;
    private JButton startButton;
    private JCheckBox acceptRulesCheckbox;
    private final String userName;
    private final DifficultyLevel difficulty;

    public RulesFrame(String userName, DifficultyLevel difficulty) {
        this.userName = userName;
        this.difficulty = difficulty;
        setTitle("Software Engineering Quiz - Rules");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 500);
        setLocationRelativeTo(null);
        setResizable(false);
        
        initializeComponents();
    }

    private void initializeComponents() {
        mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                int w = getWidth();
                int h = getHeight();
                Color color1 = new Color(25, 118, 210);
                Color color2 = new Color(25, 118, 210, 150);
                GradientPaint gp = new GradientPaint(0, 0, color1, w, h, color2);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, w, h);
            }
        };
        mainPanel.setLayout(null);

        // Welcome Label
        JLabel welcomeLabel = new JLabel("Welcome, " + userName + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(50, 30, 500, 40);
        mainPanel.add(welcomeLabel);

        // Difficulty Label
        JLabel difficultyLabel = new JLabel("Selected Difficulty: " + difficulty.getDisplayName());
        difficultyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setBounds(50, 70, 500, 30);
        mainPanel.add(difficultyLabel);

        // Rules Panel
        JPanel rulesPanel = new JPanel();
        rulesPanel.setLayout(new BoxLayout(rulesPanel, BoxLayout.Y_AXIS));
        rulesPanel.setBackground(new Color(255, 255, 255, 220));
        rulesPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Rules Title
        JLabel rulesTitle = new JLabel("Quiz Rules and Instructions");
        rulesTitle.setFont(new Font("Arial", Font.BOLD, 18));
        rulesTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        rulesPanel.add(rulesTitle);
        rulesPanel.add(Box.createVerticalStrut(20));

        // Rules Text
        String[] rules = {
            "1. The quiz consists of multiple-choice questions about software engineering.",
            "2. Each question has a time limit of 60 seconds.",
            "3. You must select an answer before the timer runs out.",
            "4. You cannot change your answer once submitted.",
            "5. Each correct answer earns you 1 point.",
            "6. There is no negative marking for wrong answers.",
            "7. You cannot go back to previous questions.",
            "8. Final results will be displayed after completing all questions.",
            "9. The quiz cannot be paused once started.",
            "10. Ensure you have a stable internet connection throughout the quiz.",
            "",
            "Difficulty Level: " + difficulty.getDisplayName(),
            getDifficultyDescription(difficulty)
        };

        for (String rule : rules) {
            JLabel ruleLabel = new JLabel(rule);
            ruleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            ruleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rulesPanel.add(ruleLabel);
            rulesPanel.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(rulesPanel);
        scrollPane.setBounds(50, 110, 500, 250);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        mainPanel.add(scrollPane);

        // Checkbox
        acceptRulesCheckbox = new JCheckBox("I have read and accept the rules");
        acceptRulesCheckbox.setFont(new Font("Arial", Font.PLAIN, 14));
        acceptRulesCheckbox.setForeground(Color.WHITE);
        acceptRulesCheckbox.setBackground(null);
        acceptRulesCheckbox.setBounds(50, 380, 300, 30);
        acceptRulesCheckbox.addActionListener(e -> startButton.setEnabled(acceptRulesCheckbox.isSelected()));
        mainPanel.add(acceptRulesCheckbox);

        // Start Button
        startButton = new JButton("Start Quiz");
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setBounds(150, 420, 300, 40);
        startButton.setForeground(Color.BLACK);
        startButton.setEnabled(false);
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(true);
        startButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));
        startButton.setContentAreaFilled(true);
        startButton.setOpaque(true);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        startButton.addActionListener(e -> {
            dispose();
            new QuizFrame(userName, Constants.API_KEY, difficulty).setVisible(true);
        });
        
        mainPanel.add(startButton);

        add(mainPanel);
    }

    private String getDifficultyDescription(DifficultyLevel difficulty) {
        switch (difficulty) {
            case EASY:
                return "Questions will cover basic software engineering concepts and fundamentals.";
            case MEDIUM:
                return "Questions will include intermediate concepts and practical applications.";
            case HARD:
                return "Questions will cover advanced topics and complex software engineering principles.";
            default:
                return "Questions will cover software engineering concepts and fundamentals.";
        }
    }
} 