package com.example.service;

import com.example.model.BankAllocation;
import com.example.model.User;
import com.example.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class AccountService {

    private final UserRepository userRepository;
    private final AllocationService allocationService;

    public AccountService(UserRepository userRepository,
                          AllocationService allocationService) {
        this.userRepository = userRepository;
        this.allocationService = allocationService;
    }

    public void addFundsToAccount(String name, double amount) throws UsernameNotFoundException {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setAccount(user.getAccount() + amount);
        userRepository.save(user);

        // 👇 Trigger allocation
        allocationService.allocateDeposit(user, amount);
    }

    public void withdrawFundsToAccount(String name, double amount) throws UsernameNotFoundException {
        User user = userRepository.findByName(name)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        double currentBalance = user.getAccount();
        if (currentBalance < amount) {
            throw new IllegalArgumentException("❌ Insufficient funds. Available: " + currentBalance);
        }

        user.setAccount(user.getAccount() - amount);
        userRepository.save(user);

        // 👇 Trigger allocation
        allocationService.allocateWithdrawal(user, amount);
    }

    public List<BankAllocation> bankAllocationList1(String user) {
        return allocationService.bankAllocationList(user);
    }

    @PostConstruct
    public void updateAllPendingInterestOnStartup() {
        List<User> users = userRepository.findAll();

        LocalDate today = LocalDate.now();

        for (User user : users) {
            if ("admin".equals(user.getName())) continue; // Skip admin

            LocalDate lastUpdate = user.getLastPendingInterestUpdate();
            if (lastUpdate == null) lastUpdate = today.minusDays(1);

            long daysMissed = ChronoUnit.DAYS.between(lastUpdate, today);
            if (daysMissed <= 0) continue;

            double dailyRate = user.getInterest() / 365.0;
            double interestGained = user.getAccount() * dailyRate * daysMissed;

            double newPending = user.getPendingInterestMonthlyPayment() + interestGained;

            user.setPendingInterestMonthlyPayment(newPending);
            user.setLastPendingInterestUpdate(today);

            userRepository.save(user);
        }

        System.out.println("✅ Daily pending interest updated for all users.");
    }
}
