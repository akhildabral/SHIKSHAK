package com.shikshak.database;

import java.sql.ResultSet;
import java.sql.SQLException;


public class Test {
	static DatabaseConnection db = new DatabaseConnection();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
		String sql = "SELECT id, fname FROM user";
		ResultSet rs = db.openConnection(sql);
		while (rs.next()) {
			int id = rs.getInt("id");
			String first = rs.getString("fname");

			System.out.print("ID: " + id);
			System.out.println(", First: " + first);
		}
		db.closeConnection(rs);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
