package com.example.service;

import com.example.model.BankAllocation;
import com.example.model.InterestSnapshot;
import com.example.repository.BankAllocationRepository;
import com.example.repository.InterestSnapshotRepository;
import com.example.util.InterestEvaluator;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InterestSnapshotService {

    private final InterestSnapshotRepository snapshotRepository;
    private final BankAllocationRepository allocationRepository;
    private final InterestEvaluator evaluator;
    private final UserInterestUpdater userInterestUpdater;

    public InterestSnapshotService(
            InterestSnapshotRepository snapshotRepository,
            BankAllocationRepository allocationRepository,
            InterestEvaluator evaluator, UserInterestUpdater userInterestUpdater
    ) {
        this.snapshotRepository = snapshotRepository;
        this.allocationRepository = allocationRepository;
        this.evaluator = evaluator;
        this.userInterestUpdater = userInterestUpdater;
    }

    public void snapshotCurrentInterest() {
        List<BankAllocation> allocations = allocationRepository.findAll();

        double rawRate = evaluator.evaluateEffectiveInterest(allocations);
        double roundedRate = Math.round(rawRate * 10000.0) / 10000.0;

        InterestSnapshot snapshot = new InterestSnapshot(roundedRate);
        snapshotRepository.save(snapshot);

        userInterestUpdater.updateAllUsersInterest(roundedRate);

        System.out.printf("📸 Interest snapshot saved: %.4f%n", roundedRate);
    }
}

