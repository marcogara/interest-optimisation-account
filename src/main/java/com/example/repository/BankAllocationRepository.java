package com.example.repository;

import com.example.model.BankAllocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BankAllocationRepository extends JpaRepository<BankAllocation, Long> {
    List<BankAllocation> findByUser_Name(String userName);
}
