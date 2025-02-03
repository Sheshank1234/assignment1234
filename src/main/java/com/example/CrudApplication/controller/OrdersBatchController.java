package com.example.CrudApplication.controller;

import com.example.CrudApplication.model.DeliveryOrder;
import com.example.CrudApplication.service.OrdersBatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


@RestController
@RequestMapping("/orders-batch-api")
public class OrdersBatchController {
    @Autowired
    private OrdersBatchService ordersBatchService;


    /**
     * Initialises two delivery orders with each have the respective restaurant and customer location details
     * @return - DeliveryOrder
     */
    @PostMapping("/initiateDeliverySystem")
    public ResponseEntity<DeliveryOrder> initiateDeliverySystem(@RequestBody Object emptyRequestBody) {
        try {
            DeliveryOrder deliveryOrderObj1 = ordersBatchService.initiateDeliverySystem();
            return new ResponseEntity<>(deliveryOrderObj1, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Endpoint to stream data to the client
    @GetMapping(value = "/sse/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamData() {
        return ordersBatchService.addEmitter();  // Adds a new emitter for the client
    }

    // Endpoint to trigger a message broadcast from backend
    @PostMapping("/triggerMessage")
    public String triggerMessage() {
        ordersBatchService.broadcastMessage("Backend triggered update");
        return "Message sent to all clients!";
    }

//    @GetMapping("/getAllBatches")
//    public ResponseEntity<String> getAllBatches() {
//        return new ResponseEntity<>("Received all batches",HttpStatus.OK);
//    }
}
