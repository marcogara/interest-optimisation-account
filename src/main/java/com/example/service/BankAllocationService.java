package com.example.service;

import com.example.model.BankAllocation;
import com.example.repository.BankAllocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BankAllocationService {

    private final BankAllocationRepository bankAllocationRepository;

    public BankAllocationService(BankAllocationRepository bankAllocationRepository) {
        this.bankAllocationRepository = bankAllocationRepository;
    }

    public List<BankAllocation> findAll() {
        return bankAllocationRepository.findAll();
    }
}