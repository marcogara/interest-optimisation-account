package com.example.service;

import com.example.model.BankAllocation;
import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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

    public List<BankAllocation> bankAllocationList1(String user) {
        return allocationService.bankAllocationList(user);
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
}
