package storeOnline;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.sql.*;
import java.time.LocalDate;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.stage.StageStyle;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class Main extends Application {

	private static final String URL = "jdbc:mysql://localhost:3306/OnlineStore";
	private static final String USER = "root";
	private static final String PASSWORD = "0595878691";

	private User currentUser;

	TableView<Order> orderTable = new TableView<>();
	ObservableList<Order> orderData = FXCollections.observableArrayList();
	TableView<PurchaseAndSales> purchasesTable = new TableView<>();
	ObservableList<PurchaseAndSales> purchasesData = FXCollections.observableArrayList();
	private ObservableList<PurchaseAndSales> transactionsData = FXCollections.observableArrayList();
	PieChart topProductsChart = new PieChart();
	LineChart<String, Number> salesTrendChart = new LineChart<>(new CategoryAxis(), new NumberAxis());
	ListView<String> notificationsList = new ListView<>();

	private ResourceBundle bundle;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {

		setLanguage("en");
		showWelcomeScreen(primaryStage);

	}

	private void showWelcomeScreen(Stage primaryStage) {

		VBox welcomePane = new VBox(20);
		welcomePane.setAlignment(Pos.CENTER);
		welcomePane.setPadding(new Insets(20));

		String imagePath = "file:///C:/Users/coolnet/eclipse-workspace/onlineStore/src/storeOnline/Screenshot%202024-12-30%20133205.png";

		welcomePane.setStyle("-fx-background-image: url('" + imagePath + "'); " + "-fx-background-size: cover; "
				+ "-fx-background-position: center center;");

		Label welcomeLabel = new Label(bundle.getString("welcome"));
		welcomeLabel.setStyle("-fx-font-size: 28px; " + "-fx-font-weight: bold; " + "-fx-font-style: italic; "
				+ "-fx-text-fill: white; " + "-fx-background-color: rgba(0, 0, 0, 0.5); " + "-fx-padding: 10px; "
				+ "-fx-background-radius: 10;");

		Button enterButton = new Button(bundle.getString("enter"));
		enterButton.setStyle("-fx-font-size: 18px; " + "-fx-font-weight: bold; " + "-fx-background-color: #4CAF50; "
				+ "-fx-text-fill: white; " + "-fx-background-radius: 20;");
		enterButton.setOnAction(e -> showLoginScreen(primaryStage));

		welcomePane.getChildren().addAll(welcomeLabel, enterButton);

		Scene welcomeScene = new Scene(welcomePane, 800, 500);
		primaryStage.setScene(welcomeScene);
		primaryStage.setTitle(bundle.getString("welcome_title"));
		primaryStage.show();

	}

	private void showLoginScreen(Stage primaryStage) {

		GridPane loginPane = new GridPane();
		loginPane.setAlignment(Pos.CENTER);
		loginPane.setPadding(new Insets(20));
		loginPane.setHgap(10);
		loginPane.setVgap(15);

		String imagePath = "file:///C:/Users/coolnet/eclipse-workspace/onlineStore/src/storeOnline/Screenshot%202024-12-30%20140116.png";
		loginPane.setStyle("-fx-background-image: url('" + imagePath + "');" + "-fx-background-size: cover;"
				+ "-fx-background-position: center center;" + "-fx-background-repeat: no-repeat;");

		HBox usernameBox = new HBox(10);
		usernameBox.setAlignment(Pos.CENTER_LEFT);
		Label usernameIcon = new Label("\uD83D\uDC64");
		usernameIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
		TextField usernameField = new TextField();
		usernameField.setPromptText("Username");
		usernameField.setStyle("-fx-background-color: rgba(255, 255, 255, 1);" + "-fx-background-radius: 15;"
				+ "-fx-border-radius: 15;" + "-fx-padding: 10;");
		usernameBox.getChildren().addAll(usernameIcon, usernameField);

		HBox passwordBox = new HBox(10);
		passwordBox.setAlignment(Pos.CENTER_LEFT);
		Label passwordIcon = new Label("\uD83D\uDD12");
		passwordIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
		PasswordField passwordField = new PasswordField();
		passwordField.setPromptText("Password");
		passwordField.setStyle("-fx-background-color: rgba(255, 255, 255, 1);" + "-fx-background-radius: 15;"
				+ "-fx-border-radius: 15;" + "-fx-padding: 10;");
		passwordBox.getChildren().addAll(passwordIcon, passwordField);

		Button loginButton = new Button("Login");
		loginButton.setStyle("-fx-background-color: #4CAF50;" + "-fx-text-fill: white;" + "-fx-font-size: 18px;"
				+ "-fx-background-radius: 15;" + "-fx-padding: 10;");
		loginButton.setOnAction(e -> {
			String username = usernameField.getText();
			String password = passwordField.getText();
			if (validateLogin(username, password)) {
				showMainSystemScreen(primaryStage);
			} else {
				showWarning("Login Failed. Please try again.");
			}
		});

		loginPane.add(usernameBox, 0, 0);
		loginPane.add(passwordBox, 0, 1);
		loginPane.add(loginButton, 0, 2);

		Scene loginScene = new Scene(loginPane, 800, 500);
		primaryStage.setScene(loginScene);
		primaryStage.setTitle("User Login");
		primaryStage.show();

	}

	private boolean validateLogin(String username, String password) {
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection
						.prepareStatement("SELECT * FROM Users WHERE Username = ? AND PasswordHash = SHA2(?, 256)")) {
			statement.setString(1, username);
			statement.setString(2, password);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				int userId = resultSet.getInt("UserID");
				String role = resultSet.getString("Role");
				String email = resultSet.getString("Email");
				currentUser = new User(userId, username, email, role);
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void showMainSystemScreen(Stage primaryStage) {

		TabPane tabPane = new TabPane();

		tabPane.getTabs().add(createDashboardTab());
		tabPane.getTabs().add(createUserManagementTab());
		tabPane.getTabs().add(createProductManagementTab());
		tabPane.getTabs().add(createOrderManagementTab());
		tabPane.getTabs().add(createSalesAndPurchasesTab());
		tabPane.getTabs().add(createReportsTab());

		Menu languageMenu = new Menu(bundle.getString("language"));

		MenuItem englishMenuItem = new MenuItem("English");
		englishMenuItem.setOnAction(e -> {
			setLanguage("en");
		});

		MenuItem arabicMenuItem = new MenuItem("العربية");
		arabicMenuItem.setOnAction(e -> {
			setLanguage("ar");
		});

		languageMenu.getItems().addAll(englishMenuItem, arabicMenuItem);
		MenuBar menuBar = new MenuBar(languageMenu);

		VBox layout = new VBox(menuBar, tabPane);
		Scene mainScene = new Scene(layout, 1000, 600);

		primaryStage.setScene(mainScene);
		primaryStage.setTitle(bundle.getString("main_system"));
		primaryStage.show();

	}

	private void showNotification(String message) {
		Stage notificationStage = new Stage();
		notificationStage.initStyle(StageStyle.UNDECORATED);
		notificationStage.setAlwaysOnTop(true);

		Label notificationLabel = new Label(message);
		notificationLabel.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white; -fx-font-size: 16px; "
				+ "-fx-padding: 10px; -fx-background-radius: 5;");

		StackPane notificationPane = new StackPane(notificationLabel);
		notificationPane.setStyle("-fx-background-color: rgba(0, 0, 0, 0.7); -fx-padding: 10px;");
		notificationPane.setPadding(new Insets(10));

		Scene scene = new Scene(notificationPane);
		notificationStage.setScene(scene);

		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		notificationStage.setX((screenBounds.getWidth() - 300) / 2);
		notificationStage.setY((screenBounds.getHeight() - 100) / 2);

		notificationStage.show();

		PauseTransition delay = new PauseTransition();
		delay.setOnFinished(e -> notificationStage.close());
		delay.play();

	}

	/// ---------------------------- Dashboard Tab ------------------------------
	private Tab createDashboardTab() {
		Tab dashboardTab = new Tab("Dashboard");
		dashboardTab.setClosable(false);

		BorderPane mainLayout = new BorderPane();

		// KPIs Section
		HBox kpiBox = new HBox(20);
		kpiBox.setPadding(new Insets(20));
		kpiBox.setAlignment(Pos.CENTER);

		Label totalSalesLabel = new Label("Total Sales: $0");
		Label totalOrdersLabel = new Label("Total Orders: 0");
		Label totalCustomersLabel = new Label("Total Customers: 0");
		Label lowStockLabel = new Label("Low Stock Products: 0");

		totalSalesLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2ecc71;");
		totalOrdersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #3498db;");
		totalCustomersLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #9b59b6;");
		lowStockLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

		// Refresh Button
		Button refreshButton = new Button("Refresh");
		refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-size: 14px;");
		refreshButton.setOnAction(e -> {
			// Update all components dynamically when the button is clicked
			updateKPIs(totalSalesLabel, totalOrdersLabel, totalCustomersLabel, lowStockLabel);
			loadTopProductsData(topProductsChart);
			salesTrendChart.getData().clear();
			loadSalesOverTime(salesTrendChart);
			loadNotificationsData(notificationsList);
		});

		kpiBox.getChildren().addAll(totalSalesLabel, totalOrdersLabel, totalCustomersLabel, lowStockLabel,
				refreshButton);
		mainLayout.setTop(kpiBox);

		// Charts Section
		VBox chartBox = new VBox(20);
		chartBox.setPadding(new Insets(20));
		chartBox.setAlignment(Pos.CENTER);

		// Top Products Pie Chart
		PieChart topProductsChart = new PieChart();
		topProductsChart.setTitle("Top Selling Products");

		// Sales Over Time Line Chart
		LineChart<String, Number> salesTrendChart = createSalesLineChart();

		// Load data for the charts
		loadTopProductsData(topProductsChart);
		loadSalesOverTime(salesTrendChart);

		chartBox.getChildren().addAll(topProductsChart, salesTrendChart);
		mainLayout.setCenter(chartBox);

		// Notifications Section
		VBox notificationsBox = new VBox(10);
		notificationsBox.setPadding(new Insets(20));
		notificationsBox.setAlignment(Pos.TOP_LEFT);
		notificationsBox.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #dcdcdc; -fx-border-width: 1;");

		Label notificationsTitle = new Label("Notifications");
		notificationsTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

		ListView<String> notificationsList = new ListView<>();
		notificationsList.setPrefHeight(150);

		// Load notifications data
		loadNotificationsData(notificationsList);

		notificationsBox.getChildren().addAll(notificationsTitle, notificationsList);
		mainLayout.setBottom(notificationsBox);

		// Initial data load for KPIs
		updateKPIs(totalSalesLabel, totalOrdersLabel, totalCustomersLabel, lowStockLabel);

		dashboardTab.setContent(mainLayout);
		return dashboardTab;
	}

	private void updateKPIs(Label totalSalesLabel, Label totalOrdersLabel, Label totalCustomersLabel,
			Label lowStockLabel) {
		String query = "SELECT " + "(SELECT SUM(TotalPrice) FROM Orders) AS TotalSales, "
				+ "(SELECT COUNT(*) FROM Orders) AS TotalOrders, "
				+ "(SELECT COUNT(*) FROM Customers) AS TotalCustomers, "
				+ "(SELECT COUNT(*) FROM Products WHERE StockLevel < LowStockThreshold) AS LowStockProducts";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			if (rs.next()) {
				totalSalesLabel.setText("Total Sales: $" + rs.getDouble("TotalSales"));
				totalOrdersLabel.setText("Total Orders: " + rs.getInt("TotalOrders"));
				totalCustomersLabel.setText("Total Customers: " + rs.getInt("TotalCustomers"));
				lowStockLabel.setText("Low Stock Products: " + rs.getInt("LowStockProducts"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadTopProductsData(PieChart chart) {
		String query = "SELECT Name, SUM(Quantity) AS TotalSold FROM OrderProducts "
				+ "JOIN Products ON OrderProducts.ProductID = Products.ProductID "
				+ "GROUP BY Name ORDER BY TotalSold DESC LIMIT 5";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			ObservableList<PieChart.Data> data = FXCollections.observableArrayList();
			while (rs.next()) {
				data.add(new PieChart.Data(rs.getString("Name"), rs.getInt("TotalSold")));
			}
			chart.setData(data);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadSalesOverTime(LineChart<String, Number> lineChart) {
		XYChart.Series<String, Number> series = new XYChart.Series<>();
		series.setName("Sales Trend");

		String query = "SELECT DATE(OrderDate) AS SalesDate, SUM(TotalPrice) AS TotalSales "
				+ "FROM Orders GROUP BY SalesDate ORDER BY SalesDate";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				series.getData().add(new XYChart.Data<>(rs.getString("SalesDate"), rs.getDouble("TotalSales")));
			}
			lineChart.getData().add(series);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadNotificationsData(ListView<String> listView) {
		String query = "SELECT Title FROM Notifications ORDER BY Timestamp DESC LIMIT 5";

		try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			ObservableList<String> notifications = FXCollections.observableArrayList();
			while (rs.next()) {
				notifications.add(rs.getString("Title"));
			}
			listView.setItems(notifications);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private LineChart<String, Number> createSalesLineChart() {
		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setLabel("Date");

		NumberAxis yAxis = new NumberAxis();
		yAxis.setLabel("Sales");

		LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
		lineChart.setTitle("Sales Over Time");

		return lineChart;
	}

	/// ----------------------- User Management Tab ------------------------------

	private Tab createUserManagementTab() {

		Tab tab = new Tab("User Management");
		tab.setClosable(false);

		BorderPane userManagementPane = new BorderPane();

		String imagePath = "file:///C:/Users/coolnet/eclipse-workspace/onlineStore/src/storeOnline/Screenshot%202024-12-30%20143730.png";
		userManagementPane.setStyle("-fx-background-image: url('" + imagePath + "');" + "-fx-background-size: cover;"
				+ "-fx-background-position: center center;" + "-fx-background-repeat: no-repeat;");

		TableView<User> userTable = new TableView<>();
		ObservableList<User> userData = FXCollections.observableArrayList();
		initializeUserTable(userTable);
		loadUserData(userData, userTable);

		Button addButton = new Button("Add User");
		addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

		Button updateButton = new Button("Update User");
		updateButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");

		Button deleteButton = new Button("Delete User");
		deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

		Button searchButton = new Button("Search User");
		searchButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");

		GridPane dynamicPane = new GridPane();
		dynamicPane.setPadding(new Insets(10));
		dynamicPane.setVgap(10);
		dynamicPane.setHgap(10);
		dynamicPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);" + "-fx-border-color: #d3d3d3;"
				+ "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
		dynamicPane.setAlignment(Pos.CENTER);

		addButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label nameLabel = new Label("Name:");
			TextField nameField = new TextField();

			Label emailLabel = new Label("Email:");
			TextField emailField = new TextField();

			Label roleLabel = new Label("Role:");
			ComboBox<String> roleComboBox = new ComboBox<>();
			roleComboBox.getItems().addAll("Admin", "Manager", "Employee");

			Button saveButton = new Button("Add");
			saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
			saveButton.setOnAction(saveEvent -> {

				String name = nameField.getText().trim();
				String email = emailField.getText().trim();
				String role = roleComboBox.getValue();

				if (name.isEmpty() || !name.matches("^[a-zA-Z0-9]+$")) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Name can only contain letters and numbers.");
					return;
				}

				if (email.isEmpty() || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$")) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid email address.");
					return;
				}

				if (role == null || role.isEmpty()) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please select a role.");
					return;
				}

				try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement statement = connection.prepareStatement(
								"INSERT INTO Users (Username, Email, PasswordHash, Role) VALUES (?, ?, SHA2(?, 256), ?)",
								Statement.RETURN_GENERATED_KEYS)) {

					statement.setString(1, nameField.getText());
					statement.setString(2, emailField.getText());
					statement.setString(3, "defaultPassword");
					statement.setString(4, roleComboBox.getValue());

					statement.executeUpdate();

					ResultSet generatedKeys = statement.getGeneratedKeys();
					if (generatedKeys.next()) {
						int generatedId = generatedKeys.getInt(1);

						userData.add(new User(generatedId, nameField.getText(), emailField.getText(),
								roleComboBox.getValue()));
						userTable.refresh();

					}
				} catch (SQLException ex) {
					ex.printStackTrace();
				}

				showNotification("User added successfully!");

			});

			dynamicPane.add(nameLabel, 0, 0);
			dynamicPane.add(nameField, 1, 0);
			dynamicPane.add(emailLabel, 0, 1);
			dynamicPane.add(emailField, 1, 1);
			dynamicPane.add(roleLabel, 0, 2);
			dynamicPane.add(roleComboBox, 1, 2);
			dynamicPane.add(saveButton, 1, 3);
		});

		updateButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			// Labels and Input Fields
			Label lblUserId = new Label("User ID:");
			TextField tfUserId = new TextField();

			Label lblName = new Label("Name:");
			TextField tfName = new TextField();
			tfName.setDisable(true);

			Label lblEmail = new Label("Email:");
			TextField tfEmail = new TextField();
			tfEmail.setDisable(true);

			Label lblRole = new Label("Role:");
			ComboBox<String> cbRole = new ComboBox<>();
			cbRole.getItems().addAll("Admin", "Manager", "Employee");
			cbRole.setDisable(true);

			// Buttons
			Button btnFind = new Button("Find");
			btnFind.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");
			Button btnUpdate = new Button("Update");
			btnUpdate.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
			btnUpdate.setDisable(true);

			btnFind.setOnAction(findEvent -> {
				String userIdInput = tfUserId.getText().trim();

				if (userIdInput.isEmpty()) {
					showWarning("User ID is required to find a user.");
					return;
				}

				try {
					int userId = Integer.parseInt(userIdInput);

					User foundUser = userData.stream().filter(user -> user.getId() == userId).findFirst().orElse(null);

					if (foundUser != null) {
						tfName.setText(foundUser.getUsername());
						tfEmail.setText(foundUser.getEmail());
						cbRole.setValue(foundUser.getRole());

						tfName.setDisable(false);
						tfEmail.setDisable(false);
						cbRole.setDisable(false);
						btnUpdate.setDisable(false);

						showAlert(Alert.AlertType.INFORMATION, "User Found",
								"User found. You can now update the details.");
					} else {
						showAlert(Alert.AlertType.WARNING, "Not Found", "No user found with the given ID.");
						tfName.clear();
						tfEmail.clear();
						cbRole.getSelectionModel().clearSelection();

						tfName.setDisable(true);
						tfEmail.setDisable(true);
						cbRole.setDisable(true);
						btnUpdate.setDisable(true);
					}
				} catch (NumberFormatException ex) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid numeric User ID.");
				}
			});

			btnUpdate.setOnAction(updateEvent -> {
				String name = tfName.getText().trim();
				String email = tfEmail.getText().trim();
				String role = cbRole.getValue();

				if (name.isEmpty() || !name.matches("^[a-zA-Z0-9 ]+$")) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input",
							"Name can only contain letters, numbers, and spaces.");
					return;
				}

				if (email.isEmpty() || !email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please enter a valid email address.");
					return;
				}

				if (role == null || role.isEmpty()) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "Please select a role.");
					return;
				}

				try {
					int userId = Integer.parseInt(tfUserId.getText().trim());

					// Update the user in the database
					try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
							PreparedStatement statement = connection.prepareStatement(
									"UPDATE Users SET Username = ?, Email = ?, Role = ? WHERE UserID = ?")) {

						statement.setString(1, name);
						statement.setString(2, email);
						statement.setString(3, role);
						statement.setInt(4, userId);

						int rowsUpdated = statement.executeUpdate();

						if (rowsUpdated > 0) {
							// Update the user in the local ObservableList
							User foundUser = userData.stream().filter(user -> user.getId() == userId).findFirst()
									.orElse(null);

							if (foundUser != null) {
								foundUser.setName(name);
								foundUser.setEmail(email);
								foundUser.setRole(role);
								userTable.refresh();
							}

							showAlert(Alert.AlertType.INFORMATION, "Success", "User details updated successfully.");
						} else {
							showAlert(Alert.AlertType.ERROR, "Error", "Failed to update user in the database.");
						}
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
					showAlert(Alert.AlertType.ERROR, "Database Error", "An error occurred while updating the user.");
				} catch (NumberFormatException ex) {
					showAlert(Alert.AlertType.ERROR, "Invalid Input", "User ID must be a numeric value.");
				}

				showNotification("User updated successfully!");

			});

			// Add components to the dynamic pane
			dynamicPane.add(lblUserId, 0, 0);
			dynamicPane.add(tfUserId, 1, 0);
			dynamicPane.add(btnFind, 2, 0);
			dynamicPane.add(lblName, 0, 1);
			dynamicPane.add(tfName, 1, 1);
			dynamicPane.add(lblEmail, 0, 2);
			dynamicPane.add(tfEmail, 1, 2);
			dynamicPane.add(lblRole, 0, 3);
			dynamicPane.add(cbRole, 1, 3);
			dynamicPane.add(btnUpdate, 1, 4);
		});

		deleteButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label deleteLabel = new Label("Enter User ID to Delete:");
			TextField deleteField = new TextField();
			Button deleteActionButton = new Button("Delete");
			deleteActionButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

			dynamicPane.add(deleteLabel, 0, 0);
			dynamicPane.add(deleteField, 1, 0);
			dynamicPane.add(deleteActionButton, 2, 0);

			deleteActionButton.setOnAction(deleteEvent -> {
				String deleteText = deleteField.getText().trim();

				if (deleteText.isEmpty()) {
					showAlert("Invalid Input", "Please enter a User ID.");
					return;
				}

				try {
					int userId = Integer.parseInt(deleteText);

					final User[] userToDelete = { null };
					for (User user : userData) {
						if (user.getId() == userId) {
							userToDelete[0] = user;
							break;
						}
					}

					if (userToDelete[0] != null) {
						Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
						confirmAlert.setTitle("Confirm Deletion");
						confirmAlert.setHeaderText(null);
						confirmAlert
								.setContentText("Are you sure you want to delete the user with ID: " + userId + "?");

						confirmAlert.showAndWait().ifPresent(response -> {
							if (response == ButtonType.OK) {
								try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
										PreparedStatement statement = connection
												.prepareStatement("DELETE FROM Users WHERE UserID = ?")) {

									statement.setInt(1, userId);
									int rowsDeleted = statement.executeUpdate();

									if (rowsDeleted > 0) {
										userData.remove(userToDelete[0]);
										userTable.refresh();
										showAlert("Success",
												"User with ID " + userId + " has been deleted successfully.");
									} else {
										showAlert("Database Error", "Failed to delete user from the database.");
									}
								} catch (SQLException ex) {
									ex.printStackTrace();
									showAlert("Database Error", "An error occurred while deleting the user.");
								}
							}
						});
					} else {
						showAlert("Not Found", "User not found with ID: " + userId);
					}
				} catch (NumberFormatException ex) {
					showAlert("Invalid Input", "User ID must be a numeric value.");
				}

				showNotification("User deleted successfully!");

			});

		});

		searchButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label searchByLabel = new Label("Search By ID:");
			ComboBox<String> searchCriteriaComboBox = new ComboBox<>();
			searchCriteriaComboBox.getItems().addAll("ID");
			searchCriteriaComboBox.setValue("ID");

			Label searchValueLabel = new Label("Search Value:");
			TextField searchField = new TextField();

			Button searchAction = new Button("Search");
			searchAction.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");

			VBox userInfoPane = new VBox(10);
			userInfoPane.setAlignment(Pos.CENTER);

			searchAction.setOnAction(searchEvent -> {
				String searchCriteria = searchCriteriaComboBox.getValue();
				String searchValue = searchField.getText().trim();

				if (searchValue.isEmpty()) {
					showAlert("Invalid Input", "Please enter a search value.");
					return;
				}

				User foundUser = null;
				if ("ID".equals(searchCriteria)) {
					try {
						int searchId = Integer.parseInt(searchValue);

						for (User user : userData) {
							if (user.getId() == searchId) {
								foundUser = user;
								break;
							}
						}
					} catch (NumberFormatException ex) {
						showAlert("Invalid Input", "Please enter a valid numeric ID.");
						return;
					}
				} else if ("Name".equals(searchCriteria)) {
					if (!searchValue.matches("^[a-zA-Z ]+$")) {
						showAlert("Invalid Input", "Name can only contain letters and spaces.");
						return;
					}

					for (User user : userData) {
						if (user.getName() != null
								&& user.getName().toLowerCase().contains(searchValue.toLowerCase())) {
							foundUser = user;
							break;
						}
					}
				}

				userInfoPane.getChildren().clear();
				if (foundUser != null) {
					Label userIdLabel = new Label("ID: " + foundUser.getId());
					Label userNameLabel = new Label(
							"Name: " + (foundUser.getUsername() != null ? foundUser.getUsername() : "N/A"));
					Label userEmailLabel = new Label("Email: " + foundUser.getEmail());
					Label userRoleLabel = new Label("Role: " + foundUser.getRole());

					userInfoPane.getChildren().addAll(userIdLabel, userNameLabel, userEmailLabel, userRoleLabel);
				} else {
					userInfoPane.getChildren().add(new Label("No user found with the given criteria."));
				}

				showNotification("Search completed successfully!");

			});

			dynamicPane.add(searchByLabel, 0, 0);
			dynamicPane.add(searchCriteriaComboBox, 1, 0);
			dynamicPane.add(searchValueLabel, 0, 1);
			dynamicPane.add(searchField, 1, 1);
			dynamicPane.add(searchAction, 1, 2);
			dynamicPane.add(userInfoPane, 0, 3, 2, 1);

		});

		VBox buttonPane = new VBox(10, addButton, updateButton, deleteButton, searchButton);
		buttonPane.setPadding(new Insets(10));
		buttonPane.setAlignment(Pos.CENTER);

		HBox contentPane = new HBox(20, userTable, buttonPane, dynamicPane);
		contentPane.setPadding(new Insets(20));

		userManagementPane.setCenter(contentPane);

		tab.setContent(userManagementPane);
		return tab;

	}

	private void showAlert(Alert.AlertType alertType, String title, String message) {

		Alert alert = new Alert(alertType);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();

	}

	private void initializeUserTable(TableView<User> userTable) {

		TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> cellData.getValue().idProperty().asObject());
		idColumn.setPrefWidth(50);

		TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
		usernameColumn.setCellValueFactory(cellData -> cellData.getValue().usernameProperty());
		usernameColumn.setPrefWidth(150);

		TableColumn<User, String> emailColumn = new TableColumn<>("Email");
		emailColumn.setCellValueFactory(cellData -> cellData.getValue().emailProperty());
		emailColumn.setPrefWidth(200);

		TableColumn<User, String> roleColumn = new TableColumn<>("Role");
		roleColumn.setCellValueFactory(cellData -> cellData.getValue().roleProperty());
		roleColumn.setPrefWidth(130);

		userTable.getColumns().addAll(idColumn, usernameColumn, emailColumn, roleColumn);

	}

	private void loadUserData(ObservableList<User> userData, TableView<User> userTable) {
		userData.clear();

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT UserID, Username, Email, Role FROM Users")) {

			while (resultSet.next()) {
				int userId = resultSet.getInt("UserID");
				String username = resultSet.getString("Username");
				String email = resultSet.getString("Email");
				String role = resultSet.getString("Role");

				userData.add(new User(userId, username, email, role));
			}

			userTable.setItems(userData);

		} catch (SQLException e) {
			showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load user data: " + e.getMessage());
			e.printStackTrace();
		}
	}

	/// ---------------------- Product Management Tab ------------------------------

	private Tab createProductManagementTab() {

		Tab tab = new Tab("Product Management");
		tab.setClosable(false);

		BorderPane productManagementPane = new BorderPane();

		String imagePath = "file:///C:/Users/coolnet/eclipse-workspace/onlineStore/src/storeOnline/Screenshot%202024-12-30%20143730.png";
		productManagementPane.setStyle("-fx-background-image: url('" + imagePath + "');" + "-fx-background-size: cover;"
				+ "-fx-background-position: center center;" + "-fx-background-repeat: no-repeat;");

		TableView<Product> productTable = new TableView<>();
		ObservableList<Product> productData = FXCollections.observableArrayList();
		initializeProductTable(productTable);
		loadProductData(productData, productTable);

		GridPane dynamicPane = new GridPane();
		dynamicPane.setPadding(new Insets(10));
		dynamicPane.setVgap(10);
		dynamicPane.setHgap(10);
		dynamicPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);" + "-fx-border-color: #d3d3d3;"
				+ "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
		dynamicPane.setAlignment(Pos.CENTER);

		Button addButton = new Button("Add Product");
		addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		addButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label nameLabel = new Label("Name:");
			TextField nameField = new TextField();

			Label priceLabel = new Label("Price:");
			TextField priceField = new TextField();

			Label categoryLabel = new Label("Category:");
			ComboBox<String> categoryComboBox = new ComboBox<>();
			categoryComboBox.getItems().addAll("Electronics", "Clothing", "Home Appliances");

			Button saveButton = new Button("Add");
			saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
			saveButton.setOnAction(saveEvent -> {
				String name = nameField.getText().trim();
				String category = categoryComboBox.getValue();
				String priceText = priceField.getText().trim();

				if (name.isEmpty() || !name.matches("^[a-zA-Z0-9 ]+$")) {
					showAlert("Invalid Input", "Name can only contain letters, numbers, and spaces.");
					return;
				}

				if (category == null || category.isEmpty()) {
					showAlert("Invalid Input", "Please select a category.");
					return;
				}

				double price;
				try {
					price = Double.parseDouble(priceText);
					if (price < 0) {
						showAlert("Invalid Input", "Price cannot be negative.");
						return;
					}
				} catch (NumberFormatException ex) {
					showAlert("Invalid Input", "Price must be a valid number.");
					return;
				}

				try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement statement = connection.prepareStatement(
								"INSERT INTO Products (productId, name, category, price, stockLevel) VALUES (?, ?, ?, ?, ?)")) {

					int newProductId = generateProductIdFromDatabase();

					statement.setInt(1, newProductId);
					statement.setString(2, name);
					statement.setString(3, category);
					statement.setDouble(4, price);
					statement.setInt(5, 0);

					statement.executeUpdate();

					Product newProduct = new Product(newProductId, name, category, price, 0);
					productData.add(newProduct);
					productTable.refresh();

					showAlert("Success", "Product added successfully!");

				} catch (SQLException ex) {
					ex.printStackTrace();
					showAlert("Database Error", "Failed to add product to the database!");
				}
			});

			dynamicPane.add(nameLabel, 0, 0);
			dynamicPane.add(nameField, 1, 0);
			dynamicPane.add(priceLabel, 0, 1);
			dynamicPane.add(priceField, 1, 1);
			dynamicPane.add(categoryLabel, 0, 2);
			dynamicPane.add(categoryComboBox, 1, 2);
			dynamicPane.add(saveButton, 1, 3);
		});

		Button updateButton = new Button("Update Product");
		updateButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
		updateButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label idLabel = new Label("Product ID:");
			TextField idField = new TextField();

			Label nameLabel = new Label("Name:");
			TextField nameField = new TextField();

			Label priceLabel = new Label("Price:");
			TextField priceField = new TextField();

			Label categoryLabel = new Label("Category:");
			ComboBox<String> categoryComboBox = new ComboBox<>();
			categoryComboBox.getItems().addAll("Electronics", "Clothing", "Home Appliances");

			Button findButton = new Button("Find");
			Button updateSaveButton = new Button("Update");
			updateSaveButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
			updateSaveButton.setDisable(true);

			findButton.setOnAction(findEvent -> {
				try {
					int productId = Integer.parseInt(idField.getText());
					Product foundProduct = findProductById(productId, productData);
					if (foundProduct != null) {
						nameField.setText(foundProduct.getProductName());
						priceField.setText(String.valueOf(foundProduct.getPrice()));
						categoryComboBox.setValue(foundProduct.getCategory());
						updateSaveButton.setDisable(false);
					} else {
						showWarning("Product not found!");
					}
				} catch (NumberFormatException ex) {
					showWarning("Invalid Product ID!");
				}
			});

			updateSaveButton.setOnAction(updateEvent -> {
				String newName = nameField.getText().trim();
				String newCategory = categoryComboBox.getValue();
				String priceText = priceField.getText().trim();

				if (newName.isEmpty() || !newName.matches("^[a-zA-Z0-9 ]+$")) {
					showAlert("Invalid Input", "Name can only contain letters, numbers, and spaces.");
					return;
				}

				if (newCategory == null || newCategory.isEmpty()) {
					showAlert("Invalid Input", "Please select a category.");
					return;
				}

				double newPrice;
				try {
					newPrice = Double.parseDouble(priceText);
					if (newPrice < 0) {
						showAlert("Invalid Input", "Price cannot be negative.");
						return;
					}
				} catch (NumberFormatException ex) {
					showAlert("Invalid Input", "Price must be a valid number.");
					return;
				}

				try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement statement = connection.prepareStatement(
								"UPDATE Products SET name = ?, category = ?, price = ? WHERE productId = ?")) {

					int productId = Integer.parseInt(idField.getText());

					statement.setString(1, newName);
					statement.setString(2, newCategory);
					statement.setDouble(3, newPrice);
					statement.setInt(4, productId);

					int rowsUpdated = statement.executeUpdate();

					if (rowsUpdated > 0) {

						Product foundProduct = findProductById(productId, productData);
						if (foundProduct != null) {
							foundProduct.setProductName(newName);
							foundProduct.setCategory(newCategory);
							foundProduct.setPrice(newPrice);
							productTable.refresh();
						}

						showAlert("Success", "Product updated successfully!");
					} else {
						showWarning("Failed to update product in database!");
					}
				} catch (NumberFormatException ex) {
					showAlert("Invalid Product ID!", "Please enter a valid numeric Product ID.");
				} catch (SQLException ex) {
					ex.printStackTrace();
					showAlert("Database Error", "An error occurred while updating the product.");
				}
			});

			dynamicPane.add(idLabel, 0, 0);
			dynamicPane.add(idField, 1, 0);
			dynamicPane.add(findButton, 2, 0);
			dynamicPane.add(nameLabel, 0, 1);
			dynamicPane.add(nameField, 1, 1);
			dynamicPane.add(priceLabel, 0, 2);
			dynamicPane.add(priceField, 1, 2);
			dynamicPane.add(categoryLabel, 0, 3);
			dynamicPane.add(categoryComboBox, 1, 3);
			dynamicPane.add(updateSaveButton, 1, 4);
		});

		Button deleteButton = new Button("Delete Product");
		deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");
		deleteButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label deleteLabel = new Label("Enter Product ID to Delete:");
			TextField deleteField = new TextField();
			Button deleteActionButton = new Button("Delete");
			deleteActionButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

			dynamicPane.add(deleteLabel, 0, 0);
			dynamicPane.add(deleteField, 1, 0);
			dynamicPane.add(deleteActionButton, 2, 0);

			deleteActionButton.setOnAction(deleteEvent -> {
				String deleteText = deleteField.getText().trim();

				if (deleteText.isEmpty()) {
					showAlert("Invalid Input", "Please enter a Product ID.");
					return;
				}

				try {
					int productId = Integer.parseInt(deleteText);

					Product productToDelete = findProductById(productId, productData);

					if (productToDelete != null) {

						Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
						confirmAlert.setTitle("Confirm Deletion");
						confirmAlert.setHeaderText(null);
						confirmAlert.setContentText(
								"Are you sure you want to delete the product with ID: " + productId + "?");

						confirmAlert.showAndWait().ifPresent(response -> {
							if (response == ButtonType.OK) {
								try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
										PreparedStatement statement = connection
												.prepareStatement("DELETE FROM Products WHERE productId = ?")) {

									statement.setInt(1, productId);
									int rowsDeleted = statement.executeUpdate();

									if (rowsDeleted > 0) {
										productData.remove(productToDelete);
										productTable.refresh();
										showAlert("Success",
												"Product with ID " + productId + " has been deleted successfully.");
									} else {
										showAlert("Database Error", "Failed to delete product from the database.");
									}
								} catch (SQLException ex) {
									ex.printStackTrace();
									showAlert("Database Error", "An error occurred while deleting the product.");
								}
							}
						});
					} else {
						showAlert("Not Found", "Product not found with ID: " + productId);
					}
				} catch (NumberFormatException ex) {
					showAlert("Invalid Input", "Product ID must be a numeric value.");
				}
			});

		});

		Button searchButton = new Button("Search Product");
		searchButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label searchLabel = new Label("Search Product ID:");
			TextField searchField = new TextField();
			Button searchActionButton = new Button("Search");
			searchActionButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");

			dynamicPane.add(searchLabel, 0, 0);
			dynamicPane.add(searchField, 1, 0);
			dynamicPane.add(searchActionButton, 2, 0);

			searchActionButton.setOnAction(searchEvent -> {
				String searchText = searchField.getText().trim();

				if (searchText.isEmpty()) {
					showAlert("Invalid Input", "Please enter a Product ID.");
					return;
				}

				try {
					int productId = Integer.parseInt(searchText);

					Product foundProduct = findProductById(productId, productData);

					if (foundProduct != null) {
						String productInfo = String.format("ID: %d\nName: %s\nCategory: %s\nPrice: $%.2f\nStock: %d",
								foundProduct.getProductId(), foundProduct.getProductName(), foundProduct.getCategory(),
								foundProduct.getPrice(), foundProduct.getStockLevel());

						showAlert("Product Found", productInfo);
					} else {
						showAlert("Not Found", "Product not found with ID: " + productId);
					}
				} catch (NumberFormatException ex) {
					showAlert("Invalid Input", "Product ID must be a numeric value.");
				}
			});
		});

		VBox buttonPane = new VBox(10, addButton, updateButton, deleteButton, searchButton);
		buttonPane.setPadding(new Insets(10));
		buttonPane.setAlignment(Pos.CENTER);

		HBox contentPane = new HBox(20, productTable, buttonPane, dynamicPane);
		contentPane.setPadding(new Insets(20));

		productManagementPane.setCenter(contentPane);

		tab.setContent(productManagementPane);
		return tab;
	}

	private int generateProductIdFromDatabase() {
		int maxId = 0;
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT MAX(productId) FROM Products")) {

			if (resultSet.next()) {
				maxId = resultSet.getInt(1);
			}

			if (resultSet.wasNull()) {
				maxId = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxId + 1;
	}

	private Product findProductById(int productId, ObservableList<Product> productData) {
		for (Product product : productData) {
			if (product.getProductId() == productId) {
				return product;
			}
		}
		return null;
	}

	private void initializeProductTable(TableView<Product> productTable) {

		TableColumn<Product, Integer> idColumn = new TableColumn<>("ID");
		idColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());

		TableColumn<Product, String> nameColumn = new TableColumn<>("Name");
		nameColumn.setCellValueFactory(cellData -> cellData.getValue().productNameProperty());

		TableColumn<Product, String> categoryColumn = new TableColumn<>("Category");
		categoryColumn.setCellValueFactory(cellData -> cellData.getValue().categoryProperty());

		TableColumn<Product, Double> priceColumn = new TableColumn<>("Price");
		priceColumn.setCellValueFactory(cellData -> cellData.getValue().priceProperty().asObject());

		TableColumn<Product, Integer> stockColumn = new TableColumn<>("Stock");
		stockColumn.setCellValueFactory(cellData -> cellData.getValue().stockLevelProperty().asObject());

		productTable.getColumns().addAll(idColumn, nameColumn, categoryColumn, priceColumn, stockColumn);

	}

	private void loadProductData(ObservableList<Product> productData, TableView<Product> productTable) {
		productData.clear();

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM Products")) {

			boolean hasData = false;

			while (resultSet.next()) {
				hasData = true;

				int id = resultSet.getInt("ProductID");
				String name = resultSet.getString("Name");
				String category = resultSet.getString("Category");
				double price = resultSet.getDouble("Price");
				int stock = resultSet.getInt("StockLevel");

				productData.add(new Product(id, name, category, price, stock));
			}

			productTable.setItems(productData);

			if (!hasData) {
				showWarning("No products found in the database.");
			}

		} catch (SQLException e) {
			e.printStackTrace();
			showWarning("Failed to load products from the database!");
		}
	}

	/// ---------------------- Order Management Tab ------------------------------

	private Tab createOrderManagementTab() {
		Tab tab = new Tab("Order Management");
		tab.setClosable(false);

		BorderPane orderManagementPane = new BorderPane();

		String imagePath = "file:///C:/Users/coolnet/eclipse-workspace/onlineStore/src/storeOnline/Screenshot%202024-12-30%20143730.png";
		orderManagementPane.setStyle("-fx-background-image: url('" + imagePath + "');" + "-fx-background-size: cover;"
				+ "-fx-background-position: center center;" + "-fx-background-repeat: no-repeat;");

		TableView<Order> orderTable = new TableView<>();
		ObservableList<Order> orderData = FXCollections.observableArrayList();
		initializeOrderTable(orderTable);
		loadOrderData(orderData, orderTable);

		GridPane dynamicPane = new GridPane();
		dynamicPane.setPadding(new Insets(10));
		dynamicPane.setVgap(10);
		dynamicPane.setHgap(10);
		dynamicPane.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9);" + "-fx-border-color: #d3d3d3;"
				+ "-fx-border-width: 1; -fx-border-radius: 10; -fx-background-radius: 10;");
		dynamicPane.setAlignment(Pos.CENTER);

		Button addButton = new Button("Add Order");
		addButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
		addButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label orderDateLabel = new Label("Order Date:");
			DatePicker orderDateField = new DatePicker();

			Label addressLabel = new Label("Shipping Address:");
			TextField addressField = new TextField();

			Label customerIdLabel = new Label("Customer ID:");
			TextField customerIdField = new TextField();

			Label weightLabel = new Label("Weight:");
			TextField weightField = new TextField();

			Label distanceLabel = new Label("Distance:");
			TextField distanceField = new TextField();

			Label regionLabel = new Label("Region:");
			ComboBox<String> regionComboBox = new ComboBox<>();
			regionComboBox.getItems().addAll("North", "South", "East", "West");

			Label totalPriceLabel = new Label("Total Price:");
			TextField totalPriceField = new TextField();

			Button saveButton = new Button("Add");
			saveButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

			saveButton.setOnAction(saveEvent -> {
				try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {

					if (orderDateField.getValue() == null) {
						showAlert("Invalid Input", "Please select an order date.");
						return;
					}

					String address = addressField.getText().trim();
					if (address.isEmpty()) {
						showAlert("Invalid Input", "Shipping address cannot be empty.");
						return;
					}

					String customerIdText = customerIdField.getText().trim();
					int customerId;
					try {
						customerId = Integer.parseInt(customerIdText);
					} catch (NumberFormatException ex) {
						showAlert("Invalid Input", "Customer ID must be a numeric value.");
						return;
					}

					String weightText = weightField.getText().trim();
					double weight;
					try {
						weight = Double.parseDouble(weightText);
						if (weight <= 0) {
							showAlert("Invalid Input", "Weight must be a positive number.");
							return;
						}
					} catch (NumberFormatException ex) {
						showAlert("Invalid Input", "Weight must be a numeric value.");
						return;
					}

					String distanceText = distanceField.getText().trim();
					double distance;
					try {
						distance = Double.parseDouble(distanceText);
						if (distance <= 0) {
							showAlert("Invalid Input", "Distance must be a positive number.");
							return;
						}
					} catch (NumberFormatException ex) {
						showAlert("Invalid Input", "Distance must be a numeric value.");
						return;
					}

					String region = regionComboBox.getValue();
					if (region == null || region.isEmpty()) {
						showAlert("Invalid Input", "Please select a region.");
						return;
					}

					String totalPriceText = totalPriceField.getText().trim();
					double totalPrice;
					try {
						totalPrice = Double.parseDouble(totalPriceText);
						if (totalPrice <= 0) {
							showAlert("Invalid Input", "Total price must be a positive number.");
							return;
						}
					} catch (NumberFormatException ex) {
						showAlert("Invalid Input", "Total price must be a numeric value.");
						return;
					}

					String checkCustomerQuery = "SELECT COUNT(*) FROM Customers WHERE CustomerID = ?";
					try (PreparedStatement checkStmt = connection.prepareStatement(checkCustomerQuery)) {
						checkStmt.setInt(1, customerId);
						ResultSet rs = checkStmt.executeQuery();
						if (rs.next() && rs.getInt(1) == 0) {
							showAlert("Invalid Customer",
									"Customer ID " + customerId + " does not exist. Please create the customer first.");
							return;
						}
					}

					String insertOrderQuery = "INSERT INTO Orders (CustomerID, OrderDate, TotalPrice, Shoping_Adress, weight, distance, region) VALUES (?, ?, ?, ?, ?, ?, ?)";
					try (PreparedStatement insertOrderStmt = connection.prepareStatement(insertOrderQuery)) {
						insertOrderStmt.setInt(1, customerId);
						insertOrderStmt.setDate(2, java.sql.Date.valueOf(orderDateField.getValue()));
						insertOrderStmt.setDouble(3, totalPrice);
						insertOrderStmt.setString(4, address);
						insertOrderStmt.setDouble(5, weight);
						insertOrderStmt.setDouble(6, distance);
						insertOrderStmt.setString(7, region);

						insertOrderStmt.executeUpdate();

						Order newOrder = new Order(generateOrderIdFromDatabase(connection),
								java.sql.Date.valueOf(orderDateField.getValue()), address, customerId);
						newOrder.setWeight(weight);
						newOrder.setDistance(distance);
						newOrder.setRegion(region);
						newOrder.setShippingCost(totalPrice);

						orderData.add(newOrder);
						orderTable.refresh();

						showAlert("Success", "Order added successfully!");
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
					showAlert("Database Error", "Failed to add order! Please check your inputs.");
				}
			});

			dynamicPane.add(orderDateLabel, 0, 0);
			dynamicPane.add(orderDateField, 1, 0);
			dynamicPane.add(addressLabel, 0, 1);
			dynamicPane.add(addressField, 1, 1);
			dynamicPane.add(customerIdLabel, 0, 2);
			dynamicPane.add(customerIdField, 1, 2);
			dynamicPane.add(totalPriceLabel, 0, 3);
			dynamicPane.add(totalPriceField, 1, 3);
			dynamicPane.add(weightLabel, 0, 4);
			dynamicPane.add(weightField, 1, 4);
			dynamicPane.add(distanceLabel, 0, 5);
			dynamicPane.add(distanceField, 1, 5);
			dynamicPane.add(regionLabel, 0, 6);
			dynamicPane.add(regionComboBox, 1, 6);
			dynamicPane.add(saveButton, 1, 7);
		});

		Button updateButton = new Button("Update Order");
		updateButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
		updateButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label idLabel = new Label("Order ID:");
			TextField idField = new TextField();

			Label orderDateLabel = new Label("Order Date:");
			DatePicker orderDateField = new DatePicker();

			Label addressLabel = new Label("Shipping Address:");
			TextField addressField = new TextField();

			Label customerIdLabel = new Label("Customer ID:");
			TextField customerIdField = new TextField();

			Label weightLabel = new Label("Weight:");
			TextField weightField = new TextField();

			Label distanceLabel = new Label("Distance:");
			TextField distanceField = new TextField();

			Label regionLabel = new Label("Region:");
			ComboBox<String> regionComboBox = new ComboBox<>();
			regionComboBox.getItems().addAll("North", "South", "East", "West");

			Button findButton = new Button("Find");
			findButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");
			Button saveButton = new Button("Update");
			saveButton.setStyle("-fx-background-color: #FFA500; -fx-text-fill: white;");
			saveButton.setDisable(true);

			findButton.setOnAction(findEvent -> {
				String idText = idField.getText().trim();

				if (idText.isEmpty()) {
					showAlert("Invalid Input", "Please enter an Order ID.");
					return;
				}

				try {
					int orderId = Integer.parseInt(idText);
					Order orderToUpdate = findOrderById(orderId, orderData);

					if (orderToUpdate != null) {

						LocalDate localDate = ((java.sql.Date) orderToUpdate.getOrederDate()).toLocalDate();
						orderDateField.setValue(localDate);
						addressField.setText(orderToUpdate.getShoping_Adress());
						customerIdField.setText(String.valueOf(orderToUpdate.getCustomer()));
						weightField.setText(String.valueOf(orderToUpdate.getWeight()));
						distanceField.setText(String.valueOf(orderToUpdate.getDistance()));
						regionComboBox.setValue(orderToUpdate.getRegion());
						saveButton.setDisable(false);
					} else {
						showAlert("Not Found", "Order not found with ID: " + orderId);
					}
				} catch (NumberFormatException ex) {
					showAlert("Invalid Input", "Order ID must be a numeric value.");
				}
			});

			saveButton.setOnAction(saveEvent -> {
				try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
						PreparedStatement statement = connection.prepareStatement(
								"UPDATE Orders SET OrderDate = ?, Shoping_Adress = ?, CustomerID = ?, weight = ?, distance = ?, region = ? WHERE OrderID = ?")) {

					String idText = idField.getText().trim();
					if (idText.isEmpty()) {
						showAlert("Invalid Input", "Order ID is required.");
						return;
					}
					int orderId = Integer.parseInt(idText);

					if (orderDateField.getValue() == null) {
						showAlert("Invalid Input", "Order date is required.");
						return;
					}
					java.sql.Date orderDate = java.sql.Date.valueOf(orderDateField.getValue());

					String address = addressField.getText().trim();
					if (address.isEmpty()) {
						showAlert("Invalid Input", "Shipping address is required.");
						return;
					}

					String customerIdText = customerIdField.getText().trim();
					int customerId;
					try {
						customerId = Integer.parseInt(customerIdText);
					} catch (NumberFormatException ex) {
						showAlert("Invalid Input", "Customer ID must be a numeric value.");
						return;
					}

					String weightText = weightField.getText().trim();
					double weight;
					try {
						weight = Double.parseDouble(weightText);
						if (weight <= 0) {
							showAlert("Invalid Input", "Weight must be a positive number.");
							return;
						}
					} catch (NumberFormatException ex) {
						showAlert("Invalid Input", "Weight must be a numeric value.");
						return;
					}

					String distanceText = distanceField.getText().trim();
					double distance;
					try {
						distance = Double.parseDouble(distanceText);
						if (distance <= 0) {
							showAlert("Invalid Input", "Distance must be a positive number.");
							return;
						}
					} catch (NumberFormatException ex) {
						showAlert("Invalid Input", "Distance must be a numeric value.");
						return;
					}

					String region = regionComboBox.getValue();
					if (region == null || region.isEmpty()) {
						showAlert("Invalid Input", "Please select a region.");
						return;
					}

					statement.setDate(1, orderDate);
					statement.setString(2, address);
					statement.setInt(3, customerId);
					statement.setDouble(4, weight);
					statement.setDouble(5, distance);
					statement.setString(6, region);
					statement.setInt(7, orderId);

					int rowsUpdated = statement.executeUpdate();

					if (rowsUpdated > 0) {
						Order orderToUpdate = findOrderById(orderId, orderData);
						if (orderToUpdate != null) {
							orderToUpdate.setOrederDate(orderDate);
							orderToUpdate.setShoping_Adress(address);
							orderToUpdate.setCustomer(customerId);
							orderToUpdate.setWeight(weight);
							orderToUpdate.setDistance(distance);
							orderToUpdate.setRegion(region);
							orderTable.refresh();
						}
						showAlert("Success", "Order updated successfully!");
					} else {
						showAlert("Not Found", "Order not found in the database.");
					}
				} catch (SQLException ex) {
					ex.printStackTrace();
					showAlert("Database Error", "An error occurred while updating the order.");
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
					showAlert("Invalid Input", "Please check your input fields.");
				}
			});

			dynamicPane.add(idLabel, 0, 0);
			dynamicPane.add(idField, 1, 0);
			dynamicPane.add(findButton, 2, 0);
			dynamicPane.add(orderDateLabel, 0, 1);
			dynamicPane.add(orderDateField, 1, 1);
			dynamicPane.add(addressLabel, 0, 2);
			dynamicPane.add(addressField, 1, 2);
			dynamicPane.add(customerIdLabel, 0, 3);
			dynamicPane.add(customerIdField, 1, 3);
			dynamicPane.add(weightLabel, 0, 4);
			dynamicPane.add(weightField, 1, 4);
			dynamicPane.add(distanceLabel, 0, 5);
			dynamicPane.add(distanceField, 1, 5);
			dynamicPane.add(regionLabel, 0, 6);
			dynamicPane.add(regionComboBox, 1, 6);
			dynamicPane.add(saveButton, 1, 7);

		});

		Button deleteButton = new Button("Delete Order");
		deleteButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");
		deleteButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label deleteLabel = new Label("Enter Order ID to Delete:");
			TextField deleteField = new TextField();
			Button deleteActionButton = new Button("Delete");
			deleteActionButton.setStyle("-fx-background-color: #FF0000; -fx-text-fill: white;");

			dynamicPane.add(deleteLabel, 0, 0);
			dynamicPane.add(deleteField, 1, 0);
			dynamicPane.add(deleteActionButton, 2, 0);

			deleteActionButton.setOnAction(deleteEvent -> {
				String deleteText = deleteField.getText().trim();

				if (deleteText.isEmpty()) {
					showAlert("Invalid Input", "Please enter an Order ID.");
					return;
				}

				try {
					int orderId = Integer.parseInt(deleteText);

					Order orderToDelete = findOrderById(orderId, orderData);

					if (orderToDelete != null) {

						Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
						confirmAlert.setTitle("Confirm Deletion");
						confirmAlert.setHeaderText(null);
						confirmAlert
								.setContentText("Are you sure you want to delete the order with ID: " + orderId + "?");

						confirmAlert.showAndWait().ifPresent(response -> {
							if (response == ButtonType.OK) {
								try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
										PreparedStatement statement = connection
												.prepareStatement("DELETE FROM Orders WHERE orderId = ?")) {

									statement.setInt(1, orderId);
									int rowsDeleted = statement.executeUpdate();

									if (rowsDeleted > 0) {

										orderData.remove(orderToDelete);
										orderTable.refresh();
										showAlert("Success", "Order with ID " + orderId + " has been deleted.");
									} else {
										showAlert("Database Error", "Failed to delete order from the database.");
									}
								} catch (SQLException ex) {
									ex.printStackTrace();
									showAlert("Database Error", "An error occurred while deleting the order.");
								}
							}
						});
					} else {
						showAlert("Not Found", "Order not found with ID: " + orderId);
					}
				} catch (NumberFormatException ex) {
					showAlert("Invalid Input", "Order ID must be a numeric value.");
				}
			});

		});

		Button searchButton = new Button("Search Order");
		searchButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");
		searchButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");
		searchButton.setOnAction(e -> {
			dynamicPane.getChildren().clear();

			Label searchLabel = new Label("Search Order ID:");
			TextField searchField = new TextField();
			Button searchActionButton = new Button("Search");
			searchActionButton.setStyle("-fx-background-color: #007BFF; -fx-text-fill: white;");

			dynamicPane.add(searchLabel, 0, 0);
			dynamicPane.add(searchField, 1, 0);
			dynamicPane.add(searchActionButton, 2, 0);

			searchActionButton.setOnAction(searchEvent -> {
				String searchText = searchField.getText().trim();

				if (searchText.isEmpty()) {
					showAlert("Invalid Input", "Please enter an Order ID.");
					return;
				}

				try {
					int orderId = Integer.parseInt(searchText);

					Order foundOrder = findOrderById(orderId, orderData);

					if (foundOrder != null) {
						String orderInfo = String.format(
								"Order ID: %d\nCustomer ID: %d\nOrder Date: %s\nShipping Address: %s\nTotal Price: $%.2f\nWeight: %.2f\nDistance: %.2f\nRegion: %s",
								foundOrder.getOrder_Id(), foundOrder.getCustomer(), foundOrder.getOrederDate(),
								foundOrder.getShoping_Adress(), foundOrder.getShippingCost(), foundOrder.getWeight(),
								foundOrder.getDistance(), foundOrder.getRegion());

						showAlert("Order Found", orderInfo);
					} else {
						showAlert("Not Found", "Order not found with ID: " + orderId);
					}
				} catch (NumberFormatException ex) {
					showAlert("Invalid Input", "Order ID must be a numeric value.");
				}
			});

		});

		VBox buttonPane = new VBox(10, addButton, updateButton, deleteButton, searchButton);
		buttonPane.setPadding(new Insets(10));
		buttonPane.setAlignment(Pos.CENTER);

		HBox contentPane = new HBox(20, orderTable, buttonPane, dynamicPane);
		contentPane.setPadding(new Insets(20));

		orderManagementPane.setCenter(contentPane);

		tab.setContent(orderManagementPane);
		return tab;
	}

	private int generateOrderIdFromDatabase(Connection connection2) {

		int maxId = 0;
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT MAX(orderId) FROM Orders")) {

			if (resultSet.next()) {
				maxId = resultSet.getInt(1);
			}

			if (resultSet.wasNull()) {
				maxId = 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return maxId + 1;
	}

	private Order findOrderById(int orderId, ObservableList<Order> orderData) {
		for (Order order : orderData) {
			if (order.getOrder_Id() == orderId) {
				return order;
			}
		}
		return null;
	}

	private void initializeOrderTable(TableView<Order> orderTable) {

		TableColumn<Order, Integer> idColumn = new TableColumn<>("Order ID");
		idColumn.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getOrder_Id()).asObject());

		TableColumn<Order, String> addressColumn = new TableColumn<>("Address");
		addressColumn
				.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShoping_Adress()));

		TableColumn<Order, java.util.Date> dateColumn = new TableColumn<>("Order Date");
		dateColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getOrederDate()));

		TableColumn<Order, Integer> customerColumn = new TableColumn<>("Customer ID");
		customerColumn.setCellValueFactory(
				cellData -> new SimpleIntegerProperty(cellData.getValue().getCustomer()).asObject());

		TableColumn<Order, Double> totalPriceColumn = new TableColumn<>("Total Price");
		totalPriceColumn.setCellValueFactory(cellData -> new SimpleDoubleProperty().asObject());

		TableColumn<Order, Double> weightColumn = new TableColumn<>("Weight");
		weightColumn
				.setCellValueFactory(cellData -> new SimpleDoubleProperty(cellData.getValue().getWeight()).asObject());

		TableColumn<Order, Double> distanceColumn = new TableColumn<>("Distance");
		distanceColumn.setCellValueFactory(
				cellData -> new SimpleDoubleProperty(cellData.getValue().getDistance()).asObject());

		TableColumn<Order, String> regionColumn = new TableColumn<>("Region");
		regionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRegion()));

		TableColumn<Order, String> statusColumn = new TableColumn<>("Shipping Status");
		statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getShippingStatus()));

		orderTable.getColumns().addAll(idColumn, addressColumn, dateColumn, customerColumn, totalPriceColumn,
				weightColumn, distanceColumn, regionColumn, statusColumn);
	}

	private void loadOrderData(ObservableList<Order> orderData, TableView<Order> orderTable) {

		orderData.clear();
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM Orders")) {

			while (resultSet.next()) {
				int id = resultSet.getInt("OrderID");
				Date orderDate = resultSet.getDate("OrderDate");
				String address = resultSet.getString("Shoping_Adress");
				int customerId = resultSet.getInt("CustomerID");
				double totalPrice = resultSet.getDouble("TotalPrice");
				double weight = resultSet.getDouble("Weight");
				double distance = resultSet.getDouble("Distance");
				String region = resultSet.getString("Region");
				String shippingStatus = resultSet.getString("ShippingStatus");

				Order order = new Order(id, orderDate, address, customerId);
				order.setTotalPrice(totalPrice);
				order.setWeight(weight);
				order.setDistance(distance);
				order.setRegion(region);
				order.setShippingStatus(shippingStatus);

				orderData.add(order);
			}

			orderTable.setItems(orderData);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/// ---------------------- Reports Tab ------------------------------

	private Tab createReportsTab() {

		Tab tab = new Tab("Reports");
		tab.setClosable(false);

		VBox reportsPane = new VBox(15);
		reportsPane.setPadding(new Insets(20));
		reportsPane.setAlignment(Pos.CENTER);

		Label titleLabel = new Label("Reports");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

		// ComboBox to select report type
		ComboBox<String> reportTypeComboBox = new ComboBox<>();
		reportTypeComboBox.getItems().addAll("Sales Report", "Product Performance", "Employee Activity",
				"Inventory Status");
		reportTypeComboBox.setPromptText("Select Report Type");

		Button generateReportButton = new Button("Generate Report");
		generateReportButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

		TableView<ObservableList<String>> reportTable = new TableView<>();
		reportTable.setPlaceholder(new Label("No data to display"));

		generateReportButton.setOnAction(e -> {
			String selectedReport = reportTypeComboBox.getValue();
			if (selectedReport != null) {
				generateReport(selectedReport, reportTable);
			} else {
				showWarning("Please select a report type.");
			}
		});

		reportsPane.getChildren().addAll(titleLabel, reportTypeComboBox, generateReportButton, reportTable);

		tab.setContent(reportsPane);
		return tab;

	}

	private void generateReport(String reportType, TableView<ObservableList<String>> reportTable) {
		reportTable.getColumns().clear();
		ObservableList<ObservableList<String>> data = FXCollections.observableArrayList();

		String query = "";
		switch (reportType) {
		case "Sales Report":
			query = "SELECT DATE(OrderDate) AS ReportDate, COUNT(OrderID) AS TotalOrders, "
					+ "SUM(TotalPrice) AS TotalSales, AVG(TotalPrice) AS AverageOrderValue, "
					+ "MAX(TotalPrice) AS HighestOrder, MIN(TotalPrice) AS LowestOrder "
					+ "FROM Orders GROUP BY ReportDate ORDER BY ReportDate DESC";
			break;
		case "Product Performance":
			query = "SELECT Products.Name AS ProductName, SUM(OrderProducts.Quantity) AS TotalSold, "
					+ "SUM(OrderProducts.Quantity * OrderProducts.UnitPrice) AS TotalRevenue "
					+ "FROM OrderProducts JOIN Products ON OrderProducts.ProductID = Products.ProductID "
					+ "GROUP BY Products.Name ORDER BY TotalSold DESC LIMIT 10";
			break;
		case "Employee Activity":
			query = "SELECT Users.Username, ActivityLogs.Activity, ActivityLogs.Timestamp "
					+ "FROM ActivityLogs JOIN Users ON ActivityLogs.UserID = Users.UserID "
					+ "ORDER BY ActivityLogs.Timestamp DESC";
			break;
		case "Inventory Status":
			query = "SELECT Products.Name, Inventory.QuantityInStock, Inventory.RestockDate, Products.LowStockThreshold "
					+ "FROM Inventory JOIN Products ON Inventory.ProductID = Products.ProductID "
					+ "ORDER BY Inventory.QuantityInStock ASC";
			break;
		default:
			showWarning("Invalid report type selected.");
			return;
		}

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(query)) {

			// Create table columns dynamically based on ResultSet metadata
			for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
				final int columnIndex = i - 1;
				TableColumn<ObservableList<String>, String> column = new TableColumn<>(
						resultSet.getMetaData().getColumnName(i));
				column.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().get(columnIndex)));
				reportTable.getColumns().add(column);
			}

			// Populate table data
			while (resultSet.next()) {
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= resultSet.getMetaData().getColumnCount(); i++) {
					row.add(resultSet.getString(i));
				}
				data.add(row);
			}

			reportTable.setItems(data);

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public class PermissionsManager {

		public static boolean canAddProduct(User user) {
			return user.getRole().equals("Admin") || user.getRole().equals("Manager");
		}

		public static boolean canUpdateStock(User user) {
			return user.getRole().equals("Admin") || user.getRole().equals("Employee");
		}

		public static boolean canViewOrders(User user) {
			return !user.getRole().equals("Employee");
		}
	}

	private void setLanguage(String lang) {

		try {
			Locale locale = new Locale(lang);
			bundle = ResourceBundle.getBundle("storeOnline.messages", locale);
		} catch (MissingResourceException e) {
			System.err.println("Missing language file for locale: " + lang + ", defaulting to English.");
			bundle = ResourceBundle.getBundle("storeOnline.messages", Locale.ENGLISH);
		}

	}

	private void showWarning(String message) {

		Alert alert = new Alert(Alert.AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();

	}

//////////////////////////----------------------------------------------------
//////////////////////////----------------------------------------------------

	private Tab createSalesAndPurchasesTab() {
		Tab tab = new Tab("Sales and Purchases");
		tab.setClosable(false);

		VBox mainPane = new VBox(20);
		mainPane.setPadding(new Insets(20));
		mainPane.setAlignment(Pos.TOP_CENTER);
		mainPane.setStyle("-fx-background-color: #f4f4f4;");

		Label titleLabel = new Label("Manage Sales and Purchases");
		titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

		TabPane salesAndPurchasesPane = new TabPane();

		Tab salesTab = new Tab("Sales");
		salesTab.setClosable(false);
		salesTab.setContent(createSalesPane());

		Tab purchasesTab = new Tab("Purchases");
		purchasesTab.setClosable(false);
		purchasesTab.setContent(createPurchasesPane());

		Tab transactionsTab = new Tab("Transactions");
		transactionsTab.setClosable(false);
		transactionsTab.setContent(createTransactionsPane());

		salesAndPurchasesPane.getTabs().addAll(salesTab, purchasesTab, transactionsTab);

		mainPane.getChildren().addAll(titleLabel, salesAndPurchasesPane);

		tab.setContent(mainPane);
		return tab;
	}

	private VBox createSalesPane() {
		VBox salesPane = new VBox(15);
		salesPane.setPadding(new Insets(20));
		salesPane.setAlignment(Pos.TOP_CENTER);

		Label titleLabel = new Label("Sales Management");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

		TableView<Product> productsTable = new TableView<>();
		ObservableList<Product> productsData = FXCollections.observableArrayList();
		initializeProductTable(productsTable);
		loadProductData(productsData, productsTable);
		productsTable.setPrefHeight(200);

		// زر التحديث
		Button refreshButton = new Button("Refresh");
		refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
		refreshButton.setOnAction(e -> {
			productsData.clear(); // حذف البيانات الحالية
			loadProductData(productsData, productsTable); // إعادة تحميل البيانات
			productsTable.refresh(); // تحديث الجدول
		});

		GridPane salesForm = new GridPane();
		salesForm.setHgap(10);
		salesForm.setVgap(15);
		salesForm.setPadding(new Insets(20));
		salesForm.setStyle(
				"-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-padding: 20;");

		Label userIdLabel = new Label("User ID:");
		TextField userIdField = new TextField();

		Label productIdLabel = new Label("Product ID:");
		TextField productIdField = new TextField();

		Label quantityLabel = new Label("Quantity:");
		TextField quantityField = new TextField();

		Label paymentMethodLabel = new Label("Payment Method:");
		ComboBox<String> paymentMethodComboBox = new ComboBox<>();
		paymentMethodComboBox.getItems().addAll("Cash", "Credit Card", "Online Payment");

		Button sellButton = new Button("Sell");
		sellButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
		sellButton.setOnAction(e -> {
			try {
				int userId = Integer.parseInt(userIdField.getText());
				if (!isUserRegistered(userId)) {
					showWarning("User is not registered. Please log in or register to perform this action.");
					return;
				}

				int productId = Integer.parseInt(productIdField.getText());
				int quantity = Integer.parseInt(quantityField.getText());
				String paymentMethod = paymentMethodComboBox.getValue();
				double totalPrice = calculateTotalPrice(productId, quantity);

				addSale(productId, quantity, paymentMethod, totalPrice);
				showAlert("Success", "Sale transaction added successfully!");
				userIdField.clear();
				productIdField.clear();
				quantityField.clear();
				paymentMethodComboBox.getSelectionModel().clearSelection();
			} catch (NumberFormatException ex) {
				showWarning("Please enter valid numeric values.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showWarning("An error occurred.");
			}
		});

		salesForm.add(userIdLabel, 0, 0);
		salesForm.add(userIdField, 1, 0);
		salesForm.add(productIdLabel, 0, 1);
		salesForm.add(productIdField, 1, 1);
		salesForm.add(quantityLabel, 0, 2);
		salesForm.add(quantityField, 1, 2);
		salesForm.add(paymentMethodLabel, 0, 3);
		salesForm.add(paymentMethodComboBox, 1, 3);
		salesForm.add(sellButton, 1, 4);

		salesPane.getChildren().addAll(titleLabel, productsTable, refreshButton, salesForm);
		return salesPane;
	}

	private boolean isUserRegistered(int userId) {
		String query = "SELECT COUNT(*) FROM Users WHERE UserID = ?";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection.prepareStatement(query)) {

			statement.setInt(1, userId);
			ResultSet resultSet = statement.executeQuery();
			if (resultSet.next()) {
				return resultSet.getInt(1) > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showWarning("Database error occurred while verifying user.");
		}
		return false;
	}

	private double calculateTotalPrice(int productId, int quantity) {
		double pricePerUnit = 0.0;

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection
						.prepareStatement("SELECT Price FROM Products WHERE ProductID = ?")) {

			statement.setInt(1, productId);
			ResultSet resultSet = statement.executeQuery();

			if (resultSet.next()) {
				pricePerUnit = resultSet.getDouble("Price");
			} else {
				throw new IllegalArgumentException("Invalid Product ID. Product not found.");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Database error occurred while calculating total price.");
		}

		double totalPrice = pricePerUnit * quantity;

		return totalPrice;
	}

	private VBox createPurchasesPane() {
		VBox purchasesPane = new VBox(20);
		purchasesPane.setPadding(new Insets(20));
		purchasesPane.setAlignment(Pos.CENTER);

		Label titleLabel = new Label("Purchases Management");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

		TableView<Product> productsTable = new TableView<>();
		ObservableList<Product> productsData = FXCollections.observableArrayList();
		initializeProductTable(productsTable);
		loadProductData(productsData, productsTable);
		productsTable.setPrefHeight(200);

		GridPane purchasesForm = new GridPane();
		purchasesForm.setHgap(10);
		purchasesForm.setVgap(15);
		purchasesForm.setPadding(new Insets(20));
		purchasesForm.setStyle(
				"-fx-background-color: #ffffff; -fx-border-color: #dcdcdc; -fx-border-radius: 10; -fx-padding: 20;");

		Label userIdLabel = new Label("User ID:");
		TextField userIdField = new TextField();

		Label productIdLabel = new Label("Product ID:");
		TextField productIdField = new TextField();

		Label quantityLabel = new Label("Quantity:");
		TextField quantityField = new TextField();

		Label supplierLabel = new Label("Supplier:");
		TextField supplierField = new TextField();

		Label paymentMethodLabel = new Label("Payment Method:");
		ComboBox<String> paymentMethodComboBox = new ComboBox<>();
		paymentMethodComboBox.getItems().addAll("Cash", "Credit Card", "Online Payment");

		Button purchaseButton = new Button("Purchase");
		purchaseButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

		purchaseButton.setOnAction(e -> {
			try {
				int userId = Integer.parseInt(userIdField.getText());
				if (!isUserRegistered(userId)) {
					showWarning("User is not registered. Please log in or register to perform this action.");
					return;
				}

				int productId = Integer.parseInt(productIdField.getText());
				int quantity = Integer.parseInt(quantityField.getText());
				String supplier = supplierField.getText();
				String paymentMethod = paymentMethodComboBox.getValue();

				if (paymentMethod == null || paymentMethod.isEmpty()) {
					showWarning("Please select a payment method.");
					return;
				}

				addPurchase(productId, quantity, supplier, paymentMethod);
				showAlert("Success", "Purchase transaction added successfully!");
				userIdField.clear();
				productIdField.clear();
				quantityField.clear();
				supplierField.clear();
				paymentMethodComboBox.getSelectionModel().clearSelection();
			} catch (NumberFormatException ex) {
				showWarning("Please enter valid numeric values.");
			} catch (Exception ex) {
				ex.printStackTrace();
				showWarning("An error occurred.");
			}
		});

		purchasesForm.add(userIdLabel, 0, 0);
		purchasesForm.add(userIdField, 1, 0);
		purchasesForm.add(productIdLabel, 0, 1);
		purchasesForm.add(productIdField, 1, 1);
		purchasesForm.add(quantityLabel, 0, 2);
		purchasesForm.add(quantityField, 1, 2);
		purchasesForm.add(supplierLabel, 0, 3);
		purchasesForm.add(supplierField, 1, 3);
		purchasesForm.add(paymentMethodLabel, 0, 4);
		purchasesForm.add(paymentMethodComboBox, 1, 4);
		purchasesForm.add(purchaseButton, 1, 5);

		purchasesPane.getChildren().addAll(titleLabel, productsTable, purchasesForm);
		return purchasesPane;
	}

	private VBox createTransactionsPane() {
		VBox transactionsPane = new VBox(15);
		transactionsPane.setPadding(new Insets(20));
		transactionsPane.setAlignment(Pos.TOP_CENTER);

		Label titleLabel = new Label("Transactions History");
		titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2980b9;");

		TableView<PurchaseAndSales> transactionsTable = new TableView<>();
		ObservableList<PurchaseAndSales> transactionsData = FXCollections.observableArrayList();
		initializeTransactionsTable(transactionsTable);
		loadTransactionData(transactionsData);
		transactionsTable.setItems(transactionsData);
		transactionsTable.setPrefHeight(300);

		HBox actionButtons = new HBox(10);
		actionButtons.setAlignment(Pos.CENTER);

		Button refreshButton = new Button("Refresh");
		refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
		refreshButton
				.setOnMouseEntered(e -> refreshButton.setStyle("-fx-background-color: #5dade2; -fx-text-fill: white;"));
		refreshButton
				.setOnMouseExited(e -> refreshButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;"));
		refreshButton.setOnAction(e -> {
			transactionsData.clear();
			loadTransactionData(transactionsData);
			transactionsTable.refresh();
		});

		Button deleteTransactionButton = new Button("Delete Transaction");
		deleteTransactionButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
		deleteTransactionButton.setOnMouseEntered(
				e -> deleteTransactionButton.setStyle("-fx-background-color: #c0392b; -fx-text-fill: white;"));
		deleteTransactionButton.setOnMouseExited(
				e -> deleteTransactionButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;"));

		deleteTransactionButton.setOnAction(e -> {
			PurchaseAndSales selectedTransaction = transactionsTable.getSelectionModel().getSelectedItem();
			if (selectedTransaction != null) {

				Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
				confirmAlert.setTitle("Confirm Deletion");
				confirmAlert.setHeaderText("Are you sure you want to delete this transaction?");
				confirmAlert.setContentText("Transaction ID: " + selectedTransaction.getTransactionId());

				Optional<ButtonType> result = confirmAlert.showAndWait();
				if (result.isPresent() && result.get() == ButtonType.OK) {
					deleteTransaction(selectedTransaction.getTransactionId());
					transactionsData.remove(selectedTransaction);
					transactionsTable.refresh();
					showAlert("Success", "Transaction deleted successfully!");
				}
			} else {
				showWarning("Please select a transaction to delete.");
			}
		});

		actionButtons.getChildren().addAll(refreshButton, deleteTransactionButton);

		transactionsPane.getChildren().addAll(titleLabel, transactionsTable, actionButtons);

		return transactionsPane;
	}

	private void deleteTransaction(int transactionId) {
		String deleteQuery = "DELETE FROM Transactions WHERE TransactionID = ?";
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

			statement.setInt(1, transactionId);
			int rowsAffected = statement.executeUpdate();
			if (rowsAffected == 0) {
				showWarning("No transaction found with ID: " + transactionId);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			showWarning("An error occurred while deleting the transaction.");
		}
	}

	private void loadTransactionData(ObservableList<PurchaseAndSales> transactionsData) {
		transactionsData.clear();
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(
						"SELECT TransactionID, ProductID, Quantity, TransactionType, Supplier, PaymentMethod, TotalPrice FROM Transactions")) {

			while (resultSet.next()) {
				int transactionId = resultSet.getInt("TransactionID");
				int productId = resultSet.getInt("ProductID");
				int quantity = resultSet.getInt("Quantity");
				String transactionType = resultSet.getString("TransactionType");
				String supplier = resultSet.getString("Supplier");
				String paymentMethod = resultSet.getString("PaymentMethod");
				double totalPrice = resultSet.getDouble("TotalPrice");

				transactionsData.add(new PurchaseAndSales(transactionId, productId, quantity, transactionType, supplier,
						paymentMethod, totalPrice));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addSale(int productId, int quantity, String paymentMethod, double totalPrice) {
		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection.prepareStatement(
						"INSERT INTO Transactions (ProductID, Quantity, TransactionType, Supplier, PaymentMethod, TotalPrice) "
								+ "VALUES (?, ?, 'Sale', NULL, ?, ?)",
						Statement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, productId);
			statement.setInt(2, quantity);
			statement.setString(3, paymentMethod);
			statement.setDouble(4, totalPrice);
			statement.executeUpdate();

			ResultSet generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				int transactionId = generatedKeys.getInt(1);

				PurchaseAndSales newTransaction = new PurchaseAndSales(transactionId, productId, quantity, "Sale", null,
						paymentMethod, totalPrice);
				transactionsData.add(newTransaction);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void addPurchase(int productId, int quantity, String supplier, String paymentMethod) {
		double totalPrice = calculateTotalPrice(productId, quantity);

		try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
				PreparedStatement statement = connection.prepareStatement(
						"INSERT INTO Transactions (ProductID, Quantity, TransactionType, Supplier, PaymentMethod, TotalPrice) "
								+ "VALUES (?, ?, 'Purchase', ?, ?, ?)",
						Statement.RETURN_GENERATED_KEYS)) {

			statement.setInt(1, productId);
			statement.setInt(2, quantity);
			statement.setString(3, supplier);
			statement.setString(4, paymentMethod);
			statement.setDouble(5, totalPrice);

			statement.executeUpdate();

			ResultSet generatedKeys = statement.getGeneratedKeys();
			if (generatedKeys.next()) {
				int transactionId = generatedKeys.getInt(1);

				PurchaseAndSales newTransaction = new PurchaseAndSales(transactionId, productId, quantity, "Purchase",
						supplier, paymentMethod, totalPrice);
				transactionsData.add(newTransaction);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void initializeTransactionsTable(TableView<PurchaseAndSales> table) {
		TableColumn<PurchaseAndSales, Integer> idColumn = new TableColumn<>("Transaction ID");
		idColumn.setCellValueFactory(cellData -> cellData.getValue().transactionIdProperty().asObject());

		TableColumn<PurchaseAndSales, Integer> productIdColumn = new TableColumn<>("Product ID");
		productIdColumn.setCellValueFactory(cellData -> cellData.getValue().productIdProperty().asObject());

		TableColumn<PurchaseAndSales, Integer> quantityColumn = new TableColumn<>("Quantity");
		quantityColumn.setCellValueFactory(cellData -> cellData.getValue().quantityProperty().asObject());

		TableColumn<PurchaseAndSales, String> typeColumn = new TableColumn<>("Type");
		typeColumn.setCellValueFactory(cellData -> cellData.getValue().transactionTypeProperty());

		TableColumn<PurchaseAndSales, String> supplierColumn = new TableColumn<>("Supplier");
		supplierColumn.setCellValueFactory(cellData -> cellData.getValue().supplierProperty());

		TableColumn<PurchaseAndSales, String> paymentMethodColumn = new TableColumn<>("Payment Method");
		paymentMethodColumn.setCellValueFactory(cellData -> cellData.getValue().paymentMethodProperty());

		TableColumn<PurchaseAndSales, Double> totalPriceColumn = new TableColumn<>("Total Price");
		totalPriceColumn.setCellValueFactory(cellData -> cellData.getValue().totalPriceProperty().asObject());

		TableColumn<PurchaseAndSales, String> purchaseMethodColumn = new TableColumn<>("Purchase Method");
		purchaseMethodColumn.setCellValueFactory(cellData -> cellData.getValue().paymentMethodProperty());

		table.getColumns().addAll(idColumn, productIdColumn, quantityColumn, typeColumn, supplierColumn,
				paymentMethodColumn, totalPriceColumn, purchaseMethodColumn);
	}

	private void showAlert(String title, String message) {

		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();

	}

}