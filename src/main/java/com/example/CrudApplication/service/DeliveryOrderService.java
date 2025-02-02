package com.example.CrudApplication.service;

import com.example.CrudApplication.model.*;
import com.example.CrudApplication.repo.DeliveryOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class DeliveryOrderService {
    private static final double AVERAGE_SPEED_KMH = 20.0;
    private final DeliveryOrderRepository deliveryOrderRepository;

    // List of all active SseEmitters (client connections)
    private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    public DeliveryOrderService(DeliveryOrderRepository deliveryOrderRepository) {
        this.deliveryOrderRepository = deliveryOrderRepository;
    }

    public DeliveryRoute getActiveBatchForDB() {
        List<DeliveryOrder> orders = deliveryOrderRepository.findAll();
        Location deliveryBoyLocation = new Location("DB1", 12.9716, 77.5946);
        return findOptimalPath(deliveryBoyLocation,orders.get(0),orders.get(1));
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


    public List<DeliveryOrder> getAllOrders() {
        return deliveryOrderRepository.findAll();
    }

    public DeliveryOrder initiateDeliverySystem() {

            DeliveryOrder deliveryOrderDto1 = DeliveryOrder.builder()
                    .orderId("ORDER12345")
                    .orderStatus(OrderStatus.Pending)
                    .restaurantName("R1")
                    .restaurantLat(12.9756)
                    .restaurantLong(77.5996)
                    .customerName("C1")
                    .customerLat(12.9786)
                    .customerLong(77.6056)
                    .build();
            DeliveryOrder deliveryOrderObj1 = deliveryOrderRepository.save(deliveryOrderDto1);
            DeliveryOrder deliveryOrderDto2 = DeliveryOrder.builder()
                    .orderId("ORDER1234")
                    .orderStatus(OrderStatus.Pending)
                    .restaurantName("R2")
                    .restaurantLat(12.9696)
                    .restaurantLong(77.5896 )
                    .customerName("C2")
                    .customerLat(12.9656)
                    .customerLong(77.5826)
                    .build();
            DeliveryOrder deliveryOrderObj2 = deliveryOrderRepository.save(deliveryOrderDto2);
            return deliveryOrderObj1;

    }


    // Adds a new emitter for the client
    public SseEmitter addEmitter() {
        SseEmitter emitter = new SseEmitter();
        emitters.add(emitter);
        return emitter;
    }

    // Send a message to all connected clients
    public void broadcastMessage(String message) {
        for (SseEmitter emitter : emitters) {
            try {
                emitter.send("data: " + message + "\n\n");
            } catch (Exception e) {
                emitter.completeWithError(e);
                emitters.remove(emitter);  // Remove failed emitter
            }
        }
    }
}
