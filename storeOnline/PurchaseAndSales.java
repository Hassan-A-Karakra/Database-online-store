package storeOnline;

import javafx.beans.property.*;

public class PurchaseAndSales {
	private final IntegerProperty transactionId;
	private final IntegerProperty productId;
	private final IntegerProperty quantity;
	private final StringProperty transactionType;
	private final StringProperty supplier;
	private final StringProperty paymentMethod;
	private final DoubleProperty totalPrice;

	// Constructor
	public PurchaseAndSales(int transactionId, int productId, int quantity, String transactionType, String supplier,
			String paymentMethod, double totalPrice) {
		this.transactionId = new SimpleIntegerProperty(transactionId);
		this.productId = new SimpleIntegerProperty(productId);
		this.quantity = new SimpleIntegerProperty(quantity);
		this.transactionType = new SimpleStringProperty(transactionType);
		this.supplier = new SimpleStringProperty(supplier);
		this.paymentMethod = new SimpleStringProperty(paymentMethod);
		this.totalPrice = new SimpleDoubleProperty(totalPrice);
	}

	// Getters and setters with Property methods
	public int getTransactionId() {
		return transactionId.get();
	}

	public void setTransactionId(int transactionId) {
		this.transactionId.set(transactionId);
	}

	public IntegerProperty transactionIdProperty() {
		return transactionId;
	}

	public int getProductId() {
		return productId.get();
	}

	public void setProductId(int productId) {
		this.productId.set(productId);
	}

	public IntegerProperty productIdProperty() {
		return productId;
	}

	public int getQuantity() {
		return quantity.get();
	}

	public void setQuantity(int quantity) {
		this.quantity.set(quantity);
	}

	public IntegerProperty quantityProperty() {
		return quantity;
	}

	public String getTransactionType() {
		return transactionType.get();
	}

	public void setTransactionType(String transactionType) {
		this.transactionType.set(transactionType);
	}

	public StringProperty transactionTypeProperty() {
		return transactionType;
	}

	public String getSupplier() {
		return supplier.get();
	}

	public void setSupplier(String supplier) {
		this.supplier.set(supplier);
	}

	public StringProperty supplierProperty() {
		return supplier;
	}

	public String getPaymentMethod() {
		return paymentMethod.get();
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod.set(paymentMethod);
	}

	public StringProperty paymentMethodProperty() {
		return paymentMethod;
	}

	public double getTotalPrice() {
		return totalPrice.get();
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice.set(totalPrice);
	}

	public DoubleProperty totalPriceProperty() {
		return totalPrice;
	}

}