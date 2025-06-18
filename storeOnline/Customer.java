package storeOnline;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Customer {

	private final IntegerProperty customerID;
	private final StringProperty firstName;
	private final StringProperty lastName;
	private final StringProperty email;

	public Customer(int customerID, String firstName, String lastName, String email) {
		this.customerID = new SimpleIntegerProperty(customerID);
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.email = new SimpleStringProperty(email);
	}

	public int getCustomerID() {
		return customerID.get();
	}

	public IntegerProperty customerIDProperty() {
		return customerID;
	}

	public String getFirstName() {
		return firstName.get();
	}

	public StringProperty firstNameProperty() {
		return firstName;
	}

	public String getLastName() {
		return lastName.get();
	}

	public StringProperty lastNameProperty() {
		return lastName;
	}

	public String getEmail() {
		return email.get();
	}

	public StringProperty emailProperty() {
		return email;
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
}
