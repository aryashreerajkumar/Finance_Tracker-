package financetracker;

import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;

/** Application entry point that starts the Swing interface on the EDT. */
public final class Main {
    private Main() { }

    /** Starts the application. */
    public static void main(String[] args) {
        FlatLightLaf.setup();
        SwingUtilities.invokeLater(() -> {
            try {
                new MainFrame().setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Unable to start the application:\n" + ex.getMessage(),
                        "Startup error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
