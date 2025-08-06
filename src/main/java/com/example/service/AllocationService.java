package com.example.service;

import com.example.model.Bank;
import com.example.model.BankAllocation;
import com.example.model.User;
import com.example.repository.BankRepository;
import com.example.repository.BankAllocationRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class AllocationService {

    private final BankRepository bankRepository;
    private final BankAllocationRepository bankAllocationRepository;

    public AllocationService(
            BankRepository bankRepository,
            BankAllocationRepository bankAllocationRepository
    ) {
        this.bankRepository = bankRepository;
        this.bankAllocationRepository = bankAllocationRepository;
    }

    public void allocateDeposit(User user, double amount) {
        List<Bank> banks = bankRepository.findAll();

        if (banks.isEmpty()) {
            throw new IllegalStateException("❌ No banks available for allocation");
        }

        // Pick the bank with the highest interest
        Bank targetBank = banks.stream()
                .max(Comparator.comparingDouble(Bank::getInterest))
                .orElseThrow(() -> new IllegalStateException("No bank with max interest found"));

        // Save allocation
        BankAllocation allocation = new BankAllocation(user, targetBank, amount, "deposit");
        bankAllocationRepository.save(allocation);

        // Update bank account balance
        targetBank.setAccount(targetBank.getAccount() + amount);
        bankRepository.save(targetBank);

        System.out.printf("✅ Allocated %.2f to bank %s at %.2f%%%n",
                amount, targetBank.getName(), targetBank.getInterest() * 100);
    }

    public List<BankAllocation> bankAllocationList(String user) {
        return bankAllocationRepository.findAll().stream()
                .filter(allocation -> user.equals(allocation.getUser().getName()))
                .toList(); // Java 16+; or use .collect(Collectors.toList()) for older versions
    }

    public void allocateWithdrawal(User user, double amount) {
        List<Bank> banks = bankRepository.findAll();

        if (banks.isEmpty()) {
            throw new IllegalStateException("❌ No banks available for withdrawal");
        }

        // Pick the bank with the **lowest** balance or interest – your logic may vary.
        Bank sourceBank = banks.stream()
                .min(Comparator.comparingDouble(Bank::getInterest)) // or use account balance if needed
                .orElseThrow(() -> new IllegalStateException("No bank found for withdrawal"));

        // Save withdrawal allocation
        BankAllocation allocation = new BankAllocation(user, sourceBank, amount, "withdrawal");
        bankAllocationRepository.save(allocation);

        // Deduct from bank account balance
        sourceBank.setAccount(sourceBank.getAccount() - amount);
        bankRepository.save(sourceBank);

        System.out.printf("✅ Withdrawn %.2f from bank %s at %.2f%%%n",
                amount, sourceBank.getName(), sourceBank.getInterest() * 100);
    }

}
