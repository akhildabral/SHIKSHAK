package com.shikshak.services;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import com.shikshak.database.DatabaseConnection;
import com.shikshak.model.User;

@Path("/Signup")
public class SignupService {

	DatabaseConnection dbobj = new DatabaseConnection();
	Statement stmt = null;
	ResultSet rs = null;
	JSONObject resp = new JSONObject();

	@PUT
	@Path("/validate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response userValidation(User userData) {
		try {
			System.out.println("......................");
			int flag = 0;
			String emailRcvd = "";

			JSONObject jsonObj = (JSONObject) JSONValue.parse(userData.toString());

			if (jsonObj.containsKey("email")) {
				emailRcvd = jsonObj.get("email").toString().replace("\"", "").trim();
			}

			stmt = dbobj.openConnection();
			rs = stmt.executeQuery("select email from user");
			while (rs.next()) {
				String userEmail = rs.getString("email");
				if (emailRcvd.equals(userEmail)) {
					flag = 1;
					break;
				}
			}
			if (flag == 1) {
				resp.put("response", "false");
				return Response.ok(resp.toJSONString()).build();
			} else {
				resp.put("response", "true");
				return Response.ok(resp.toJSONString()).build();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				rs.close();
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dbobj.closeConnection();
		}
		return null;
	}

	@PUT
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response userRegistration(User userData) {
		try {
			int flag = 0;
			String emailRcvd = "";
			String firstNameRcvd = "";
			String lastNameRcvd = "";
			String passwordRcvd = "";
			String phoneRcvd = "";
			String accountRcvd = "";
			String pictureRcvd = "";

			JSONObject jsonObj = (JSONObject) JSONValue.parse(userData.toString());

			if (jsonObj.containsKey("email")) {
				
				emailRcvd = jsonObj.get("email").toString().replace("\"", "").trim();
				firstNameRcvd = jsonObj.get("firstName").toString();
				lastNameRcvd = jsonObj.get("lastName").toString();
				passwordRcvd = jsonObj.get("password").toString().replace("\"", "").trim();
				phoneRcvd = jsonObj.get("phone").toString().replace("\"", "").trim();
				accountRcvd = jsonObj.get("account").toString().replace("\"", "").trim();
				pictureRcvd = jsonObj.get("picture").toString().replace("\"", "").trim();
				
				stmt = dbobj.openConnection();
				String query = "INSERT INTO user (fname, lname, PASSWORD, phone, ACCOUNT, PICTURE, EMAIL) VALUES ('" +firstNameRcvd +"', '" + lastNameRcvd +"', '" + passwordRcvd +"', '" + phoneRcvd +"', '" + accountRcvd +"', '" + pictureRcvd +"', '" + emailRcvd +"')";
				stmt.executeUpdate(query);
				stmt.close();
			}
			resp.put("response", accountRcvd);
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			dbobj.closeConnection();
		}
		return Response.ok(resp.toJSONString()).build();
	}
}