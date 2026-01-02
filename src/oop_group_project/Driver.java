package oop_group_project;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Driver {
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("ERROR");
		}
		
		SwingUtilities.invokeLater(() -> {
			MainGUI frame = new MainGUI();
			frame.setVisible(true);
			frame.setLocationRelativeTo(null);
		});
	}
}
