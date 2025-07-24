package com.example.service;

import com.example.model.Bank;
import com.example.model.BankAllocation;
import com.example.model.InterestSnapshot;
import com.example.repository.BankRepository;
import com.example.repository.BankAllocationRepository;
import com.example.repository.InterestSnapshotRepository;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.Optional;

@Service
public class TestAllocationService {

    private final BankRepository bankRepository;
    private final BankAllocationRepository bankAllocationRepository;
    private final InterestSnapshotRepository interestSnapshotRepository;

    public TestAllocationService(
            BankRepository bankRepository,
            BankAllocationRepository bankAllocationRepository,
            InterestSnapshotRepository interestSnapshotRepository
    ) {
        this.bankRepository = bankRepository;
        this.bankAllocationRepository = bankAllocationRepository;
        this.interestSnapshotRepository = interestSnapshotRepository;
    }

    @PostConstruct
    public void runTestAllocation() {
        Optional<Bank> maybeBank = bankRepository.findByName("Urbo");
        if (maybeBank.isEmpty()) {
            System.out.println("Bank 'Urbo' not found, skipping test allocation.");
            return;
        }

        Bank bank = maybeBank.get();

        // Create sample allocation
        BankAllocation allocation = new BankAllocation(bank, 10000, "test-deposit");
        // bankAllocationRepository.save(allocation);
        System.out.println("✅ Test allocation saved for Urbo");

        // Create sample snapshot
        InterestSnapshot snapshot = new InterestSnapshot(0.0266);
        // interestSnapshotRepository.save(snapshot);
        System.out.println("✅ Test interest snapshot saved: 2.66%");
    }
}
