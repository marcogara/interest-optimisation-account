package com.example.service;

import com.example.model.BankAllocation;
import com.example.model.User;
import com.example.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
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

    public List<BankAllocation> getBankAllocationsForUser(String userName) {
        try {
            return allocationService.findAllocationsByUserName(userName);
        } catch (Exception e) {
            // Log, return empty list to avoid breaking controller
            return Collections.emptyList();
        }
    }

    @PostConstruct
    public void updateAllPendingInterestOnStartup() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            LocalDate originalUpdateDate = user.getLastPendingInterestUpdate();
            updateUserInterest(user);

            // If the last update date has changed, it means the user was updated and should be saved.
            if (user.getLastPendingInterestUpdate() != null && !user.getLastPendingInterestUpdate().equals(originalUpdateDate)) {
                userRepository.save(user);
            }
        }

        System.out.println("✅ Daily pending interest updated for all users.");
    }

    public void updateUserInterest(User user) {
        if ("ADMIN".equals(user.getRole())) {
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDate lastUpdate = user.getLastPendingInterestUpdate();
        if (lastUpdate == null) {
            lastUpdate = today.minusDays(1);
        }

        long daysMissed = ChronoUnit.DAYS.between(lastUpdate, today);
        if (daysMissed <= 0) {
            return;
        }

        double dailyRate = user.getInterest() / 365.0;
        double interestGained = user.getAccount() * dailyRate * daysMissed;

        double newPending = user.getPendingInterestMonthlyPayment() + interestGained;
        newPending = Math.round(newPending * 10000.0) / 10000.0; // round to 4 decimals

        user.setPendingInterestMonthlyPayment(newPending);
        user.setLastPendingInterestUpdate(today);
    }
}
