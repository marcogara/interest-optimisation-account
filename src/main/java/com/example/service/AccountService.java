package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

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
}
