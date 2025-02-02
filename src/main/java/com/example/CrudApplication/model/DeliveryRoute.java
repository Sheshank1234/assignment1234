package com.example.CrudApplication.model;

import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class DeliveryRoute {
    private RouteStep routeStep;
    private String routeOrder;
}
