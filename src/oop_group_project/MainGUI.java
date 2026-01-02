package oop_group_project;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class MainGUI extends JFrame {
	
	private JTextField velocityField, angleField;
	private JButton calculateBtn, saveBtn, clearBtn;
	private JTable historyTable;
	private DefaultTableModel tableModel;
	private DatabaseManager manager;
	
	public MainGUI() {
		manager = new DatabaseManager();
		setTitle("Projectile Trajectory Calculator");
		setSize(500,400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLayout(new BorderLayout(10,10));
		
		JPanel inputPanel = new JPanel(new GridLayout(3,2,5,5));
		inputPanel.add(new JLabel("Initial Velocity (m/s): "));
		velocityField = new JTextField();
		inputPanel.add(velocityField);
		
		calculateBtn = new JButton("Calculate");
		clearBtn = new JButton("Clear");
		inputPanel.add(calculateBtn);
		inputPanel.add(clearBtn);
		
		String[] columns = {"Type","Velocity", "Angle", "Max Range (m)"};
		tableModel = new DefaultTableModel(columns,0);
		historyTable = new JTable(tableModel);
		
		saveBtn = new JButton("Save");
		add(inputPanel,BorderLayout.NORTH);
		add(new JScrollPane(historyTable),BorderLayout.CENTER);
		add(saveBtn,BorderLayout.SOUTH);
		
		setupListeners();
	}
	
	private void setupListeners() {
		
	}
}
