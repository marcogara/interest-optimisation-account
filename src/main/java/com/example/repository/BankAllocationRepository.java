package com.example.repository;

import com.example.model.BankAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAllocationRepository extends JpaRepository<BankAllocation, Long> {
    // You can add custom queries here later, e.g., findByStatus("ACTIVE")
}
