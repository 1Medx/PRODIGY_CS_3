
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class PasswordStrengthChecker extends JFrame {
    private final JPasswordField passwordField;
    private final JPanel strengthIndicator;
    private final JLabel feedbackLabel;
    private final List<CriteriaLabel> criteriaLabels;
    private float animationProgress = 0;

    private static final Color BACKGROUND_COLOR = Color.WHITE;
    private static final Color ACCENT_COLOR = new Color(64, 64, 64);
    private static final Color CRITERIA_MET_COLOR = new Color(76, 175, 80);
    private static final Color CRITERIA_UNMET_COLOR = new Color(176, 190, 197);
    private static final Font MAIN_FONT = new Font("Helvetica", Font.PLAIN, 14);

    public PasswordStrengthChecker() {
        setTitle("Password Strength Checker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(25, 25));
        getContentPane().setBackground(BACKGROUND_COLOR);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Password input field
        passwordField = new JPasswordField(20);
        passwordField.setFont(MAIN_FONT);
        passwordField.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ACCENT_COLOR));
        passwordField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                updatePasswordStrength();
            }
        });

        // Strength indicator
        strengthIndicator = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int width = getWidth();
                int height = getHeight();

                // Draw background
                g2d.setColor(new Color(200, 200, 200));
                g2d.fill(new RoundRectangle2D.Float(0, 0, width, height, height, height));

                // Draw strength bar
                g2d.setColor(getStrengthColor());
                g2d.fill(new RoundRectangle2D.Float(0, 0, width * animationProgress, height, height, height));

                g2d.dispose();
            }
        };
        strengthIndicator.setPreferredSize(new Dimension(300, 8));
        strengthIndicator.setBackground(BACKGROUND_COLOR);

        // Feedback label
        feedbackLabel = new JLabel("Enter a password");
        feedbackLabel.setFont(MAIN_FONT);
        feedbackLabel.setForeground(ACCENT_COLOR);
        feedbackLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Criteria panel
        JPanel criteriaPanel = new JPanel();
        criteriaPanel.setLayout(new BoxLayout(criteriaPanel, BoxLayout.Y_AXIS));
        criteriaPanel.setBackground(BACKGROUND_COLOR);
        criteriaPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        criteriaLabels = new ArrayList<>();
        String[] criteriaTexts = {
                "At least 8 characters",
                "Contains uppercase letter",
                "Contains lowercase letter",
                "Contains number",
                "Contains special character"
        };
        for (String text : criteriaTexts) {
            CriteriaLabel label = new CriteriaLabel(text);
            criteriaLabels.add(label);
            criteriaPanel.add(label);
        }

        // Add components to main panel
        mainPanel.add(createCenteredPanel(passwordField));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(createCenteredPanel(strengthIndicator));
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(feedbackLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(criteriaPanel);

        add(mainPanel, BorderLayout.CENTER);

        pack();
        setSize(400, 400);
        setLocationRelativeTo(null);

        // Start animation
        Timer animationTimer = new Timer(16, e -> {
            strengthIndicator.repaint();
            criteriaLabels.forEach(CriteriaLabel::repaint);
        });
        animationTimer.start();
    }

    private JPanel createCenteredPanel(JComponent component) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(BACKGROUND_COLOR);
        panel.add(component);
        return panel;
    }

    private void updatePasswordStrength() {
        String password = new String(passwordField.getPassword());
        int strength = calculatePasswordStrength(password);
        animationProgress = strength / 100f;

        if (strength < 25) {
            feedbackLabel.setText("Weak password");
        } else if (strength < 50) {
            feedbackLabel.setText("Moderate password");
        } else if (strength < 75) {
            feedbackLabel.setText("Strong password");
        } else {
            feedbackLabel.setText("Very strong password");
        }

        updateCriteria(password);
    }

    private int calculatePasswordStrength(String password) {
        int strength = 0;

        if (password.length() >= 8) strength += 25;
        if (password.matches(".*[A-Z].*")) strength += 25;
        if (password.matches(".*[a-z].*")) strength += 25;
        if (password.matches(".*\\d.*")) strength += 12;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) strength += 13;

        return Math.min(strength, 100);
    }

    private void updateCriteria(String password) {
        criteriaLabels.get(0).setMet(password.length() >= 8);
        criteriaLabels.get(1).setMet(password.matches(".*[A-Z].*"));
        criteriaLabels.get(2).setMet(password.matches(".*[a-z].*"));
        criteriaLabels.get(3).setMet(password.matches(".*\\d.*"));
        criteriaLabels.get(4).setMet(password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*"));
    }

    private Color getStrengthColor() {
        float hue = animationProgress * 0.3f; // 0.3 is green in HSB color model
        return Color.getHSBColor(hue, 0.7f, 0.9f);
    }

    private static class CriteriaLabel extends JLabel {
        private boolean met;
        private float animationProgress = 0;

        public CriteriaLabel(String text) {
            super(text);
            setFont(MAIN_FONT);
            setForeground(ACCENT_COLOR);
            setIcon(new CriteriaIcon());
        }

        public void setMet(boolean met) {
            this.met = met;
        }

        public float getAnimationProgress() {
            return animationProgress;
        }

        public void setAnimationProgress(float animationProgress) {
            this.animationProgress = animationProgress;
        }

        private class CriteriaIcon implements Icon {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = getIconWidth();
                g2d.setColor(met ? CRITERIA_MET_COLOR : CRITERIA_UNMET_COLOR);
                g2d.fill(new Ellipse2D.Float(x, y, size, size));

                if (met) {
                    g2d.setColor(Color.WHITE);
                    g2d.setStroke(new BasicStroke(2f));
                    int padding = 4;
                    int endX = x + size - padding;
                    int endY = y + size / 2;
                    g2d.draw(new Line2D.Float(x + padding, endY, x + size / 2, y + size - padding));
                    g2d.draw(new Line2D.Float(x + (float) size / 2, y + size - padding, endX, y + padding));
                }

                g2d.dispose();
            }

            @Override
            public int getIconWidth() {
                return 16;
            }

            @Override
            public int getIconHeight() {
                return 16;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new PasswordStrengthChecker().setVisible(true);
        });
    }
}