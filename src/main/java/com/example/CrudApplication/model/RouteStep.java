package com.example.CrudApplication.model;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class RouteStep {
    private double totalDistance;
    private double totalTime;
    private String[] path; // Array of locations in order
}

