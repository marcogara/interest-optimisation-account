package com.example.simulation;

import com.example.model.Bank;
import com.example.repository.BankRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class InterestSimulationService {

    private final BankRepository bankRepository;
    private final Random random = new Random();

    public InterestSimulationService(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }

    public void simulateInterestChanges() {
        List<Bank> banks = bankRepository.findAll();
        if (banks.size() < 2) return; // Ensure at least 2 banks

        // Track current top interest
        Bank previousTopBank = banks.stream()
                .max((a, b) -> Double.compare(a.getInterest(), b.getInterest()))
                .orElse(null);

        Bank newTopBank = previousTopBank;

        while (newTopBank == previousTopBank && banks.size() > 1) {
            for (Bank bank : banks) {
                double change = (random.nextDouble() - 0.5) * 0.001; // ±0.05%
                double newInterest = bank.getInterest() + change;
                newInterest = Math.max(0.01, Math.min(0.03, newInterest)); // Clamp between 1% and 3%
                newInterest = Math.round(newInterest * 10000.0) / 10000.0; // Round to 4 decimals
                bank.setInterest(newInterest);
            }

            bankRepository.saveAll(banks);

            newTopBank = banks.stream()
                    .max((a, b) -> Double.compare(a.getInterest(), b.getInterest()))
                    .orElse(null);
        }

        System.out.println("✅ Interest simulation complete. New top bank: " + newTopBank.getName() + " (" + newTopBank.getInterest() + ")");
    }
}
