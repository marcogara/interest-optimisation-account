package com.example.authcore.service;

import com.example.authcore.model.User;
import com.example.authcore.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final UserRepository userRepository;
    public AccountService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void addFundsToAccount(String email, double amount) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setAccount(user.getAccount() + amount);
        userRepository.save(user); }
}
