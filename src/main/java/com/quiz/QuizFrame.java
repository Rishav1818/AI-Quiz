package com.quiz;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class QuizFrame extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(82, 86, 255);  // Bright indigo
    private static final Color NEXT_BUTTON_COLOR = new Color(255, 187, 0);  // Warm yellow
    private static final Color SUBMIT_BUTTON_COLOR = new Color(40, 167, 69); // Green
    private static final Color TIMER_WARNING_COLOR = new Color(220, 53, 69); // Red
    
    private static final int TOTAL_QUESTIONS = 10;
    private static final int TIME_PER_QUESTION = 60;
    
    private QuestionGenerator questionGenerator;
    private Question currentQuestion;
    private JLabel questionLabel;
    private JRadioButton[] optionButtons;
    private JButton nextButton;
    private JButton submitButton;
    private JLabel timerLabel;
    private Timer questionTimer;
    private int timeLeft;
    private int totalQuestions = 0;
    private int correctAnswers = 0;
    private String userName;
    private List<Question> attemptedQuestions = new ArrayList<>();
    private List<Integer> userAnswers = new ArrayList<>();
    private ButtonGroup optionGroup;

    public QuizFrame(String userName, String apiKey, DifficultyLevel difficulty) {
        this.userName = userName;
        this.questionGenerator = new QuestionGenerator(apiKey, difficulty);
        
        setTitle("Software Engineering Quiz - " + difficulty.getDisplayName() + " Level");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Main Panel setup and UI initialization
        initializeUI();
        
        // Load first question synchronously before showing the frame
        loadFirstQuestion();
    }

    private void initializeUI() {
        // Main Panel
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top Panel - transparent
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        topPanel.setPreferredSize(new Dimension(800, 60));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        
        // User Info Panel
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        userInfoPanel.setOpaque(false);
        ImageIcon userIconImg = new ImageIcon(getClass().getResource("/icons/user.png"));
        Image scaledUserImg = userIconImg.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH);
        JLabel userIcon = new JLabel(new ImageIcon(scaledUserImg));
        userInfoPanel.add(userIcon);
        
        JLabel userLabel = new JLabel(userName);
        userLabel.setFont(new Font("Arial", Font.BOLD, 20));
        userLabel.setForeground(Color.BLACK);
        userInfoPanel.add(userLabel);
        
        // Timer Panel
        JPanel timerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        timerPanel.setOpaque(false);
        ImageIcon clockIconImg = new ImageIcon(getClass().getResource("/icons/clock.png"));
        Image scaledClockImg = clockIconImg.getImage().getScaledInstance(24, 24, Image.SCALE_SMOOTH);
        JLabel clockIcon = new JLabel(new ImageIcon(scaledClockImg));
        timerPanel.add(clockIcon);
        
        timerLabel = new JLabel(TIME_PER_QUESTION + "s");
        timerLabel.setFont(new Font("Arial", Font.BOLD, 20));
        timerLabel.setForeground(Color.BLACK);
        timerPanel.add(timerLabel);
        
        // Add components to top panel
        topPanel.add(userInfoPanel, BorderLayout.WEST);
        topPanel.add(timerPanel, BorderLayout.EAST);

        // Question Panel
        JPanel questionPanel = new JPanel();
        questionPanel.setBackground(Color.WHITE);
        questionPanel.setLayout(new BorderLayout(0, 20));
        questionPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Question Label
        questionLabel = new JLabel();
        questionLabel.setFont(new Font("Arial", Font.BOLD, 18));
        questionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        questionPanel.add(questionLabel, BorderLayout.NORTH);
        
        // Options Panel
        JPanel optionsPanel = new JPanel();
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setLayout(new GridLayout(4, 1, 0, 10));
        
        optionButtons = new JRadioButton[4];
        
        for (int i = 0; i < 4; i++) {
            final int index = i;
            optionButtons[i] = new JRadioButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int diameter = 16;
                    int x = 4;
                    int y = (getHeight() - diameter) / 2;
                }
            };
            optionButtons[i].setFont(new Font("Arial", Font.PLAIN, 16));
            optionButtons[i].setBackground(Color.WHITE);
            optionButtons[i].setCursor(new Cursor(Cursor.HAND_CURSOR));
            optionButtons[i].setFocusPainted(false);
            optionButtons[i].setBorderPainted(false);
            optionButtons[i].setContentAreaFilled(false);
            
            optionButtons[i].addActionListener(e -> {
                JRadioButton clicked = (JRadioButton) e.getSource();
                
                // If this option was already selected, deselect it
                if (clicked.isSelected()) {
                    // First deselect all other options
                    for (JRadioButton btn : optionButtons) {
                        if (btn != clicked) {
                            btn.setSelected(false);
                        }
                    }
                }
                repaint();
            });
            
            optionButtons[i].addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {  // Double click
                        optionButtons[index].setSelected(false);
                        repaint();
                    }
                }
            });
            optionsPanel.add(optionButtons[i]);
        }
        
        questionPanel.add(optionsPanel, BorderLayout.CENTER);
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonsPanel.setBackground(Color.WHITE);

        // Next Button
        nextButton = new JButton("Next Question ▶") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                if (getModel().isPressed()) {
                    g2d.setColor(NEXT_BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(NEXT_BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(NEXT_BUTTON_COLOR);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Draw text shadow
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.drawString(text, textX + 1, textY + 1);
                
                // Draw text
                g2d.setColor(Color.BLACK);
                g2d.drawString(text, textX, textY);
            }
        };
        nextButton.setPreferredSize(new Dimension(150, 40));
        nextButton.setBorderPainted(false);
        nextButton.setContentAreaFilled(false);
        nextButton.setFocusPainted(false);
        nextButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nextButton.addActionListener(e -> handleNextQuestion());
        
        // Submit Button
        submitButton = new JButton("Submit Quiz") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw background
                if (getModel().isPressed()) {
                    g2d.setColor(SUBMIT_BUTTON_COLOR.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(SUBMIT_BUTTON_COLOR.brighter());
                } else {
                    g2d.setColor(SUBMIT_BUTTON_COLOR);
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                
                // Draw text
                g2d.setFont(new Font("Arial", Font.BOLD, 14));
                g2d.setColor(Color.WHITE);
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int textX = (getWidth() - fm.stringWidth(text)) / 2;
                int textY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();
                g2d.drawString(text, textX, textY);
            }
        };
        submitButton.setPreferredSize(new Dimension(120, 40));
        submitButton.setBorderPainted(false);
        submitButton.setContentAreaFilled(false);
        submitButton.setFocusPainted(false);
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        submitButton.addActionListener(e -> handleSubmit());

        buttonsPanel.add(nextButton);
        buttonsPanel.add(submitButton);
        
        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(questionPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
        
        // Set up timer
        questionTimer = new Timer(1000, e -> {
            timeLeft--;
            timerLabel.setText(timeLeft + "s");
            if (timeLeft <= 10) {
                timerLabel.setForeground(TIMER_WARNING_COLOR);
            }
            if (timeLeft <= 0) {
                handleTimeUp();
            }
        });
    }
    
    private void loadFirstQuestion() {
        try {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            Question question = questionGenerator.generateQuestion();
            
            if (question != null) {
                currentQuestion = question;
                totalQuestions++;
                
                // Update UI
                questionLabel.setText("<html><body style='width: 500px'>" + 
                    "Question " + totalQuestions + ": " + 
                    currentQuestion.getQuestionText() + "</body></html>");
                
                String[] options = currentQuestion.getOptions();
                for (int i = 0; i < 4; i++) {
                    optionButtons[i].setText(options[i]);
                    optionButtons[i].setSelected(false);
                    optionButtons[i].setEnabled(true);
                }
                
                nextButton.setEnabled(true);
                
                // Reset and start timer
                timeLeft = TIME_PER_QUESTION;
                timerLabel.setText(timeLeft + "s");
                timerLabel.setForeground(Color.BLACK);
                questionTimer.restart();
                
                setVisible(true);  // Show frame only after first question is loaded
            } else {
                throw new Exception("Failed to generate first question");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                this,
                "Failed to load the first question. Please try restarting the quiz.",
                "Error",
                JOptionPane.ERROR_MESSAGE
            );
            System.exit(1);
        } finally {
            setCursor(Cursor.getDefaultCursor());
        }
    }
    
    private void handleTimeUp() {
        questionTimer.stop();
        // Save current answer (if any) before moving to next question
        saveCurrentAnswer();
        // Automatically move to next question
        loadNextQuestion();
    }
    
    private void saveCurrentAnswer() {
        int selectedAnswer = -1;
        for (int i = 0; i < optionButtons.length; i++) {
            if (optionButtons[i].isSelected()) {
                selectedAnswer = i;
                break;
            }
        }
        attemptedQuestions.add(currentQuestion);
        userAnswers.add(selectedAnswer);
    }
    
    private void handleNextQuestion() {
        saveCurrentAnswer();
        loadNextQuestion();
    }
    
    private void handleSubmit() {
        // Save the current answer before submitting
        saveCurrentAnswer();
        
        int attemptedCount = totalQuestions;
        if (attemptedCount < TOTAL_QUESTIONS) {
            int choice = JOptionPane.showConfirmDialog(
                this,
                String.format("You have only attempted %d out of %d questions. Are you sure you want to submit?", 
                    attemptedCount, TOTAL_QUESTIONS),
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        } else {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to submit the quiz?",
                "Confirm Submission",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        
        showResults();
    }

    private void loadNextQuestion() {
        if (totalQuestions >= TOTAL_QUESTIONS) {
            nextButton.setEnabled(false);
            int choice = JOptionPane.showConfirmDialog(
                this,
                "You have completed all questions. Would you like to submit the quiz?",
                "Quiz Complete",
                JOptionPane.YES_NO_OPTION
            );
            if (choice == JOptionPane.YES_OPTION) {
                handleSubmit();
            }
            return;
        }

        setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        SwingWorker<Question, Void> worker = new SwingWorker<>() {
            @Override
            protected Question doInBackground() throws Exception {
                return questionGenerator.generateQuestion();
            }

            @Override
            protected void done() {
                try {
                    Question question = get();
                    if (question != null) {
                        currentQuestion = question;
                        totalQuestions++;
                        
                        // Update UI
                        questionLabel.setText("<html><body style='width: 500px'>" + 
                            "Question " + totalQuestions + ": " + 
                            currentQuestion.getQuestionText() + "</body></html>");
                        
                        String[] options = currentQuestion.getOptions();
                        for (int i = 0; i < 4; i++) {
                            optionButtons[i].setText(options[i]);
                            optionButtons[i].setSelected(false);
                            optionButtons[i].setEnabled(true);
                        }
                        
                        nextButton.setEnabled(true);
                        if (totalQuestions == TOTAL_QUESTIONS) {
                            nextButton.setEnabled(false);
                        }
                        
                        // Reset and start timer
                        timeLeft = TIME_PER_QUESTION;
                        timerLabel.setText(timeLeft + "s");
                        timerLabel.setForeground(Color.BLACK);
                        questionTimer.restart();
                    } else {
                        throw new Exception("Failed to generate question");
                    }
                } catch (Exception e) {
                    e.printStackTrace();  // For debugging
                    int choice = JOptionPane.showConfirmDialog(
                        QuizFrame.this,
                        "Failed to load question. Would you like to try again?",
                        "Error",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.ERROR_MESSAGE
                    );
                    
                    if (choice == JOptionPane.YES_OPTION) {
                        loadNextQuestion();
                    } else {
                        showResults();
                    }
                } finally {
                    setCursor(Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    private void showResults() {
        questionTimer.stop();
        // Calculate score
        correctAnswers = 0;
        for (int i = 0; i < attemptedQuestions.size(); i++) {
            if (userAnswers.get(i) == attemptedQuestions.get(i).getCorrectOptionIndex()) {
                correctAnswers++;
            }
        }
        
        // Show results in new frame
        dispose();
        new ResultFrame(userName, correctAnswers, TOTAL_QUESTIONS, attemptedQuestions, userAnswers).setVisible(true);
    }
} 