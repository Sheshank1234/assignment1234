package com.example.CrudApplication.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    private String name;
    private double latitude;
    private double longitude;


    // ✅ Add this to fix your issue
    @Override
    public String toString() {
        return "Location{name='" + name + "', latitude=" + latitude + ", longitude=" + longitude + "}";
    }
}
