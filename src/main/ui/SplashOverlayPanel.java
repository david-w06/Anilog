package ui;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

// JPanel that represents a temporary top layer intro animation
public class SplashOverlayPanel extends JPanel {

    private final int cornerRadius;
    private float alpha = 0.0f;
    private int state = 0; // 0 = fading in, 1 = holding, 2 = fading out
    private int holdFrames = 0;
    private static final int MAX_HOLD_FRAMES = 60; // 1s hold at 60 fps
    private Timer timer;

    private static final String AUDIO_PATH = "/audio/anilog_dub.wav";

    // REQUIRES: cornerRadius >= 0
    // MODIFIES: this
    // EFFECTS: constructs a panel and installs mouse listeners to block background user input during animation
    public SplashOverlayPanel(int cornerRadius) {
        this.cornerRadius = cornerRadius;
        setOpaque(false);
        MouseAdapter blockInputListener = new MouseAdapter() {};
        addMouseListener(blockInputListener);
        addMouseMotionListener(blockInputListener);
    }

    // MODIFIES: this
    // EFFECTS: start animation, initiates audio playback, executes onComplete callback when animation finishes
    public void playAnimation(Runnable onComplete) {
        alpha = 0.0f;
        state = 0;
        holdFrames = 0;

        // playAudio(AUDIO_PATH);

        timer = new Timer(16, e -> advanceAnimationFrame(onComplete));
        timer.start();
    }

    // MODIFIES: this
    // EFFECTS: steps through splash animation states stops timer, triggers onComplete callback when fade-out finishes
    private void advanceAnimationFrame(Runnable onComplete) {
        if (state == 0) {
            alpha += 0.025f;
            if (alpha >= 1.0f) {
                alpha = 1.0f;
                state = 1;
            }
        } else if (state == 1) {
            holdFrames++;
            if (holdFrames >= MAX_HOLD_FRAMES) {
                state = 2;
            }
        } else if (state == 2) {
            alpha -= 0.025f;
            if (alpha <= 0.0f) {
                alpha = 0.0f;
                timer.stop();
                if (onComplete != null) {
                    onComplete.run();
                } 
            }
        }
        repaint();
    }

    @Override
    // REQUIRES: g != null
    // MODIFIES: g
    // EFFECTS: paints dark background fill clipped to rounded panel shape and renders fading logo
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setClip(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius));

        g2.setComposite(AlphaComposite.SrcOver);
        g2.setColor(Color.decode("#0A0A0A"));
        g2.fillRect(0, 0, getWidth(), getHeight());

        drawLogo(g2);

        g2.dispose();
    }

    // REQUIRES: g2 != null
    // MODIFIES: g2
    // EFFECTS: draws centered "AniLog" logo text
    private void drawLogo(Graphics2D g2) {
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
        Font logoFont = Theme.FONT_TITLE.deriveFont(48f);
        g2.setFont(logoFont);

        String text = "AniLog";
        FontMetrics fm = g2.getFontMetrics(logoFont);
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getAscent();

        int x = (getWidth() - textWidth) / 2;
        int y = (getHeight() + textHeight) / 2 - 10;

        g2.setColor(Color.decode("#FF2A7A"));
        g2.drawString(text, x, y);
    }

    // REQUIRES: resourcePath != null
    // EFFECTS: starts a background thread to load and play audio at resourcePath
    private void playAudio(String resourcePath) {
        new Thread(() -> {
            try {
                AudioInputStream audioStream = getAudioStream(resourcePath);
                if (audioStream == null) {
                    System.err.println("Audio file not found at resource or local path.");
                    return;
                }
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                System.err.println("Splash audio error: " + e.getMessage());
            }
        }).start();
    }

    // REQUIRES: resourcePath != null
    // EFFECTS: attempts to resolve AudioInputStream from classpath resource or local fallback path;
    //          returns null if resource cannot be found.
    private AudioInputStream getAudioStream(String resourcePath) throws Exception {
        InputStream audioSrc = getClass().getResourceAsStream(resourcePath);
        if (audioSrc != null) {
            return AudioSystem.getAudioInputStream(new BufferedInputStream(audioSrc));
        }

        File localFile = new File("src/main/resources" + resourcePath);
        if (!localFile.exists()) {
            localFile = new File("src/main/resources/audio/anilog_dub.wav");
        }
        if (localFile.exists()) {
            return AudioSystem.getAudioInputStream(localFile);
        }

        return null;
    }
}