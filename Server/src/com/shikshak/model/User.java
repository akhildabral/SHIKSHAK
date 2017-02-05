package com.shikshak.model;

import java.io.Serializable;
import org.json.simple.JSONObject;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	private int id;
	private String firstName;
	private String lastName;
	private String password;
	private String phone;
	private String account;
	private String picture;
	private String email;

	public User() {
		super();
	}

	public User(String firstName, String lastName, String password, String phone, String account, String picture) {
		super();
		this.firstName = firstName;
		this.lastName = lastName;
		this.password = password;
		this.phone = phone;
		this.account = account;
		this.picture = picture;
	}

	public int getId() {
		return id;
	}

	@XmlElement
	public void setId(int id) {
		this.id = id;
	}

	public String getfirstName() {
		return firstName;
	}

	@XmlElement
	public void setfirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getlastName() {
		return lastName;
	}

	@XmlElement
	public void setlastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPassword() {
		return password;
	}

	@XmlElement
	public void setPassword(String password) {
		this.password = password;
	}

	public String getPhone() {
		return phone;
	}

	@XmlElement
	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAccount() {
		return account;
	}

	@XmlElement
	public void setAccount(String account) {
		this.account = account;
	}

	public String getPicture() {
		return picture;
	}

	@XmlElement
	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getEmail() {
		return email;
	}

	@XmlElement
	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		JSONObject json = new JSONObject();
		json.put("id", id);
		json.put("firstName", firstName);
		json.put("lastName", lastName);
		json.put("password", password);
		json.put("phone", phone);
		json.put("account", account);
		json.put("picture", picture);
		json.put("email", email);
		return json.toJSONString();
	}
}