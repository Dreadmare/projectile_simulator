package oop_group_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Vector;

@SuppressWarnings({ "serial", "unused" })
public class MainGUI extends JFrame {

	private JTextField velocityField, angleField;
	private JButton calculateBtn, saveBtn, clearBtn, deleteBtn, searchBtn;
	private JTable historyTable;
	private DefaultTableModel tableModel;
	private DatabaseManager manager;

	public MainGUI() {
		manager = new DatabaseManager();

		velocityField = new JTextField(10);
		angleField = new JTextField(10);
		calculateBtn = new JButton("Calculate");
		saveBtn = new JButton("Save & Refresh");
		searchBtn = new JButton("Search (Min Vel)");
		deleteBtn = new JButton("Delete Selected");
		clearBtn = new JButton("Clear Fields");

		String[] columns = {"ID","Type","Velocity", "Angle", "Max Range (m)"};
		tableModel = new DefaultTableModel(columns,0);
		historyTable = new JTable(tableModel);

		initLayout();
		setupListeners();
		this.setTitle("Projectile Trajectory Simulator");
	    this.setSize(800, 600); // Sets width and height
	    this.setMinimumSize(new Dimension(500, 400));
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	private void initLayout() {
		setLayout(new BorderLayout(10, 10));

		// Top Panel: Inputs
		JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
		inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Parameters"));
		inputPanel.add(new JLabel("Initial Velocity (m/s):"));
		inputPanel.add(velocityField);
		inputPanel.add(new JLabel("Launch Angle (0-90°):"));
		inputPanel.add(angleField);
		inputPanel.add(calculateBtn);
		inputPanel.add(clearBtn);

		// Bottom Panel: Database Actions
		JPanel actionPanel = new JPanel();
		actionPanel.add(saveBtn);
		actionPanel.add(searchBtn);
		actionPanel.add(deleteBtn);

		// Adding to Frame
		add(inputPanel, BorderLayout.NORTH);
		add(new JScrollPane(historyTable), BorderLayout.CENTER);
		add(actionPanel, BorderLayout.SOUTH);
	}

	private void setupListeners() {
		calculateBtn.addActionListener(e -> {
			try {
				double v = Double.parseDouble(velocityField.getText());
				double a = Double.parseDouble(angleField.getText());
				if (v < 0 || a < 0 || a > 90) {
					throw new InvalidInputException("Please insert valid values");
				}

				StandardBall ball = new StandardBall(v,a);
				double range = ball.calculateRange();

				manager.addSimulation(ball);
				Vector<Object> row = new Vector<>();
				row.add("N/A"); //placeholder
				row.add("Ball");
				row.add(v);
				row.add(a);
				row.add(String.format("%.2f",range));
				tableModel.addRow(row);

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(this, "Input must be a number", "Input Error", JOptionPane.ERROR_MESSAGE);

			} catch (InvalidInputException ex) {
				JOptionPane.showMessageDialog(this, ex.getMessage(), "Logic Error", JOptionPane.WARNING_MESSAGE);
			}
		});

		saveBtn.addActionListener(e -> {
		    int row = historyTable.getSelectedRow();
		    if (row != -1) {
		        try {
		        	double v = Double.parseDouble(tableModel.getValueAt(row, 2).toString());
		            double a = Double.parseDouble(tableModel.getValueAt(row, 3).toString());
		            double r = Double.parseDouble(tableModel.getValueAt(row, 4).toString());
		            
		            StandardBall tempBall = new StandardBall(v, a);
		            manager.saveToDatabase(tempBall, r); 
		            JOptionPane.showMessageDialog(this, "Saved Successfully!");
		            
		            refreshTable();
		            
		        } catch (Exception ex) {
		            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
		        }
		    } else {
		        JOptionPane.showMessageDialog(this, "Please select a row first");
		    }
		});
		
		deleteBtn.addActionListener(e -> {
			int selectedRow = historyTable.getSelectedRow();
			if (selectedRow != -1) {
				try {
					// Assuming ID is in the first column of your table
					int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
					manager.deleteSimulation(id);
					tableModel.removeRow(selectedRow);
					JOptionPane.showMessageDialog(this, "Record Deleted!");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
				}
			}
		});

		searchBtn.addActionListener(e -> {
			String input = JOptionPane.showInputDialog("Enter minimum velocity to search:");
			if (input != null) {
				try {
					double minV = Double.parseDouble(input);
					ResultSet rs = manager.searchByVelocity(minV);

					// Clear table and show results
					tableModel.setRowCount(0);
					while (rs.next()) {
						tableModel.addRow(new Object[]{
								rs.getInt("id"),
								"Ball",
								rs.getDouble("velocity"),
								rs.getDouble("angle"),
								rs.getDouble("max_range")
						});
					}
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Search error: " + ex.getMessage());
				}
			}
		});
	}
	
	private void refreshTable() {
	    try {
	        // Clear the existing rows in the UI
	        tableModel.setRowCount(0);
	        
	        // Fetch fresh data from the DB
	        ResultSet rs = manager.fetchAllData();
	        
	        while (rs.next()) {
	            tableModel.addRow(new Object[]{
	                rs.getInt("id"),
	                rs.getString("type"),
	                rs.getDouble("velocity"),
	                rs.getDouble("angle"),
	                rs.getDouble("max_range")
	            });
	        }
	    } catch (SQLException ex) {
	        JOptionPane.showMessageDialog(this, "Error refreshing data: " + ex.getMessage());
	    }
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(()-> new MainGUI().setVisible(true));
	}
}
