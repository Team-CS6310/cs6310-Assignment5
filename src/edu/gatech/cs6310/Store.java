package edu.gatech.cs6310;

import java.util.*;

public class Store {
    public String storeName;
    public int revenue;
    public int purchases;
    public int overloads;

    private int[] location; // x=location[0],y=location[y]




    public Map<String, Integer> catalog;//item name -> weight

    public Map<String, Drone> drones; // drone ID -> drone object
    public Map<String, Order> orders; // order ID -> order

    public Store(String storeName, int revenue, int location_x, int location_y) {
        this.storeName = storeName;
        this.revenue = revenue;
        this.location = new int[]{location_x, location_y};
        this.catalog = new HashMap<String, Integer>();
        this.drones = new HashMap<>();
        this.orders = new HashMap<>();
        this.purchases = 0;
        this.overloads = 0;


    }


    public String getName() {
        return storeName;
    }

    public int getRevenue() {
        return revenue;
    }
    public int getPurchases(){
        return purchases;
    }
    public int getOverloads(){
        return overloads;
    }

    public int[] getLocation(){
        return location;
    }

    public void setLocation(int location_x, int location_y){
        this.location[0] = location_x;
        this.location[1] = location_y;

    }
    public HashMap<String, Order> getOrders() {
        return (HashMap<String, Order>) orders;
    }

    public Map<String, Integer> getCatalog() {
        return catalog;
    }


    public Map<String, Drone> getDrones() {
        return drones;
    }

    public void setDrones(Map<String, Drone> drones) {
        this.drones = drones;
    }



    public boolean addItem(String itemName, int itemWeight) {
        if (catalog.containsKey(itemName)) {
            return false; // item already exists
        }
        catalog.put(itemName, itemWeight);
        return true; // item added successfully
    }


    public String sell_item(String storeName, String itemName, int itemWeight) {
        if (!addItem(itemName, itemWeight)) {
            return "ERROR:item_identifier_already_exists";
        }
        return "OK:change_completed";
    }


    public void display_items() {
        List<String> itemNames = new ArrayList<>(this.catalog.keySet());
        Collections.sort(itemNames);
        for (String itemName : itemNames) {
            System.out.println(itemName + "," + this.catalog.get(itemName));
        }
    }

    public String make_drone(Store store, String droneID, int capacity, int fuel, int flySpeed) {
        // Check if the drone identifier already exists
        if (drones.containsKey(droneID)) {
            return "ERROR:drone_identifier_already_exists";
        }else{
            Drone drone = new Drone(store, droneID, capacity, fuel, flySpeed);
            drones.put(droneID, drone);
        }
        return "OK:change_completed";
    }



    public String display_drones() {
        List<String> droneIDs = new ArrayList<>(drones.keySet());
        Collections.sort(droneIDs);

        for (String droneID : droneIDs) {
            Drone drone = drones.get(droneID);
            String flownBy = "";
            if (drone.getPilot() != null) {
                flownBy = ",flown_by:" + drone.getPilot().getFirstName() + "_" + drone.getPilot().getLastName();
            }
            System.out.println("droneID:" + droneID + ",total_cap:" + drone.getCapacity() +
                    ",num_orders:" + drone.getNum_orders() + ",remaining_cap:" + drone.getRemainingCapacity() +
                    ",trips_left:" + drone.getFuel() + ",flySpeed:" + drone.getFlySpeed() + flownBy);
        }

        return "OK:display_completed";
    }


    public void add_order(Order newOrder) {
        String orderID = newOrder.getOrderID();
        orders.put(orderID, newOrder);
    }







    public void display_orders() {
        List<String> orderList = new ArrayList<>(orders.keySet());
        Collections.sort(orderList);
        for (String orderID : orderList) {
            Order order = orders.get(orderID);
            System.out.println("orderID" + ":" + orderID);
            Map<String, Order.OrderLine> orderLines = new TreeMap<>(order.getOrderLines());
            for (String itemName : orderLines.keySet()) {
                Order.OrderLine orderLine = orderLines.get(itemName);
                System.out.println("item_name:" + itemName + ",total_quantity:" + orderLine.getQuantity() + ",total_cost:" +
                        orderLine.getTotalPrice() + ",total_weight:" + orderLine.getTotalWeight());
            }

        }
    }

    public void completeOrder(String orderID, int price){
        this.revenue += price;
        this.orders.remove(orderID);
        this.purchases += 1;
    }

    public void birdAttack(String orderID) {
        this.revenue -=50;
    }

    public void cancelOrder(String orderID){
        this.orders.remove(orderID);
    }


    public int overLoads(String orderID){
        Order order = orders.get(orderID);
        Drone drone = order.getDrone();
        int addon = drone.getNum_orders()-1;
        this.overloads += addon;
        return overloads;
    }


}







