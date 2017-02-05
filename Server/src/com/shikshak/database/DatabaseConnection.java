package com.shikshak.database;

import java.sql.*;

public class DatabaseConnection {

	static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
	static final String DB_URL = "jdbc:mysql://localhost/shikshak_db";
	static final String USER = "root";
	static final String PASS = "root";

	static private Connection conn = null;
	static private Statement stmt = null;
	static private ResultSet rs = null;

	public Statement openConnection() {
		try {
			System.out.println("Open connection");
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(DB_URL, USER, PASS);
			stmt = conn.createStatement();
			/*rs = stmt.executeQuery(str);*/
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stmt;
	}

	public void closeConnection() {
		try {
			conn.close();
			System.out.println("connection closed.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
			} catch (SQLException se2) {
			}
			try {
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				se.printStackTrace();
			}
		}
	}
}
