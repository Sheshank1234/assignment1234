package com.example.CrudApplication.service;

import com.example.CrudApplication.model.*;
import com.example.CrudApplication.repo.DeliveryOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;


@Service
public class OrdersBatchService {
        // List of all active SseEmitters (client connections)
        private final List<SseEmitter> emitters = new CopyOnWriteArrayList<>();
        private final DeliveryOrderRepository deliveryOrderRepository;

        public OrdersBatchService (DeliveryOrderRepository deliveryOrderRepository) {
            this.deliveryOrderRepository = deliveryOrderRepository;
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
