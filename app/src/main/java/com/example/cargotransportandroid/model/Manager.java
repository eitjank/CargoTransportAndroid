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
public class Manager extends User {
    private boolean isAdmin = false;
    private String email;

    private List<Route> routes;

    public Manager(String login, String password, String name, String surname, LocalDate birthDate,
                   String phoneNumber, boolean isAdmin, String email) {
        super(login, password, name, surname, birthDate, phoneNumber);
        this.isAdmin = isAdmin;
        this.email = email;
    }

    @Override
    public String toString() {
        return "Manager(" +
                "id=" + getId() +
                ", login='" + getLogin() + '\'' +
                ", name='" + getName() + '\'' +
                ", surname='" + getSurname() + '\'' +
                ')';
    }
}
