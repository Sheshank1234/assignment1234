package com.example.CrudApplication.service;

import com.example.CrudApplication.model.*;
import com.example.CrudApplication.repo.DeliveryOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DeliveryOrderService {
    private static final double AVERAGE_SPEED_KMH = 20.0;
    private final DeliveryOrderRepository deliveryOrderRepository;
    private static final Logger log = LoggerFactory.getLogger(DeliveryOrderService.class);

    public DeliveryOrderService(DeliveryOrderRepository deliveryOrderRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
    }

    /**
     * Returns list of all delivery orders
     * @return - List of DeliveryOrder
     */
    public List<DeliveryOrder> getAllOrders() {
        try {
            return deliveryOrderRepository.findAll();
        } catch (Exception e) {
            log.error("Error fetching orders: {}", e.getMessage(), e);
            // Return an empty list to avoid breaking the application
            return Collections.emptyList();
        }
    }

    /**
     * For give two customers and two restaurants - this returns the path to be followed by delivery boy
     * @return - DeliveryRoute
     */
    public DeliveryRoute getActiveBatchForDB() {
        try {
            List<DeliveryOrder> orders = deliveryOrderRepository.findAll();
            log.error("List of orders are : {}",orders);
            Location deliveryBoyLocation = new Location("DB1", 12.9716, 77.5946);
            log.error("Delivery boy location is: {}",deliveryBoyLocation);
            return findOptimalPath(deliveryBoyLocation,orders.get(0),orders.get(1));
        }catch (Exception e) {
            log.error("Error fetching orders: {}", e.getMessage(), e);
            return null;
        }
    }

    // Haversine formula to calculate distance between two points
    private double calculateDistance(Location loc1, Location loc2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(loc2.getLatitude() - loc1.getLatitude());
        double lonDistance = Math.toRadians(loc2.getLongitude() - loc1.getLongitude());
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(loc1.getLatitude())) * Math.cos(Math.toRadians(loc2.getLatitude()))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    // Calculate total distance and time for a given path
    private double[] calculateTotalDistanceAndTime(Location[] path) {
        double totalDistance = 0;
        for (int i = 0; i < path.length - 1; i++) {
            totalDistance += calculateDistance(path[i], path[i + 1]);
        }
        double totalTime = totalDistance / AVERAGE_SPEED_KMH; // Time in hours
        return new double[]{totalDistance, totalTime};
    }

    // Find the optimal path ensuring restaurant is visited before customer
    public DeliveryRoute findOptimalPath(Location deliveryBoyLocation, DeliveryOrder o1, DeliveryOrder o2) {
        // Possible paths (restaurant must be visited before customer for each order)
        Location Order1Customer =  new Location("Order1Customer",o1.getCustomerLat(), o1.getCustomerLong());
        Location Order1Res =  new Location("Order1Res",o1.getRestaurantLat(), o1.getRestaurantLong());
        Location Order2Customer =  new Location("Order2Customer",o2.getCustomerLat(), o2.getCustomerLong());
        Location Order2Res =  new Location("Order2Res",o2.getRestaurantLat(), o2.getRestaurantLong());

        Location[] p1 = {deliveryBoyLocation, Order1Res, Order1Customer, Order2Res, Order2Customer};
        RouteMap r1 = new RouteMap(p1,"D -> R1 -> C1 -> R2 -> C2"); // Path 1: D -> R1 -> C1 -> R2 -> C2

        Location[] p2 = {deliveryBoyLocation, Order1Res, Order2Res, Order1Customer, Order2Customer};
        RouteMap r2 = new RouteMap(p2,"D -> R1 -> R2 -> C1 -> C2"); // Path 2: D -> R1 -> R2 -> C1 -> C2

        Location[] p3 ={deliveryBoyLocation, Order2Res, Order1Res, Order1Customer,Order2Customer};
        RouteMap r3 = new RouteMap(p3," D -> R2 -> R1 -> C1 -> C2"); // Path 3: D -> R2 -> R1 -> C1 -> C2

        Location[] p4 = {deliveryBoyLocation, Order2Res, Order2Customer, Order1Res, Order1Customer};
        RouteMap r4 = new RouteMap(p4,"D -> R2 -> C2 -> R1 -> C1"); // Path 4: D -> R2 -> C2 -> R1 -> C1

        RouteMap[] possiblePaths = {
                r1,
                r2,
                r3,
                r4
        };

        // Find the path with the minimum total distance
        RouteStep optimalPath = new RouteStep();
        DeliveryRoute deliveryRoute = new DeliveryRoute();

        double minDistance = Double.MAX_VALUE;
        double minTime = Double.MAX_VALUE;

        for (RouteMap path : possiblePaths) {
            double[] distanceAndTime = calculateTotalDistanceAndTime(path.getPath());
            double distance = distanceAndTime[0];
            double time = distanceAndTime[1];

            if (distance < minDistance) {
                minDistance = distance;
                minTime = time;
                optimalPath.setTotalDistance(distance);
                optimalPath.setTotalTime(time);
                optimalPath.setPath(Arrays.stream(path.getPath()).map(loc -> "[" + loc.getLatitude() + ", " + loc.getLongitude() + "]").toArray(String[]::new));
                deliveryRoute.setRouteOrder(path.getRoute());
                deliveryRoute.setRouteStep(optimalPath);
            }
        }

        return deliveryRoute;
    }




}
