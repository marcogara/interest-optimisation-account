package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.example.model.InterestSnapshot;
import com.example.repository.InterestSnapshotRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final InterestSnapshotRepository interestSnapshotRepository;


    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, InterestSnapshotRepository interestSnapshotRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.interestSnapshotRepository = interestSnapshotRepository;
    }

    /**
     * Checks if a user with the given email already exists.
     * @param email The email to check.
     * @return An Optional containing the User if found, otherwise empty.
     */
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Registers a new user. This method handles password encoding and saving the user.
     * @param user The user object to register, containing the plaintext password.
     */
    @Transactional
    public void registerUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");

        // Assign the latest interest rate to the new user
        Optional<InterestSnapshot> latestSnapshot = Optional.ofNullable(interestSnapshotRepository.findTopByOrderByValidFromDesc());
        latestSnapshot.ifPresent(snapshot -> user.setInterest(snapshot.getEffectiveInterest()));

        userRepository.save(user);
    }

    /**
     * Finds a user by their name.
     * @param name The name of the user to find.
     * @return An Optional containing the User if found, otherwise empty.
     */
    public Optional<User> findByName(String name) {
        return userRepository.findByName(name);
    }

    /**
     * Finds all users.
     * @return A list of all users.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }
}