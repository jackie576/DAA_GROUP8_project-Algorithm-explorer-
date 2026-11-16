// Holds the playback buttons and animation speed controls used during demos.
// This file contains the core logic for this part of the program and keeps the UI
// focused on the algorithm being explained instead of mixing presentation with logic.

// this tells the compiler the package the file belongs to.
package frontend;

// these are the imports for the classes and packages used in this file.
import java.awt.*;
import java.util.function.DoubleConsumer;
import javax.swing.*;

// This class provides playback controls for algorithm animations, including pause/resume, step, reset, and speed adjustment.
public class PlaybackControls extends JPanel {

    private final JButton pause = Theme.button("Pause", false);
    private final JLabel speedValue = Theme.label("1.0x");

    public PlaybackControls(Runnable onPause, Runnable onStep,
                            Runnable onReset, DoubleConsumer onSpeed) {
        setOpaque(false);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JButton step = Theme.button("Step", false);
        JButton reset = Theme.button("Reset", false);

        JPanel buttons = new JPanel(new GridLayout(1, 3, 6, 0));
        buttons.setOpaque(false);
        for (JButton b : new JButton[]{pause, step, reset}) {
            b.setBorder(BorderFactory.createEmptyBorder(8, 2, 8, 2));
        }
        buttons.add(pause);
        buttons.add(step);
        buttons.add(reset);
        buttons.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        // slider value 1..16 means 0.25x .. 4x
        JSlider speed = new JSlider(1, 16, 4);
        speed.setOpaque(false);
        speed.addChangeListener(e -> {
            double value = speed.getValue() / 4.0;
            speedValue.setText(String.format("%.2fx", value));
            onSpeed.accept(value);
        });

        JPanel speedRow = new JPanel(new BorderLayout(8, 0));
        speedRow.setOpaque(false);
        speedRow.add(Theme.label("Speed"), BorderLayout.WEST);
        speedRow.add(speed, BorderLayout.CENTER);
        speedRow.add(speedValue, BorderLayout.EAST);
        speedRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        speedRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        add(buttons);
        add(Box.createVerticalStrut(10));
        add(speedRow);

        pause.addActionListener(e -> onPause.run());
        step.addActionListener(e -> onStep.run());
        reset.addActionListener(e -> onReset.run());
    }

    // Updates the text of the pause button to reflect whether the animation is currently playing or paused.
    public void setPlaying(boolean playing) {
        pause.setText(playing ? "Pause" : "Resume");
    }
}
