package storeOnline;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class ActivityLog {

	private final SimpleIntegerProperty userId;
	private final SimpleStringProperty activity;
	private final SimpleStringProperty timestamp;

	// Constructor
	public ActivityLog(int userId, String activity, String timestamp) {
		this.userId = new SimpleIntegerProperty(userId);
		this.activity = new SimpleStringProperty(activity);
		this.timestamp = new SimpleStringProperty(timestamp);
	}

	// Getters
	public int getUserId() {
		return userId.get();
	}

	public String getActivity() {
		return activity.get();
	}

	public String getTimestamp() {
		return timestamp.get();
	}

	// Setters
	public void setUserId(int userId) {
		this.userId.set(userId);
	}

	public void setActivity(String activity) {
		this.activity.set(activity);
	}

	public void setTimestamp(String timestamp) {
		this.timestamp.set(timestamp);
	}

	// Properties for TableView bindings
	public SimpleIntegerProperty userIdProperty() {
		return userId;
	}

	public SimpleStringProperty activityProperty() {
		return activity;
	}

	public SimpleStringProperty timestampProperty() {
		return timestamp;
	}
}