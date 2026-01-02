package oop_group_project;
import java.util.*;
import java.sql.*;

public class DatabaseManager {
	private final String url = "jdbc:mysql://localhost:3306/projectile_db";
	private final String user = "root";
	private final String password = "";
	
	private List<Projectile> historyList = new ArrayList<>();
	public void addSimulation(Projectile p) {
		historyList.add(p);
	}
	
	public void saveToDatabase(Projectile p, double range) {
		try {
			Connection conn = DriverManager.getConnection(url, user, password);
			String query = "INSERT INTO results (type, velocity, angle, max_range) VALUES (?,?,?,?,?)";
			PreparedStatement pstmt = conn.prepareStatement(query);
			pstmt.setString(1, "Standard Ball");
			pstmt.setDouble(2, p.velocity);
			pstmt.setDouble(3, p.angle);
			pstmt.setDouble(4, range);
			pstmt.executeUpdate();
			System.out.println("Data saved");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void deleteSimulation(int id) throws SQLException {
		String query = "DELETE FROM results WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
	}
	
	public ResultSet fetchAllData() throws SQLException {
		Connection conn = DriverManager.getConnection(url, user, password);
		Statement stmt = conn.createStatement();
		return stmt.executeQuery("SELECT * FROM results");
	}
}
