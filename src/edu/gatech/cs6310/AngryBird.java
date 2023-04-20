package edu.gatech.cs6310;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AngryBird {
    private String birdID;

    private int[] location; // x=location[0],y=location[y]

    private double attackProbability;
    private int changeFrequency;

    HashMap<String, Store> stores = new HashMap<>();

    Map<String, Customer> customers = new HashMap<>();
    private double flySpeed;
    private int changeCounter;

    public AngryBird(String birdID, double attackProbability, int location_x, int location_y, int changeFrequency){
        this.birdID = birdID;
        this.attackProbability = attackProbability;
        this.location = new int[]{location_x, location_y};
        this.changeFrequency = changeFrequency;
    }


    public String getBirdID() {
        return birdID;
    }

    public double getAttackProbability() {
        return attackProbability;
    }

    public int getChangeFrequency() {
        return changeFrequency;
    }

    public int[] getLocation(){
        return location;
    }

    public void setLocation(int location_x, int location_y){
        this.location[0] = location_x;
        this.location[1] = location_y;

    }

    public void updateLocation(HashMap<String, Store> stores, Map<String, Customer> customers, double timePassed) {
        if (this.changeCounter <= this.changeFrequency) {
            // Get the range of x and y coordinates of stores and customers
            int[] rangeX = {Integer.MAX_VALUE, Integer.MIN_VALUE};
            int[] rangeY = {Integer.MAX_VALUE, Integer.MIN_VALUE};
            for (Store store : stores.values()) {
                int[] location = store.getLocation();
                rangeX[0] = Math.min(rangeX[0], location[0]);
                rangeX[1] = Math.max(rangeX[1], location[0]);
                rangeY[0] = Math.min(rangeY[0], location[1]);
                rangeY[1] = Math.max(rangeY[1], location[1]);
            }
            for (Customer customer : customers.values()) {
                int[] location = customer.getLocation();
                rangeX[0] = Math.min(rangeX[0], location[0]);
                rangeX[1] = Math.max(rangeX[1], location[0]);
                rangeY[0] = Math.min(rangeY[0], location[1]);
                rangeY[1] = Math.max(rangeY[1], location[1]);
            }

            // Generate a random location within the range of stores' and customers' locations
            int x = (int) (Math.random() * (rangeX[1] - rangeX[0] + 1)) + rangeX[0];
            int y = (int) (Math.random() * (rangeY[1] - rangeY[0] + 1)) + rangeY[0];
            int[] newLocation = {x, y};

            // Calculate the distance and time it takes to reach the location
            double distance = Math.sqrt(Math.pow(newLocation[0] - this.location[0], 2) + Math.pow(newLocation[1] - this.location[1], 2));
            double time = distance / this.flySpeed;

            // Update the location and reset the counter
            if (time <= timePassed) {
                this.location = newLocation;
                this.changeCounter = 0;
            } else {
                double ratio = timePassed / time;
                this.location[0] = (int) Math.round(this.location[0] + (newLocation[0] - this.location[0]) * ratio);
                this.location[1] = (int) Math.round(this.location[1] + (newLocation[1] - this.location[1]) * ratio);
            }
        } else {
            this.changeCounter++;
        }
    }

    public void setAttackProbability(double attackProbability) {
        this.attackProbability = attackProbability;
    }
}
