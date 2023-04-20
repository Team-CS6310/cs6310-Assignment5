package edu.gatech.cs6310;

import java.util.HashMap;
import java.util.Map;

public class Customer {
    private String customerID;
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private int rating;
    private int credit;
    private int pendingCredit;
    private final int[] location; // x=location[0],y=location[y]
    private Map<String, Order> orders;







    public Customer(String customerID, String firstName, String lastName, String phoneNumber, int rating, int credit, int location_x, int location_y){
        this.customerID = customerID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.rating = rating;
        this.credit = credit;
        this.location = new int[]{location_x, location_y};;
        this.pendingCredit = credit;
        this.orders = new HashMap<>();
    }

    public String getCustomerID() {
        return customerID;
    }

    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getCredit() {
        return credit;
    }
    public int[] getLocation(){
        return location;
    }

    public void setLocation(int location_x, int location_y){
        this.location[0] = location_x;
        this.location[1] = location_y;

    }





    public void start_order(Order newOrder) {
        String orderID = newOrder.getOrderID();

        orders.put(orderID, newOrder);
    }


    public int setPendingCredit(int totalPrice) {
        pendingCredit -= totalPrice;
        return pendingCredit;
    }

    public void completeOrder(String orderID, int price){
        this.credit -= price;
        this.orders.remove(orderID);
    }


    public void droneAttacked(String orderID) {
        this.credit +=10;
    }
    public void cancelOrder(String orderID){
        this.orders.remove(orderID);
    }


    public int getPendingCredit() {
        return pendingCredit;
    }
}
