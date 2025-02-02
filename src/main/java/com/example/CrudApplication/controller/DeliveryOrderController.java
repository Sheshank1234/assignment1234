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

@RestController
@RequestMapping("/delivery-orders-api")
public class DeliveryOrderController {

        @Autowired
          private DeliveryOrderService deliveryOrderService;

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


}


