package com.example.model;

import com.example.service.UserInterestUpdater;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class BankAllocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id") // 🔹 Add this mapping
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "bank_id")
    private Bank bank;

    private double amount;

    private String source; // e.g., "deposit", "rebalancing"

    private String status = "ACTIVE"; // default value

    private LocalDateTime timestamp = LocalDateTime.now();

    public BankAllocation() {}

    public BankAllocation(User user, Bank bank, double amount, String source) {
        this.user = user;
        this.bank = bank;
        this.amount = amount;
        this.source = source;
        this.status = "ACTIVE";
        this.timestamp = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Bank getBank() { return bank; }
    public void setBank(Bank bank) { this.bank = bank; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
