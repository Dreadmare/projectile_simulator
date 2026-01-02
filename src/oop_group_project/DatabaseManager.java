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
	    String query = "INSERT INTO results (type, velocity, angle, max_range) VALUES (?, ?, ?, ?)";
	    
	    try (Connection conn = DriverManager.getConnection(url, user, password);
	         PreparedStatement pstmt = conn.prepareStatement(query)) {
	        
	        // 2. Map the parameters correctly to the 4 question marks
	        pstmt.setString(1, "Standard Ball"); // Matches 'type'
	        pstmt.setDouble(2, p.velocity);      // Matches 'velocity'
	        pstmt.setDouble(3, p.angle);         // Matches 'angle'
	        pstmt.setDouble(4, range);           // Matches 'max_range'
	        
	        pstmt.executeUpdate();
	        System.out.println("Data saved successfully!");
	        
	    } catch (SQLException e) {
	        // This will now provide much clearer error messages if something else goes wrong
	        System.err.println("SQL Error: " + e.getMessage());
	    }
	}
	
	public void updateRange(int id, double newRange) throws SQLException {
		String query = "UPDATE results SET max_range = ? WHERE id = ?";
		try (Connection conn = DriverManager.getConnection(url, user, password); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setDouble(1, newRange);
			pstmt.setInt(2, id);
			pstmt.executeUpdate();
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
	
	public ResultSet searchByVelocity(double minVelocity) throws SQLException {
		Connection conn = DriverManager.getConnection(url, user, password);
		String query = "SELECT * FROM  results WHERE velocity >= ?";
		PreparedStatement pstmt = conn.prepareStatement(query);
		pstmt.setDouble(1, minVelocity);
		return pstmt.executeQuery();
	}
	
	public int addSimulationAndGetId(Projectile p, double range) {
	    String query = "INSERT INTO results (type, velocity, angle, max_range) VALUES (?, ?, ?, ?)";
	    // RETURN_GENERATED_KEYS allows us to get the ID created by the database
	    try (Connection conn = DriverManager.getConnection(url, user, password);
	         PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
	        
	        pstmt.setString(1, "Ball");
	        pstmt.setDouble(2, p.velocity);
	        pstmt.setDouble(3, p.angle);
	        pstmt.setDouble(4, range);
	        pstmt.executeUpdate();

	        ResultSet rs = pstmt.getGeneratedKeys();
	        if (rs.next()) {
	            return rs.getInt(1); // Return the new ID
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return -1; 
	}
}
