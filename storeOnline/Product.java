package storeOnline;

import javafx.beans.property.*;

public class Product {

	private final IntegerProperty productId;
	private final StringProperty name;
	private final StringProperty category;
	private final DoubleProperty price;
	private IntegerProperty stockLevel;

	public Product(int productId, String name, String category, double price, int stockLevel) {
		this.productId = new SimpleIntegerProperty(productId);
		this.name = new SimpleStringProperty(name);
		this.category = new SimpleStringProperty(category);
		this.price = new SimpleDoubleProperty(price);
		this.stockLevel = new SimpleIntegerProperty(stockLevel);
	}

	public Product(String name, double price, String category) {
		this.productId = new SimpleIntegerProperty(0);
		this.name = new SimpleStringProperty(name);
		this.category = new SimpleStringProperty(category);
		this.price = new SimpleDoubleProperty(price);
		this.stockLevel = new SimpleIntegerProperty(0);
	}

	// Getters
	public int getProductId() {
		return productId.get();
	}

	public String getProductName() {
		return name.get();
	}

	public String getCategory() {
		return category.get();
	}

	public double getPrice() {
		return price.get();
	}

	public int getStockLevel() {
		return stockLevel.get();
	}

	// Setters
	public void setProductId(int productId) {
		this.productId.set(productId);
	}

	public void setProductName(String productName) {
		this.name.set(productName);
	}

	public void setCategory(String category) {
		this.category.set(category);
	}

	public void setPrice(double price) {
		this.price.set(price);
	}

	public void setStockLevel(int stockLevel) {
		this.stockLevel.set(stockLevel);
	}

	// Properties
	public IntegerProperty productIdProperty() {
		return productId;
	}

	public StringProperty productNameProperty() {
		return name;
	}

	public StringProperty categoryProperty() {
		return category;
	}

	public DoubleProperty priceProperty() {
		return price;
	}

	public IntegerProperty stockLevelProperty() {
		return stockLevel;
	}

	@Override
	public String toString() {
		return "Product{" + "productId=" + productId.get() + ", productName='" + name.get() + '\'' + ", category='"
				+ category.get() + '\'' + ", price=" + price.get() + ", stockLevel=" + stockLevel.get() + '}';
	}

	public int getId() {
		// TODO Auto-generated method stub
		return 0;
	}
}