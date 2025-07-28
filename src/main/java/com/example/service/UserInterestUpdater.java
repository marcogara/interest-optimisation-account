package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserInterestUpdater {

    private final UserRepository userRepository;

    public UserInterestUpdater(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void updateAllUsersInterest(double newRate) {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            user.setInterest(newRate);
        }

        userRepository.saveAll(users);
        System.out.printf("✅ Updated all users with new interest: %.4f%n", newRate);
    }
}
