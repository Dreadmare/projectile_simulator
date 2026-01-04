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
import java.io.*;

@SuppressWarnings({ "serial", "unused" })
public class MainGUI extends JFrame {

	private JTextField velocityField, angleField, massField, dragField;
	private JButton calculateBtn, exportBtn, clearBtn, deleteBtn, searchBtn, loadBtn, resetBtn;
	private JTable historyTable;
	private DefaultTableModel tableModel;
	private DatabaseManager manager;
	private JSpinner massSpinner;
	private JSlider airResistanceSlider;

	public MainGUI() {
		manager = new DatabaseManager();

		velocityField = new JTextField(10);
		angleField = new JTextField(10);
		massField = new JTextField(10);
		dragField = new JTextField(10);
		calculateBtn = new JButton("Calculate");
		exportBtn = new JButton("Export to CSV");
		searchBtn = new JButton("Search (Min Velocity)");
		deleteBtn = new JButton("Delete Selected");
		clearBtn = new JButton("Clear Fields");
		loadBtn = new JButton("Reload History");
		resetBtn = new JButton("RESET");

		String[] columns = {"ID","Mass (kg)","Drag (%)","Velocity (m/s)", "Angle", "Max Range (m)"};
		tableModel = new DefaultTableModel(columns,0);
		historyTable = new JTable(tableModel);

		initLayout();
		setupListeners();
		this.setTitle("Projectile Trajectory Simulator");
	    this.setSize(800, 600); 
	    this.setMinimumSize(new Dimension(500, 400));
	    this.setLocationRelativeTo(null);
	    this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    
	    refreshTable();
	}

	private void initLayout() {
		setLayout(new BorderLayout(10, 10));
		massSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.1, 100.0, 0.5));
		airResistanceSlider = new JSlider(0, 100, 0); // 0 to 1.0 scale

		// Top Panel: Inputs
		JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 10));
		inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Parameters"));
		inputPanel.add(new JLabel("Initial Velocity (m/s):"));
		inputPanel.add(velocityField);
		inputPanel.add(new JLabel("Launch Angle (0-90°):"));
		inputPanel.add(angleField);
		inputPanel.add(calculateBtn);
		inputPanel.add(clearBtn);
		inputPanel.add(new JLabel("Mass (kg):"));
		inputPanel.add(massSpinner);
		inputPanel.add(new JLabel("Air Resistance (%):"));
		inputPanel.add(airResistanceSlider);

		// Bottom Panel: Database Actions
		JPanel actionPanel = new JPanel();
		actionPanel.add(exportBtn);
		actionPanel.add(searchBtn);
		actionPanel.add(deleteBtn);
		actionPanel.add(loadBtn);
		actionPanel.add(resetBtn);

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
		        double m = (Double) massSpinner.getValue(); 

		        double d = airResistanceSlider.getValue() / 100.0;

		        if (v < 0 || a < 0 || a > 90 || m <= 0) {
		            throw new InvalidInputException("Invalid Parameters! Check your inputs.");
		        }

		        Projectile projectile = new StandardBall(v, a, m, d);
		        double range = projectile.calculateRange();

		        
		        int newId = manager.addSimulationAndGetId(projectile, range);

		        tableModel.addRow(new Object[]{
		            (newId == -1) ? "DB Error" : newId,
		            m,
		            (d*100) + "%",
		            v, 
		            a, 
		            String.format("%.2f", range)
		        });

		    } catch (NumberFormatException ex) {
		        JOptionPane.showMessageDialog(this, "Velocity and Angle must be valid numbers!");
		    } catch (InvalidInputException ex) {
		        JOptionPane.showMessageDialog(this, ex.getMessage());
		    }
		});
		
		
		clearBtn.addActionListener(e -> {
		    velocityField.setText("");
		    angleField.setText("");
		    massField.setText("");
		    dragField.setText("");
		    tableModel.setRowCount(0);
		    
		    velocityField.requestFocus();
		    
		    System.out.println("UI Cleared");
		});
		
		deleteBtn.addActionListener(e -> {
			int selectedRow = historyTable.getSelectedRow();
			if (selectedRow != -1) {
				try {
					int id = Integer.parseInt(tableModel.getValueAt(selectedRow, 0).toString());
					manager.deleteSimulation(id);
					tableModel.removeRow(selectedRow);
					JOptionPane.showMessageDialog(this, "Record Deleted!");
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(this, "Error deleting: " + ex.getMessage());
				}
			}
		});
		
		exportBtn.addActionListener(e -> {
		    if (tableModel.getRowCount() == 0) {
		        JOptionPane.showMessageDialog(this, "No data available to export!");
		        return;
		    }

		    JFileChooser fileChooser = new JFileChooser();
		    fileChooser.setDialogTitle("Specify a file to save");
		    
		    int userSelection = fileChooser.showSaveDialog(this);

		    if (userSelection == JFileChooser.APPROVE_OPTION) {
		        java.io.File fileToSave = fileChooser.getSelectedFile();

		        String filePath = fileToSave.getAbsolutePath();
		        if (!filePath.toLowerCase().endsWith(".csv")) {
		            fileToSave = new java.io.File(filePath + ".csv");
		        }

		        try (java.io.PrintWriter pw = new java.io.PrintWriter(fileToSave)) {
		            pw.println("ID, Mass (kg), Drag Resistance (%), Velocity (m/s), Angle(deg),MaxRange(m)");

		            for (int i = 0; i < tableModel.getRowCount(); i++) {
		                StringBuilder row = new StringBuilder();
		                for (int j = 0; j < tableModel.getColumnCount(); j++) {
		                    row.append(tableModel.getValueAt(i, j));
		                    if (j < tableModel.getColumnCount() - 1) row.append(",");
		                }
		                pw.println(row.toString());
		            }

		            JOptionPane.showMessageDialog(this, "Data exported successfully to: " + fileToSave.getName());
		        } catch (java.io.IOException ex) {
		            JOptionPane.showMessageDialog(this, "Error writing to file: " + ex.getMessage());
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
								rs.getDouble("mass"),
								rs.getDouble("drag"),
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
		
		loadBtn.addActionListener(e -> {
		    refreshTable();
		    JOptionPane.showMessageDialog(this, "Data loaded from database.");
		});
		
		resetBtn.addActionListener(e -> {
		    int confirm = JOptionPane.showConfirmDialog(this, 
		        "This will delete ALL history and reset IDs. Are you sure?", 
		        "Warning", JOptionPane.YES_NO_OPTION);
		        
		    if (confirm == JOptionPane.YES_OPTION) {
		        try {
		            manager.resetDatabase();
		            refreshTable(); // Clear the UI table
		            JOptionPane.showMessageDialog(this, "Database has been reset!");
		        } catch (SQLException ex) {
		            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
		        }
		    }
		});
	}
	
	private void refreshTable() {
	    try {
	        tableModel.setRowCount(0);
	        
	        ResultSet rs = manager.fetchAllData();
	        
	        while (rs.next()) {
	            tableModel.addRow(new Object[]{
	                rs.getInt("id"),
	                rs.getDouble("mass"),
	                (rs.getDouble("drag")*100) + "%",
	                rs.getDouble("velocity"),
	                rs.getDouble("angle"),
	                String.format("%.2f", rs.getDouble("max_range"))
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
