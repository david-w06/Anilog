package ui;

import javax.swing.*;
import java.awt.*;

public class AnimatedPanelContainer extends JPanel {

    private JPanel currentPanel;
    private boolean animating = false;

    public AnimatedPanelContainer(JPanel initialPanel) {
        setLayout(null);
        currentPanel = initialPanel;
        add(currentPanel);
    }

    /**
     * EFFECTS: slides from the current panel to the new panel vertically.
     *          If the new panel is below the current panel, it enters from
     *          the bottom. Otherwise it enters from the top.
     */
    public void showPanel(JPanel newPanel, boolean fromBottom) {

        if (animating || newPanel == currentPanel) {
            return;
        }

        animating = true;

        int width = getWidth();
        int height = getHeight();

        // New panel starts outside the container
        int newStartY = fromBottom ? height : -height;

        newPanel.setBounds(0, newStartY, width, height);
        add(newPanel);

        // Old panel moves in the opposite direction
        int oldEndY = fromBottom ? -height : height;

        Timer timer = new Timer(10, null);

        long startTime = System.currentTimeMillis();
        int duration = 300; // milliseconds

        timer.addActionListener(e -> {

            long elapsed = System.currentTimeMillis() - startTime;

            float progress = Math.min(1f, (float) elapsed / duration);

            // Smooth ease-in-out
            float eased = easeInOut(progress);

            int currentY = (int) (oldEndY * eased);
            int newY = (int) (newStartY * (1 - eased));

            currentPanel.setLocation(0, currentY);
            newPanel.setLocation(0, newY);

            repaint();

            if (progress >= 1f) {
                timer.stop();

                remove(currentPanel);

                currentPanel = newPanel;
                currentPanel.setLocation(0, 0);

                animating = false;

                revalidate();
                repaint();
            }
        });

        timer.start();
    }

    /**
     * EFFECTS: returns a smooth ease-in-out interpolation value.
     */
    private float easeInOut(float x) {
        return x < 0.5f
                ? 2 * x * x
                : 1 - (float) Math.pow(-2 * x + 2, 2) / 2;
    }

    @Override
    public void doLayout() {
        if (!animating && currentPanel != null) {
            currentPanel.setBounds(0, 0, getWidth(), getHeight());
        }
    }
}
