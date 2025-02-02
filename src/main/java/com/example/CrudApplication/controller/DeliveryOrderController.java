package com.example.CrudApplication.controller;

import com.example.CrudApplication.model.DeliveryOrder;
import com.example.CrudApplication.model.DeliveryRoute;
import com.example.CrudApplication.service.DeliveryOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api")
public class DeliveryOrderController {

        @Autowired
          private DeliveryOrderService deliveryOrderService;

    // Endpoint to stream data to the client
    @GetMapping(value = "/sse/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamData() {
        return deliveryOrderService.addEmitter();  // Adds a new emitter for the client
    }

    // Endpoint to trigger a message broadcast from backend
    @PostMapping("/triggerMessage")
    public String triggerMessage() {
        deliveryOrderService.broadcastMessage("Backend triggered update");
        return "Message sent to all clients!";
    }

    @GetMapping("/hello")
    public String sayHello() {
        return "Hello, Spring Boot!";
    }

        @GetMapping("/getAllDeliveryOrders")
        public ResponseEntity<List<DeliveryOrder>> getDeliveryOrderById() {
            List<DeliveryOrder> deliveryOrdersList = deliveryOrderService.getAllOrders();
            if (deliveryOrdersList.isEmpty()) {
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(deliveryOrdersList,HttpStatus.OK);
        }

    @GetMapping("/getActiveBatchForDB")
    public ResponseEntity<DeliveryRoute> getActiveBatchForDB() {
        DeliveryRoute optimalPath = deliveryOrderService.getActiveBatchForDB();
        return new ResponseEntity<>(optimalPath,HttpStatus.OK);
    }

        @PostMapping("/initiateDeliverySystem")
        public ResponseEntity<DeliveryOrder> initiateDeliverySystem(@RequestBody Object emptyRequestBody) {
            try {
                DeliveryOrder deliveryOrderObj1 = deliveryOrderService.initiateDeliverySystem();
                return new ResponseEntity<>(deliveryOrderObj1, HttpStatus.CREATED);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
}


