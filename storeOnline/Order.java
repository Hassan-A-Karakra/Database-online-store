package storeOnline;

public class Order {

	int order_Id;
	java.util.Date orederDate;
	String Shoping_Adress;
	int customer_id;
	private String shippingStatus;
	private double totalPrice;

	public int getOrder_Id() {
		return order_Id;
	}

	public void setOrder_Id(int order_Id) {
		this.order_Id = order_Id;
	}

	public java.util.Date getOrederDate() {
		return orederDate;
	}

	public void setOrederDate(java.util.Date orederDate) {
		this.orederDate = orederDate;
	}

	public String getShoping_Adress() {
		return Shoping_Adress;
	}

	public void setShoping_Adress(String shoping_Adress) {
		Shoping_Adress = shoping_Adress;
	}

	public int getCustomer() {
		return customer_id;
	}

	public void setCustomer(int customer) {
		this.customer_id = customer;
	}

	@Override
	public String toString() {
		return "Order [order_Id=" + order_Id + ", orederDate=" + orederDate + ", Shoping_Adress=" + Shoping_Adress
				+ ", customer_id=" + customer_id + "]";
	}

	public Order(int order_Id, java.util.Date orederDate, String shoping_Adress, int cid) {
		super();
		this.order_Id = order_Id;
		this.orederDate = orederDate;
		Shoping_Adress = shoping_Adress;
		customer_id = cid;
	}

	private double weight;
	private double distance;
	private String region;
	private double shippingCost;
	private String shippingCompany;

	// Getter and Setter for weight
	public double getWeight() {
		return this.weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	// Getter and Setter for distance
	public double getDistance() {
		return this.distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	// Getter and Setter for region
	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	// Getter and Setter for shipping cost
	public double getShippingCost() {
		return this.shippingCost;
	}

	public void setShippingCost(double shippingCost) {
		this.shippingCost = shippingCost;
	}

	// Getter and Setter for shipping company
	public String getShippingCompany() {
		return this.shippingCompany;
	}

	public void setShippingCompany(String shippingCompany) {
		this.shippingCompany = shippingCompany;
	}

	public String getTotalPrice() {
		return String.valueOf(this.shippingCost);
	}

	public String getCustomerName() {
		return "Customer #" + this.customer_id;
	}

	public String getAddress() {
		return this.Shoping_Adress;
	}

	// Getter and Setter for shippingStatus
	public String getShippingStatus() {
		return this.shippingStatus;
	}

	public void setShippingStatus(String shippingStatus) {
		this.shippingStatus = shippingStatus;
	}

	public void setTotalPrice(double totalPrice) {
		this.totalPrice = totalPrice;
	}

}