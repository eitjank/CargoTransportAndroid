package com.example.cargotransportandroid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Route {
    private Long id;
    private String startLocation;
    private String endLocation;
    private LocalDateTime deliveryDeadline;
    private List<Checkpoint> checkpoints;
    private List<Cargo> cargo;
    private Driver driver;
    private Manager manager;
    private Truck assignedTruck;

    public Route(String startLocation, String endLocation, LocalDateTime deliveryDeadline, Driver driver, Manager manager, Truck assignedTruck) {
        this.startLocation = startLocation;
        this.endLocation = endLocation;
        this.deliveryDeadline = deliveryDeadline;
        this.driver = driver;
        this.manager = manager;
        this.assignedTruck = assignedTruck;
    }
    @Override
    public String toString() {
        return "Route(" +
                "id=" + id +
                ", startLocation='" + startLocation + '\'' +
                ", endLocation='" + endLocation + '\'' +
                ", deliveryDeadline=" + deliveryDeadline +
                ", driver=" + driver +
                ", manager=" + manager +
                ", assignedTruck=" + assignedTruck +
                ')';
    }
}
