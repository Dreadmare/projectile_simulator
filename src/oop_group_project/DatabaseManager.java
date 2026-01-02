package oop_group_project;
import java.util.*;
import java.sql.*;

public class DatabaseManager {
	
	private List<Projectile> historyList = new ArrayList<>();
	public void addSimulation(Projectile p) {
		historyList.add(p);
	}
	
	public void saveToDatabase(Projectile p, double range) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/projectile_db","root","");
			String query = "INSERT INTO results (type, velocity, angle, max_range) VALUES (?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, "Standard Ball");
			pstmt.executeUpdate();
			System.out.println("Data saved");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
