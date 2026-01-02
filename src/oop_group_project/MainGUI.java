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
}
