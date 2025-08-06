package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;


@Entity
@Table(name = "users") // Optional: sets the table name
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(unique = true)
    private String email;

    @NotBlank
    private String password;

    private double interest;

    private double account;

    private double pendingInterestMonthlyPayment;

    private LocalDate lastPendingInterestUpdate;

    public User(){}

    public User(Long id, String name, String email, String password) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getInterest() {
        return interest;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public double getAccount() {
        return account;
    }

    public void setAccount(double account) {
        this.account = account;
    }

    public double getPendingInterestMonthlyPayment() {
        return pendingInterestMonthlyPayment;
    }

    public void setPendingInterestMonthlyPayment(double pendingInterestMonthlyPayment) {
        this.pendingInterestMonthlyPayment = pendingInterestMonthlyPayment;
    }

    public LocalDate getLastPendingInterestUpdate() {
        return lastPendingInterestUpdate;
    }

    public void setLastPendingInterestUpdate(LocalDate lastPendingInterestUpdate) {
        this.lastPendingInterestUpdate = lastPendingInterestUpdate;
    }
}
