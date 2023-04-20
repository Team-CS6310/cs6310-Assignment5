package edu.gatech.cs6310;

import java.text.DateFormat;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class Drone {
    private String droneID;
    private int capacity;
    private int fuel;
    private int num_orders;
    private int remaining_cap;
    private Store store;
    private Pilot pilot;

    private int[] location;
    public Map<String, Order> orders = new TreeMap<>();

    HashMap<String, Store> stores = new HashMap<>();

    Map<String, Customer> customers = new HashMap<>();
    HashMap<String, AngryBird> angrybirds = new HashMap<>();



    private int flySpeed;

    public Drone(Store store, String droneID, int capacity, int fuel, int flySpeed){
        this.droneID = droneID;
        this.store = store;
        this.capacity = capacity;
        this.fuel = fuel;
        this.num_orders = 0;
        this.remaining_cap = capacity;
        this.flySpeed = flySpeed;
        this.location = store.getLocation();

    }

    // Overloaded constructor with additional attributes
    public Drone(Store store, String droneID, int capacity, int fuel, int[] location, int flySpeed) {
        this.store = store;
        this.droneID = droneID;
        this.capacity = capacity;
        this.fuel = fuel;
        this.num_orders = 0;
        this.remaining_cap = capacity;
        this.location = store.getLocation();


    }

    public Store getStore() {
        return store;
    }

    public String getDroneID(){
        return droneID;
    }

    public int getCapacity(){
        return capacity;
    }


    public int getFuel(){
        return fuel; // remaining  number of deliveries that the drone can make before needing refueling
    }

    public int setNumOrders(){
        num_orders += 1;
        return num_orders;
    }

    public int getNum_orders(){
        return num_orders;
    }





    public int setRemainingCapacity(int weight){
        remaining_cap -= weight;
        return remaining_cap;
    }

    public int getRemainingCapacity(){
        return remaining_cap;
    }

    public Pilot getPilot() {
        return pilot;
    }

    public void setPilot(Pilot pilot) {
        this.pilot = pilot;
    }

    public void deliverOrder(String orderID, int weight){
        this.remaining_cap += weight;
        this.fuel -= 1;
        this.num_orders -= 1;
        this.orders.remove(orderID);
    }


    public void attacked(String orderID) {
        this.fuel -= 1;
    }

    public void cancelOrder(String orderID, int weight){

        this.orders.remove(orderID);
        this.num_orders -= 1;
        this.remaining_cap += weight;
    }



    public void transfer_order(Drone newDrone, String orderID, int weight){
        this.num_orders -= 1;
        this.orders.remove(orderID);
        newDrone.num_orders += 1;
        orders.remove(orderID);
        this.remaining_cap += weight;
        newDrone.remaining_cap -= weight;
    }


    public void updateLocation(Order order, int flySpeed, double timePassed) {
        int[] customerLocation = order.getCustomer().getLocation();
        int[] storeLocation = store.getLocation();

        // Calculate the distance between the store and the customer
        double distance = Math.sqrt(Math.pow(customerLocation[0] - storeLocation[0], 2) + Math.pow(customerLocation[1] - storeLocation[1], 2));

        double distanceTraveled = flySpeed * timePassed;

        // Update the drone's location
        double ratio = distanceTraveled / distance;
        this.location[0] = (int) Math.round(storeLocation[0] + (customerLocation[0] - storeLocation[0]) * ratio);
        this.location[1] = (int) Math.round(storeLocation[1] + (customerLocation[1] - storeLocation[1]) * ratio);
    }

    public boolean isAttacked(Order order, HashMap<String, AngryBird> angrybirds) {
        int[] customerLocation = order.getCustomer().getLocation();
        int[] storeLocation = store.getLocation();
        double distance = Math.sqrt(Math.pow(storeLocation[0] - customerLocation[0], 2) + Math.pow(storeLocation[1] - customerLocation[1], 2));
        double time = distance / flySpeed;

        for (double timePassed = 0; timePassed <= time; timePassed += 0.1) {
            this.updateLocation(order, flySpeed, timePassed);
            for (AngryBird bird : angrybirds.values()) {
                bird.updateLocation(stores, customers, timePassed);
                int[] birdLocation = bird.getLocation();
                double distanceToDrone = Math.sqrt(Math.pow(birdLocation[0] - this.location[0], 2) + Math.pow(birdLocation[1] - this.location[1], 2));
                if (distanceToDrone < bird.getAttackProbability() * 100) {
                    return true;
                }
            }
        }
        return false;
    }

    private int[] getLocation() {
        return this.location;
    }


    public int getFlySpeed() {
        return flySpeed;
    }
}
