package com.shikshak;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.json.simple.JSONObject;
@XmlRootElement
public class User implements Serializable {

   private static final long serialVersionUID = 1L;
   private int id;
   private String name;

   public User(){}
   
   public User(int id, String name){
      this.id = id;
      this.name = name;
   }

   public int getId() {
      return id;
   }

   @XmlElement
   public void setId(int id) {
      this.id = id;
   }
   public String getName() {
      return name;
   }
   @XmlElement
   public void setName(String name) {
      this.name = name;
   }
   
   @Override
   public String toString() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("name",name);
		return json.toJSONString();
	}
  	
}