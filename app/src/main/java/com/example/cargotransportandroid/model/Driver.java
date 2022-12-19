package com.example.cargotransportandroid.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Driver extends User {

    private LocalDate drivingLicenseValidUntilDate;

    private List<Route> routes;

    public Driver(String login, String password, String name, String surname, LocalDate birthDate, String phoneNumber,
                  LocalDate drivingLicenseValidUntilDate) {
        super(login, password, name, surname, birthDate, phoneNumber);
        this.drivingLicenseValidUntilDate = drivingLicenseValidUntilDate;
    }

    @Override
    public String toString() {
        return "Driver(" +
                "id=" + getId() +
                ", login='" + getLogin() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ')';
    }
}
