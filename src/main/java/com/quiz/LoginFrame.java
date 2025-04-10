package com.quiz;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JPanel mainPanel;
    private JTextField nameField;
    private JComboBox<DifficultyLevel> difficultyComboBox;

    public LoginFrame() {
        setTitle("Software Engineering Quiz - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
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
        JLabel welcomeLabel = new JLabel("Welcome to SE Quiz");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(95, 30, 250, 40);
        mainPanel.add(welcomeLabel);

        // Name Label
        JLabel nameLabel = new JLabel("Enter your name:");
        nameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setBounds(50, 100, 150, 30);
        mainPanel.add(nameLabel);

        // Name Text Field
        nameField = new JTextField();
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBounds(50, 140, 300, 35);
        nameField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.WHITE),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        mainPanel.add(nameField);

        // Difficulty Label
        JLabel difficultyLabel = new JLabel("Select difficulty level:");
        difficultyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        difficultyLabel.setForeground(Color.WHITE);
        difficultyLabel.setBounds(50, 190, 150, 30);
        mainPanel.add(difficultyLabel);

        // Difficulty ComboBox
        difficultyComboBox = new JComboBox<>(DifficultyLevel.values());
        difficultyComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        difficultyComboBox.setBounds(50, 230, 300, 35);
        difficultyComboBox.setBackground(Color.WHITE);
        difficultyComboBox.setBorder(BorderFactory.createLineBorder(Color.WHITE));
        mainPanel.add(difficultyComboBox);

        // Start Button
        JButton startButton = new JButton("Start Quiz");
        startButton.setForeground(Color.BLACK);
        startButton.setFont(new Font("Arial", Font.BOLD, 16));
        startButton.setFocusPainted(false);
        startButton.setBorderPainted(true);  // Enable border painting
        startButton.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2));  // Add white border
        startButton.setContentAreaFilled(true);  // Enable background filling
        startButton.setOpaque(true);  // Make button opaque
        startButton.setBounds(50, 290, 300, 40);
        startButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        startButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please enter your name",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            DifficultyLevel selectedDifficulty = (DifficultyLevel) difficultyComboBox.getSelectedItem();
            dispose();
            new RulesFrame(name, selectedDifficulty).setVisible(true);
        });
        mainPanel.add(startButton);

        add(mainPanel);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
} 