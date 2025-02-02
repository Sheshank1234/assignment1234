package com.example.CrudApplication.model;

import lombok.*;
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Builder
public class RouteMap {
    private Location[] path; // Array of locations in order
    private String route;

}
