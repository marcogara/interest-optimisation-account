package com.example.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class InterestSnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double effectiveInterest; // e.g., 0.0255 for 2.55%

    private LocalDateTime validFrom = LocalDateTime.now();

    private LocalDateTime validTo; // NULL means still active

    public InterestSnapshot() {}

    public InterestSnapshot(double effectiveInterest) {
        this.effectiveInterest = effectiveInterest;
        this.validFrom = LocalDateTime.now();
    }

    public Long getId() { return id; }

    public double getEffectiveInterest() { return effectiveInterest; }
    public void setEffectiveInterest(double effectiveInterest) { this.effectiveInterest = effectiveInterest; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
}
