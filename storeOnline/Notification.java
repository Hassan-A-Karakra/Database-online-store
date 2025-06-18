package storeOnline;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;

public class Notification {
	private final IntegerProperty id;
	private final StringProperty message;
	private final StringProperty timestamp;

	public Notification(int id, String message, String timestamp) {
		this.id = new SimpleIntegerProperty(id);
		this.message = new SimpleStringProperty(message);
		this.timestamp = new SimpleStringProperty(timestamp);
	}

	public int getId() {
		return id.get();
	}

	public void setId(int id) {
		this.id.set(id);
	}

	public String getMessage() {
		return message.get();
	}

	public void setMessage(String message) {
		this.message.set(message);
	}

	public String getTimestamp() {
		return timestamp.get();
	}

	public void setTimestamp(String timestamp) {
		this.timestamp.set(timestamp);
	}

	public IntegerProperty idProperty() {
		return id;
	}

	public StringProperty messageProperty() {
		return message;
	}

	public StringProperty timestampProperty() {
		return timestamp;
	}
}