package com.example.cargotransportandroid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cargo {
    private Long id;
    private String name;
    private double weight;
    private Route route;

    public Cargo(String name, double weight, Route route) {
        this.name = name;
        this.weight = weight;
        this.route = route;
    }
}
