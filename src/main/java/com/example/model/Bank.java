package com.example.model;

import jakarta.persistence.*;

@Entity
public class Bank {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    private double interest;

    private double account;

    public Bank() {}

    public Bank(String name, double interest, double account) {
        this.name = name;
        this.interest = interest;
        this.account = account;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getInterest() {
        return interest;
    }

    public double getAccount() {
        return account;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setInterest(double interest) {
        this.interest = interest;
    }

    public void setAccount(double account) {
        this.account = account;
    }
}
