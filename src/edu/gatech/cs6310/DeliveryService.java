package edu.gatech.cs6310;

import java.util.*;

//thomas update begin
import java.util.concurrent.ExecutorService;
import java.sql.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
//thomas update end

public class DeliveryService {
    //thomas update begin
    private static Connection connection = null;
    public void connect() {

        //String url = "jdbc:mysql://mysql:3306/mysql?user=username&password=password";
        String url = "jdbc:mysql://mysql:3306/db20236310?user=username&password=password";

        try {
            connection = DriverManager.getConnection(url);
            System.out.println("database connect ok");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Store> stores = new LinkedHashMap<>();

    public static void display_stores_ops(Connection connection) {
        if (stores.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            try {
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery("select storeName, revenue, locationx, locationy from Stores");
                while (rs.next()) {
                    String name = rs.getString("storeName");
                    int value = rs.getInt("revenue");
                    int value1 = rs.getInt("locationx");
                    int value2 = rs.getInt("locationy");
                    //System.out.println("name:" + name + ",revenue:" + value);
                    sb.append("name:").append(name).append(",revenue:").append(value).
                            append(",location:").append(value1).append(",").append(value2).append("\n");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (stores.isEmpty()) {
            System.out.println("No stores found");
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (Store store : stores.values()) {
            sb.append("name:").append(store.getName()).append(",revenue:").append(store.getRevenue()).append("\n");
        }
        System.out.println(sb.toString().trim());
    }



    public void LoadTest() {

        int numIterations = 100;
        int numThreads = 5;

        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    long startTime = System.currentTimeMillis();
                    Class.forName("com.mysql.cj.jdbc.Driver");
                    Connection connection = DriverManager.getConnection("jdbc:mysql://mysql:3306/db20236310", "username", "password");
                    for (int j = 0; j < numIterations; j++) {
                        Statement statement = connection.createStatement();
                        ResultSet resultSet = statement.executeQuery("SELECT * FROM Stores");
                        resultSet.close();
                        statement.close();
                    }
                    connection.close();
                    long endTime = System.currentTimeMillis();
                    System.out.println(Thread.currentThread().getName() + " completed in " + (endTime - startTime) + " ms");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //thomas update end

    public void commandLoop() {

        //thomas update begin
        connect();
        //thomas update end


        Scanner commandLineInput = new Scanner(System.in);
        String wholeInputLine;
        String[] tokens;
        final String DELIMITER = ",";

        HashMap<String, Store> stores = new HashMap<>();
        HashMap<String, Pilot> pilots = new HashMap<>();
        Map<String, Customer> customers = new HashMap<>();
        HashMap<String, Store> drones = new HashMap<>();
        HashMap<String, AngryBird> angrybirds = new HashMap<>();
        Map<String, Order> orders; // order ID -> order
        int successfulTransfers = 0;






        while (true) {
            try {
                // Determine the next command and echo it to the monitor for testing purposes
                wholeInputLine = commandLineInput.nextLine();
                tokens = wholeInputLine.split(DELIMITER);
                System.out.println("> " + wholeInputLine);
                if (wholeInputLine.startsWith("//")) continue;

                if (tokens[0].equals("make_store")) {
                    //System.out.println("store: " + tokens[1] + ", revenue: " + tokens[2] + ", location_x: " + tokens[3] + ", location_y: " + tokens[4]);

                    String storeName = tokens[1];
                    int revenue = Integer.parseInt(tokens[2].trim());
                    int location_x = Integer.parseInt(tokens[3].trim());
                    int location_y = Integer.parseInt(tokens[4].trim());
                    if (stores.containsKey(storeName)) {
                        System.out.println("ERROR:store_identifier_already_exists");
                    }else{
                        Store store = new Store(storeName, revenue, location_x, location_y);
                        stores.put(storeName, store);
                        //thomas update begin
                        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO Stores (storeName, revenue, locationx, locationy) VALUES (?, ?, ?, ?)")) {
                            stmt.setString(1, storeName);
                            stmt.setString(2, String.valueOf(revenue));
                            stmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("no: sql not insert");
                        }
                        //thomas update end
                        System.out.println("OK:change_completed");
                    }

                    //thomas update begin
                } else if (tokens[0].equals("load_test")) {
                    LoadTest();
                } else if (tokens[0].equals("display_stores_ops")) {
                    display_stores_ops(connection);
                    //thomas update end

                } else if (tokens[0].equals("display_stores")) {
                    //System.out.println("no parameters needed");
                    if (stores.isEmpty()) {
                        System.out.println("No stores found");
                    }
                    List<Store> sortedStores = new ArrayList<>(stores.values());
                    Collections.sort(sortedStores, Comparator.comparing(Store::getName)); // Sort by name
                    StringBuilder sb = new StringBuilder();
                    for (Store store : sortedStores) {
                        sb.append("name:").append(store.getName()).append(",revenue:").append(store.getRevenue()).
                                append(",location:").append(Arrays.toString(store.getLocation())).append("\n");
                    }
                    //System.out.println(sb.toString().trim());

                    //thomas update begin
                    try {
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("select storeName, revenue, locationx, locationy from Stores");
                        while (rs.next()) {
                            String name = rs.getString("storeName");
                            int value = rs.getInt("revenue");
                            int value1 = rs.getInt("locationx");
                            int value2 = rs.getInt("locationy");
                            //System.out.println("name:" + name + ",revenue:" + value);
                            sb.append("name:").append(name).append(",revenue:").append(value).
                                    append(",location:").append(value1).append(",").append(value2).append("\n");
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //thomas update end

                    System.out.println("OK:display_completed");


                } else if (tokens[0].equals("sell_item")) {
                    //System.out.println("store: " + tokens[1] + ", item: " + tokens[2] + ", weight: " + tokens[3]);

                    String storeName = tokens[1];
                    String itemName = tokens[2];
                    int itemWeight = Integer.parseInt(tokens[3].trim());
                    if (!stores.containsKey(storeName)) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }else{
                    Store store = stores.get(storeName);
                    String result = store.sell_item(storeName, itemName, itemWeight);

                        //thomas update begin
                        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO Items (storeName, itemName, itemWeight) VALUES (?, ?, ?)")) {
                            stmt.setString(1, storeName);
                            stmt.setString(2, itemName);
                            stmt.setString(3, String.valueOf(itemWeight));
                            stmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("no: sql not insert");
                        }
                        // thomas update end

                        System.out.println(result);}



                } else if (tokens[0].equals("display_items")) {
                    //System.out.println("store: " + tokens[1]);
                    String storeName = tokens[1];
                    if (!stores.containsKey(storeName)) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }else {
                        Store store = stores.get(storeName);
                        //store.display_items();
                        //thomas update begin
                        try {
                            Statement stmt = connection.createStatement();
                            System.out.println("select itemName, itemWeight from Items where storeName = ''" + tokens[1] + "''");
                            ResultSet rs = stmt.executeQuery("SELECT itemName, itemWeight FROM Items WHERE storeName = '" + tokens[1].replaceAll("'", "''") + "'");
                            //ResultSet rs = stmt.executeQuery("select itemName, itemWeight from Items where storeName = ''" + tokens[1] + "''");
                            System.out.println("check1");
                            while (rs.next()) {
                                System.out.println("check2");
                                String name = rs.getString("itemName");
                                int value = rs.getInt("itemWeight");
                                System.out.println(name + "," + value);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        //thomas update end
                        System.out.println("OK:display_completed");
                    }


                } else if (tokens[0].equals("make_pilot")) {
                    //System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
                    //System.out.println(", phone: " + tokens[4] + ", tax: " + tokens[5] + ", license: " + tokens[6] + ", experience: " + tokens[7]);
                    String pilotAccount = tokens[1];
                    String firstName = tokens[2];
                    String lastName = tokens[3];
                    String phoneNumber = tokens[4];
                    String taxID = tokens[5];
                    String licenseID = tokens[6];
                    int successfulDelivery = Integer.parseInt(tokens[7].trim());

                    if (pilots.containsKey(pilotAccount)) {
                        System.out.println("ERROR:pilot_identifier_already_exists");
                    } else {
                        // Check if the license ID already exists for another pilot
                        boolean licenseExists = false;
                        for (Pilot pilot : pilots.values()) {
                            if (pilot.getLicenseID().equals(licenseID)) {
                                licenseExists = true;
                                break;
                            }
                        }
                        if (licenseExists) {
                            System.out.println("ERROR:pilot_license_already_exists");
                        } else {
                            // Create a new pilot and add it to the map
                            Pilot newPilot = new Pilot(pilotAccount, firstName, lastName, phoneNumber, taxID, licenseID, successfulDelivery);
                            pilots.put(pilotAccount, newPilot);
                            //thomas update begin
                            try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO Pilots (pilotAccount, firstName, lastName, phoneNumber, taxID, licenseID, successfulDelivery) VALUES (?, ?, ?, ?, ?, ?, ?)")) {
                                stmt.setString(1, pilotAccount);
                                stmt.setString(2, firstName);
                                stmt.setString(3, lastName);
                                stmt.setString(4, phoneNumber);
                                stmt.setString(5, taxID);
                                stmt.setString(6, licenseID);
                                stmt.setString(7, String.valueOf(successfulDelivery));
                                stmt.executeUpdate();
                            } catch (SQLException e) {
                                System.out.println("no: sql not insert");
                            }
                            // thomas update end
                            System.out.println("OK:change_completed");
                        }
                    }
                } else if (tokens[0].equals("display_pilots")) {

                    // Get a list of all the pilots' identifiers
                    List<String> identifiers = new ArrayList<>(pilots.keySet());
                    // Sort the identifiers in ascending order
                    Collections.sort(identifiers);
                    // Loop through the sorted identifiers and display the information of each pilot
                    for (String identifier : identifiers) {
                        Pilot pilot = pilots.get(identifier);
                        System.out.println("name:" + pilot.getFirstName() + "_" + pilot.getLastName() + ",phone:" + pilot.getPhoneNumber() + ",taxID:" +
                                pilot.getTaxID() + ",licenseID:" + pilot.getLicenseID() + ",experience:" +
                                pilot.getSuccessfulDelivery());
                    }
                    //thomas update begin
                    try {
                        Statement stmt = connection.createStatement();
                        ResultSet rs = stmt.executeQuery("select pilotAccount, firstName, lastName, phoneNumber, taxID, licenseID, successfulDelivery from Pilots");
                        System.out.println("check1");
                        while (rs.next()) {
                            System.out.println("check2");
                            String name = rs.getString("pilotAccount");
                            String name1 = rs.getString("firstName");
                            String name2 = rs.getString("lastName");
                            String name3 = rs.getString("phoneNumber");
                            String name4 = rs.getString("taxID");
                            String name5 = rs.getString("licenseID");
                            int value = rs.getInt("successfulDelivery");
                            //System.out.println(name + "\t" + name1 + "\t" + name2 + "\t" + name3 + "\t" + name4 + "\t" + name5 + "\t" + value);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    //thomas update end
                    System.out.println("OK:display_completed");


                } else if (tokens[0].equals("make_drone")) {
                    //System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", capacity: " + tokens[3] + ", fuel: " + tokens[4] + ", flySpeed: " + tokens[5]);

                    String storeName = tokens[1];
                    String droneID = tokens[2];
                    int capacity = Integer.parseInt(tokens[3]);
                    int fuel = Integer.parseInt(tokens[4]);
                    int flySpeed = Integer.parseInt(tokens[5]);
                    // Check if the store exists
                    if (!stores.containsKey(storeName)) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }else {
                        Store store = stores.get(storeName);
                        String result = store.make_drone(store, droneID, capacity, fuel, flySpeed);
                        //thomas update begin
                        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO Drones (storeName, droneID, capacity, fuel, numOrders, remainingCap) VALUES (?, ?, ?, ?, ?, ?)")) {
                            stmt.setString(1, tokens[1]);
                            stmt.setString(2, tokens[2]);
                            stmt.setString(3, tokens[3]);
                            stmt.setString(4, tokens[4]);
                            stmt.setString(5, String.valueOf(0));
                            stmt.setString(6, String.valueOf(0));
                            stmt.executeUpdate();
                        } catch (SQLException e) {
                            System.out.println("no: sql not insert");
                        }
                        // thomas update end
                        System.out.println(result);
                    }


                } else if (tokens[0].equals("display_drones")) {
                    //System.out.println("store: " + tokens[1]);
                    if (!stores.containsKey(tokens[1])) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }else {
                        Store store = stores.get(tokens[1]);
                        String result = store.display_drones();
                        System.out.println(result);
                    }


                } else if (tokens[0].equals("fly_drone")) {
                    //System.out.println("store: " + tokens[1] + ", drone: " + tokens[2] + ", pilot: " + tokens[3]);
                    String storeName = tokens[1];
                    String droneID = tokens[2];
                    String pilotAccount = tokens[3];
                    // Check if the store and drone exist
                    if (!stores.containsKey(storeName)) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    } else {
                        Store store = stores.get(storeName);
                        if (!store.getDrones().containsKey(droneID)) {
                            System.out.println("ERROR:drone_identifier_does_not_exist");
                        } else {
                            Drone drone = store.getDrones().get(droneID);
                            // Check if the pilot exists
                            if (!pilots.containsKey(pilotAccount)) {
                                System.out.println("ERROR:pilot_identifier_does_not_exist");
                            } else {
                                Pilot pilot = pilots.get(pilotAccount);
                                 //Check if the drone is already being flown by another pilot
                                if (drone.getPilot() != null && !drone.getPilot().equals(pilot)) {
                                    // Replace the pilot of the drone
                                    Pilot oldPilot = drone.getPilot();
                                    oldPilot.setDrone(null);
                                    drone.setPilot(pilot);
                                    pilot.setDrone(drone);
                                    System.out.println("OK:change_completed");
                                } else {
                                    // Assign the pilot to the drone
                                    if (pilot.getDrone() != null && !pilot.getDrone().equals(drone)) {
                                        // If the pilot is already flying another drone, remove the pilot from that drone
                                        Drone oldDrone = pilot.getDrone();
                                        oldDrone.setPilot(null);
                                    }
                                    drone.setPilot(pilot);
                                    pilot.setDrone(drone);
                                    System.out.println("OK:change_completed");
                                }
                            }
                        }
                    }
                } else if (tokens[0].equals("make_customer")) {
                    //System.out.print("account: " + tokens[1] + ", first_name: " + tokens[2] + ", last_name: " + tokens[3]);
                    //System.out.println(", phone: " + tokens[4] + ", rating: " + tokens[5] + ", credit: " + tokens[6])
                    // System.out.println(", location_x: " + tokens[7] + ", location_y: " + tokens[8]);
                    String customerID = tokens[1];
                    String firstName = tokens[2];
                    String lastName = tokens[3];
                    String phoneNumber = tokens[4];
                    int rating = Integer.parseInt(tokens[5]);
                    int credit = Integer.parseInt(tokens[6]);
                    int location_x = Integer.parseInt(tokens[7].trim());
                    int location_y = Integer.parseInt(tokens[8].trim());

                    if (customers.containsKey(customerID)) {
                        System.out.println("ERROR:customer_identifier_already_exists");
                    } else {
                        Customer customer = new Customer(customerID, firstName, lastName, phoneNumber, rating, credit, location_x, location_y);
                        customers.put(customerID, customer);
                        System.out.println("OK:change_completed");
                    }

                } else if (tokens[0].equals("display_customers")) {
                    //System.out.println("no parameters needed");
                    // Get a list of all the customer identifiers
                    List<String> identifiers = new ArrayList<>(customers.keySet());
                    // Sort the identifiers in ascending order
                    Collections.sort(identifiers);
                    // Loop through the sorted identifiers and display the information of each customer
                    for (String identifier : identifiers) {
                        Customer customer = customers.get(identifier);
                        System.out.println("name:" + customer.getFirstName() + "_" + customer.getLastName() +
                                ",phone:" + customer.getPhoneNumber() + ",rating:" + customer.getRating() +
                                ",credit:" + customer.getCredit() + ",location:" + Arrays.toString(customer.getLocation()));
                    }
                    System.out.println("OK:display_completed");

                } else if (tokens[0].equals("start_order")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", drone: " + tokens[3] + ", customer: " + tokens[4]);
                    String storeName = tokens[1];
                    String orderID = tokens[2];
                    String droneID = tokens[3];
                    String customerID = tokens[4];
                    // check if store name exists when start the order
                    if (!stores.containsKey(storeName)) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }else {
                        if (!customers.containsKey(customerID)) {
                            System.out.println("ERROR:customer_identifier_does_not_exist");
                        } else {
                            Store store = stores.get(storeName);

                            if (store.getOrders().containsKey(orderID)) {
                                System.out.println("ERROR:order_identifier_already_exists");
                            } else {
                                if (!store.getDrones().containsKey(droneID)) {
                                    System.out.println("ERROR:drone_identifier_does_not_exist");
                                } else {
                                    // create new order with orderId and customer
                                    Customer customer  = customers.get(customerID);
                                    Drone drone = store.getDrones().get(droneID);
                                    Order newOrder = new Order(store, orderID, drone, customer);
                                    store.add_order(newOrder);
                                    customer.start_order(newOrder);
                                    drone.setNumOrders();
                                    System.out.println("OK:change_completed");
                                }
                            }
                        }
                    }

                } else if (tokens[0].equals("display_orders")) {
                    //System.out.println("store: " + tokens[1]);
                    String storeName = tokens[1];
                    if (!stores.containsKey(storeName)) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }else{
                        Store store = stores.get(storeName);
                        store.display_orders();
                        System.out.println("OK:display_completed");
                    }


                } else if (tokens[0].equals("request_item")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", item: " + tokens[3] + ", quantity: " + tokens[4] + ", unit_price: " + tokens[5]);
                    String storeName = tokens[1];
                    String orderID = tokens[2];
                    String itemName = tokens[3];
                    int quantity = Integer.parseInt(tokens[4]);
                    int unitPrice = Integer.parseInt(tokens[5]);

                    Store store = stores.get(storeName);
                    if (store == null) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }else {
                        Order order = store.getOrders().get(orderID);
                        if (order == null) {
                            System.out.println("ERROR:order_identifier_does_not_exist");
                        }else {
                            if (!store.getCatalog().containsKey(itemName)) {
                                System.out.println("ERROR:item_identifier_does_not_exist");
                            }else {
                                if (order.getOrderLines().containsKey(itemName)) {
                                    System.out.println("ERROR:item_already_ordered");
                                }else {
                                    order.request_item(itemName, quantity, unitPrice);
                                }
                            }
                        }
                    }


                } else if (tokens[0].equals("purchase_order")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                    String storeName = tokens[1];
                    String orderID = tokens[2];
                    Store store = stores.get(storeName);
                    if (store == null) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }else {
                        Order order = store.getOrders().get(orderID);
                        if (order == null) {
                            System.out.println("ERROR:order_identifier_does_not_exist");
                        }else{
                            Drone drone = order.getDrone();
                            if(drone.getFuel() < 1){
                                System.out.println("ERROR:drone_needs_fuel");
                            }else{
                                if (drone.getPilot() == null){
                                    System.out.println("ERROR:drone_needs_pilot");
                                }else {
                                    if (drone.isAttacked(order, angrybirds)){
                                        // System.out.println("Total delivery distance:"+order.getDrone().getDelieveryDistance(order,order.getDrone().getFlySpeed()));
                                        System.out.println("drone_is_attacked_by_the_bird and will go back to store for repair");

                                        store.birdAttack(orderID);
                                        System.out.println("Repair cost due to the attack:$50"); // will deduct from store revenue

                                        Customer customer = order.getCustomer();
                                        store.overLoads(orderID);
                                        customer.droneAttacked(orderID);
                                        System.out.println("Offer $10 credit to the customer"); // increase customer's credit

                                        drone.attacked(orderID);
                                        System.out.println("drone lost 1 Galon fuel due to the bird attach");
                                        System.out.println("updated fuel:"+drone.getFuel());
                                        // System.out.println("Extra distance drone needs to fly due to the attack:"+2*(order.getDrone().getTimepassed(order,angrybirds))*(drone.getFlySpeed()));


                                    }else{
                                        Customer customer = order.getCustomer();
                                        int order_weight = order.weight;
                                        int cost = order.cost;

                                        store.overLoads(orderID);
                                        customer.completeOrder(orderID, cost);
                                        store.completeOrder(orderID, cost);
                                        drone.deliverOrder(orderID, order_weight);
                                        drone.getPilot().deliverOrder();

                                        System.out.println("OK:no_attack_delivery_succeed");

                                    }
                                }
                            }
                        }
                    }


                } else if (tokens[0].equals("cancel_order")) {
                    //System.out.println("store: " + tokens[1] + ", order: " + tokens[2]);
                    String storeName = tokens[1];
                    String orderID = tokens[2];
                    Store store = stores.get(storeName);
                    if (store == null) {
                        System.out.println("ERROR:store_identifier_does_not_exist");
                    }else {
                        Order order = store.getOrders().get(orderID);
                        if (order == null) {
                            System.out.println("ERROR:order_identifier_does_not_exist");
                        }else{
                            Drone drone = order.getDrone();
                            Customer customer = order.getCustomer();
                            drone.cancelOrder(orderID, order.weight);
                            customer.cancelOrder(orderID);
                            store.cancelOrder(orderID);
                            System.out.println("OK:change_completed");

                        }
                    }


                } else if (tokens[0].equals("make_bird")) {
                    // System.out.println("bird: " + tokens[1] + ", attackProbability: " + tokens[2] + ", location_x: "
                    // + tokens[3] + ", location_y: " + tokens[4] + ", changeFrequency: " + tokens[5]);
                    String birdID = tokens[1];
                    double attackProbability = Double.parseDouble(tokens[2]);
                    int location_x = Integer.parseInt(tokens[3]);
                    int location_y = Integer.parseInt(tokens[4]);
                    int changeFrequency = Integer.parseInt(tokens[5]);

                    if (angrybirds.containsKey(birdID)) {
                        System.out.println("ERROR:bird_identifier_already_exists");
                    } else {
                        AngryBird angrybird = new AngryBird(birdID, attackProbability, location_x, location_y, changeFrequency);
                        angrybirds.put(birdID, angrybird);
                        System.out.println("OK:change_completed");
                    }

                } else if (tokens[0].equals("set_bird_attack_prob")) {
                    String birdID = tokens[1];
                    double attackProbability = Double.parseDouble(tokens[2]);
                    if (angrybirds.containsKey(birdID)) {
                        AngryBird angrybird = angrybirds.get(birdID);
                        angrybird.setAttackProbability(attackProbability);
                        System.out.println("OK:change_completed");
                    } else {
                        System.out.println("ERROR:bird_identifier_not_found");
                    }
                } else if (tokens[0].equals("set_num_birds")) {
                    int numBirds = Integer.parseInt(tokens[1]);
                    if (numBirds < 0) {
                        System.out.println("ERROR:invalid_number_of_birds");
                    } else {
                        int currentNumBirds = angrybirds.size();
                        if (numBirds > currentNumBirds) {
                            // add birds
                            for (int i = currentNumBirds; i < numBirds; i++) {
                                String birdID = "bird_" + (i + 1);
                                double attackProbability = 0.1;
                                int location_x = 0;
                                int location_y = 0;
                                int changeFrequency = 1;
                                AngryBird angrybird = new AngryBird(birdID, attackProbability, location_x, location_y, changeFrequency);
                                angrybirds.put(birdID, angrybird);
                            }
                            System.out.println("OK:change_completed");
                        } else if (numBirds < currentNumBirds) {
                            // remove birds
                            List<String> birdsToRemove = new ArrayList<>();
                            int numToRemove = currentNumBirds - numBirds;
                            for (String birdID : angrybirds.keySet()) {
                                if (numToRemove == 0) {
                                    break;
                                }
                                birdsToRemove.add(birdID);
                                numToRemove--;
                            }
                            for (String birdID : birdsToRemove) {
                                angrybirds.remove(birdID);
                            }
                            System.out.println("OK:change_completed");
                        } else {
                            System.out.println("OK:change_completed");
                        }
                    }
                }else if (tokens[0].equals("display_birds")) {

                    // Get a list of all the pilots' identifiers
                    List<String> identifiers = new ArrayList<>(angrybirds.keySet());
                    // Sort the identifiers in ascending order
                    Collections.sort(identifiers);
                    // Loop through the sorted identifiers and display the information of each pilot
                    for (String identifier : identifiers) {
                        AngryBird angrybird = angrybirds.get(identifier);
                        System.out.println("birdID:" + angrybird.getBirdID() + ",attackProbability:" + angrybird.getAttackProbability() +
                                ",location:" + Arrays.toString(angrybird.getLocation()) + ",changeFrequency:" + angrybird.getChangeFrequency());
                    }
                    System.out.println("OK:display_completed");


                } else if (tokens[0].equals("transfer_order")) {
                    // System.out.println("store: " + tokens[1] + ", order: " + tokens[2] + ", new_drone: " + tokens[3]);
                    String storeName = tokens[1];
                    String orderID = tokens[2];
                    String droneID = tokens[3];
                    Store store = stores.get(storeName);
                    Order order = store.getOrders().get(orderID);
                    Drone olddrone = order.getDrone();
                    Drone newdrone = store.getDrones().get(droneID);


                    if (order.getDrone() == newdrone){
                        System.out.println("OK:new_drone_is_current_drone_no_change");
                    }else{
                        if (order.weight > newdrone.getRemainingCapacity()){
                            System.out.println("ERROR:new_drone_does_not_have_enough_capacity");
                        }else{
                            order.transfer_order(newdrone);
                            olddrone.transfer_order(newdrone, orderID, order.weight);
                            successfulTransfers++;
                            System.out.println("OK:change_completed");

                        }
                    }



                } else if (tokens[0].equals("display_efficiency")) {
                    // System.out.println("no parameters needed");
                    List<Store> sortedStores = new ArrayList<>(stores.values());
                    Collections.sort(sortedStores, Comparator.comparing(Store::getName)); // Sort by name
                    StringBuilder sb = new StringBuilder();

                    for (Store store : sortedStores) {

                        sb.append("name:").append(store.getName()).append(",purchases:").append(store.getPurchases()).
                                append(",overloads:").append(store.getOverloads() ).append(",transfers:").append(successfulTransfers);
                    }
                    System.out.println(sb.toString().trim());
                    System.out.println("OK:display_completed");

                } else if (tokens[0].equals("stop")) {
                    System.out.println("stop acknowledged");
                    break;

                } else {
                    System.out.println("command " + tokens[0] + " NOT acknowledged");
                }
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println();
            }
        }

        System.out.println("simulation terminated");
        commandLineInput.close();
    }
}
