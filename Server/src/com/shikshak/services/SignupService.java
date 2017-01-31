package com.shikshak.services;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

import org.json.simple.JSONObject;

import com.shikshak.database.DatabaseConnection;
import com.shikshak.model.User;

@Path("/Signup")
public class SignupService {

	DatabaseConnection dbobj = new DatabaseConnection();
	JSONObject resp = new JSONObject();

	@PUT
	@Path("/validate")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response userValidation(User userData) {
		try {
			int flag = 0;
			String emailRcvd = "";

			final ObjectNode node = new ObjectMapper().readValue(userData.toString(), ObjectNode.class);

			if (node.has("email")) {
				emailRcvd = node.get("email").toString().replace("\"", "").trim();
			}

			ResultSet rs = dbobj.openConnection("select email from user");
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
			}
			dbobj.closeConnection(rs);
		} catch (SQLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resp.put("response", "true");
		return Response.ok(resp.toJSONString()).build();
	}
}