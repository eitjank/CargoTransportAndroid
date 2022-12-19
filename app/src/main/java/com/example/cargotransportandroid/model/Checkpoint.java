package com.example.cargotransportandroid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Checkpoint {
    private Long id;
    private String location;
    private LocalDateTime timeArrived;
    private LocalDateTime timeDeparted;
    private Route route;

    public Checkpoint(String location, LocalDateTime timeArrived, LocalDateTime timeDeparted, Route route) {
        this.location = location;
        this.timeArrived = timeArrived;
        this.timeDeparted = timeDeparted;
        this.route = route;
    }
}
