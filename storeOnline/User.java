package storeOnline;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class User {
	private final IntegerProperty id;
	private final StringProperty name;
	private final StringProperty email;
	private final StringProperty password;
	private final StringProperty role;
	private SimpleStringProperty username;

	public User(int id, String name, String email, String password, String role) {
		this.id = new SimpleIntegerProperty(id);
		this.name = new SimpleStringProperty(name);
		this.email = new SimpleStringProperty(email);
		this.password = new SimpleStringProperty(password);
		this.role = new SimpleStringProperty(role);
	}

	public User(int userId, String username, String email, String role) {
		this.id = new SimpleIntegerProperty(userId);
		this.username = new SimpleStringProperty(username);
		this.name = new SimpleStringProperty();
		this.email = new SimpleStringProperty(email);
		this.password = new SimpleStringProperty(""); // Set to empty or handle it appropriately
		this.role = new SimpleStringProperty(role);
	}

	public String getPassword() {
		return password.get();
	}

	public void setPassword(String password) {
		this.password.set(password);
	}

	public StringProperty passwordProperty() {
		return password;
	}

	// Getters
	public int getId() {
		return id.get();
	}

	public String getName() {
		return name.get();
	}

	public String getEmail() {
		return email.get();
	}

	public String getRole() {
		return role.get();
	}

	// Setters
	public void setId(int id) {
		this.id.set(id);
	}

	public void setName(String name) {
		this.name.set(name);
	}

	public void setEmail(String email) {
		this.email.set(email);
	}

	public StringProperty usernameProperty() {
		return username;
	}

	public void setRole(String role) {
		this.role.set(role);
	}

	// Properties
	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty nameProperty() {
		return name;
	}

	public StringProperty emailProperty() {
		return email;
	}

	public StringProperty roleProperty() {
		return role;
	}

	public String getUsername() {
		return username.get();
	}

}