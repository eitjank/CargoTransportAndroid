package com.example.cargotransportandroid.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Truck {
    private int id;
    private String carBrand;
    private String model;
    private int manufactureYear;

    private List<Route> routeAssignedTo;

    public Truck(String carBrand, String model, int manufactureYear) {
        this.carBrand = carBrand;
        this.model = model;
        this.manufactureYear = manufactureYear;
    }

    @Override
    public String toString() {
        return "Truck(" +
                "id=" + id +
                ", carBrand='" + carBrand + '\'' +
                ", model='" + model + '\'' +
                ", manufactureYear=" + manufactureYear +
                ')';
    }
}
