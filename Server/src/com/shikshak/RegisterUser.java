package com.shikshak;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;

import org.json.simple.JSONObject;

import com.shikshak.model.User;

@Path("/Register")
public class RegisterUser {

   @GET
   @Path("/getUser")
   @Produces(MediaType.APPLICATION_JSON)
   public Response getUser(){
	   //Get user from Database
	   JSONObject resp = new JSONObject();
	   resp.put("id", "123");
	   resp.put("name", "existing user name");
	   //Return user information
      return Response.ok(resp.toJSONString()).build();
   }
   
//   @GET
//   @Path("/getAllUsers")
//   @Produces(MediaType.APPLICATION_JSON)
//   public List<User> getUsers(){
//      return userDao.getAllUsers();
//   }
//   
   @PUT
   @Path("/addUser")
   @Produces(MediaType.APPLICATION_JSON)
   @Consumes(MediaType.APPLICATION_JSON)
   public Response addUser(User userData){
	   //Add New Use To database...
      System.out.println(userData); 
      
      //Create response object
      JSONObject resp = new JSONObject();
	   resp.put("success", "true");
      //Return response 
      return Response.ok(resp.toJSONString()).build();
   }
//  
}