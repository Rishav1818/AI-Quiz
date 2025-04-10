package com.quiz;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ResultFrame extends JFrame {
    public ResultFrame(String userName, int score, int totalQuestions, List<Question> questions, List<Integer> userAnswers) {
        setTitle("Quiz Results");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // Set to full screen size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setLocationRelativeTo(null);

        // Main Panel with gradient background
        JPanel mainPanel = new JPanel() {
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
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));

        // Results Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        // Congratulations Label
        JLabel congratsLabel = new JLabel("Quiz Completed! 🏆");
        congratsLabel.setFont(new Font("Arial", Font.BOLD, 28));
        congratsLabel.setForeground(Color.WHITE);
        congratsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(congratsLabel);
        headerPanel.add(Box.createVerticalStrut(10));

        // User Name Label
        JLabel nameLabel = new JLabel("Congratulations, " + userName + "!");
        nameLabel.setFont(new Font("Arial", Font.BOLD, 20));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        headerPanel.add(nameLabel);
        headerPanel.add(Box.createVerticalStrut(20));

        // Score Panel
        JPanel scorePanel = new JPanel();
        scorePanel.setOpaque(false);
        scorePanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        // Final Score
        double percentage = (score * 100.0) / totalQuestions;
        JLabel scoreLabel = new JLabel(String.format("Final Score: %d/%d (%.1f%%)", 
            score, totalQuestions, percentage));
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setForeground(Color.WHITE);
        scorePanel.add(scoreLabel);

        headerPanel.add(scorePanel);

        // Question Review Panel with white semi-transparent background
        JPanel reviewPanel = new JPanel();
        reviewPanel.setLayout(new BoxLayout(reviewPanel, BoxLayout.Y_AXIS));
        reviewPanel.setBackground(new Color(255, 255, 255, 220));
        reviewPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Add each question and its result
        for (int i = 0; i < questions.size(); i++) {
            Question question = questions.get(i);
            int userAnswer = userAnswers.get(i);
            boolean isCorrect = userAnswer == question.getCorrectOptionIndex();

            JPanel questionPanel = new JPanel();
            questionPanel.setLayout(new BoxLayout(questionPanel, BoxLayout.Y_AXIS));
            questionPanel.setBackground(null);
            questionPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 20, 0));

            // Question Header
            JLabel questionHeader = new JLabel("Question " + (i + 1) + ":");
            questionHeader.setFont(new Font("Arial", Font.BOLD, 16));
            questionPanel.add(questionHeader);

            // Question Text
            JLabel questionText = new JLabel(question.getQuestionText());
            questionText.setFont(new Font("Arial", Font.PLAIN, 14));
            questionPanel.add(questionText);
            questionPanel.add(Box.createVerticalStrut(10));

            // Options
            String[] options = question.getOptions();
            for (int j = 0; j < options.length; j++) {
                String prefix = (j == question.getCorrectOptionIndex()) ? "✓ " :
                              (j == userAnswer) ? "✗ " : "  ";
                JLabel optionLabel = new JLabel(prefix + (char)('A' + j) + ") " + options[j]);
                optionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                if (j == question.getCorrectOptionIndex()) {
                    optionLabel.setForeground(new Color(0, 150, 0));
                } else if (j == userAnswer && userAnswer != -1) {
                    optionLabel.setForeground(new Color(200, 0, 0));
                }
                questionPanel.add(optionLabel);
            }

            // Result Status
            String resultText = userAnswer == -1 ? "Not attempted" :
                              isCorrect ? "Correct!" : "Incorrect";
            JLabel resultLabel = new JLabel(resultText);
            resultLabel.setFont(new Font("Arial", Font.BOLD, 14));
            resultLabel.setForeground(userAnswer == -1 ? Color.GRAY :
                                    isCorrect ? new Color(0, 150, 0) : new Color(200, 0, 0));
            questionPanel.add(Box.createVerticalStrut(5));
            questionPanel.add(resultLabel);

            reviewPanel.add(questionPanel);
            if (i < questions.size() - 1) {
                reviewPanel.add(new JSeparator());
            }
        }

        // Scroll Pane for Review
        JScrollPane scrollPane = new JScrollPane(reviewPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Try Again Button
        JButton tryAgainButton = new JButton("Try Again");
        tryAgainButton.setFont(new Font("Arial", Font.BOLD, 16));
        tryAgainButton.setForeground(Color.BLACK);
        tryAgainButton.setBackground(Color.WHITE);
        tryAgainButton.setFocusPainted(false);
        tryAgainButton.setBorderPainted(false);
        tryAgainButton.setContentAreaFilled(true);
        tryAgainButton.setOpaque(true);
        tryAgainButton.setBounds(50, 290, 300, 40);
        tryAgainButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add modern rounded look
        tryAgainButton.putClientProperty("JButton.buttonType", "roundRect");
        
        // Add hover effect
        tryAgainButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                tryAgainButton.setBackground(new Color(245, 245, 245));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                tryAgainButton.setBackground(Color.WHITE);
            }
        });

        tryAgainButton.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setOpaque(false);
        buttonPanel.add(tryAgainButton);

        // Add all components to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}