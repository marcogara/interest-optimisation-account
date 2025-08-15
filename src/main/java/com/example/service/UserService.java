package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    InterestSnapshotService interestSnapshotService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, InterestSnapshotService interestSnapshotService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.interestSnapshotService = interestSnapshotService;
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
    public void registerUser(User user) {
        // Encode password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");

        userRepository.save(user);

        // when a user register the snapshot allow to assign the new user with the correct interest rate. To refactor at later stage.
        try {
            interestSnapshotService.snapshotCurrentInterest();
        } catch (Exception e) {
            System.out.println(e);
        }
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