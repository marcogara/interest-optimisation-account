package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AllocationService allocationService;

    @InjectMocks
    private AccountService accountService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");
        testUser.setAccount(0.0);
        testUser.setPendingInterestMonthlyPayment(0);
    }

    @Test
    void addFundsToAccount_shouldIncreaseAccountBalanceAndTriggerAllocation() {
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        double initialBalance = testUser.getAccount();
        double amountToAdd = 100.0;

        accountService.addFundsToAccount("testuser", amountToAdd);

        assertEquals(initialBalance + amountToAdd, testUser.getAccount());
        verify(userRepository, times(1)).findByName("testuser");
        verify(userRepository, times(1)).save(testUser);
        verify(allocationService, times(1)).allocateDeposit(testUser, amountToAdd);
    }

    @Test
    void addFundsToAccount_shouldThrowUsernameNotFoundExceptionWhenUserNotFound() {
        when(userRepository.findByName(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> accountService.addFundsToAccount("nonexistentuser", 100.0));
        verify(userRepository, times(1)).findByName("nonexistentuser");
        verify(userRepository, never()).save(any(User.class));
        verify(allocationService, never()).allocateDeposit(any(User.class), anyDouble());
    }

    @Test
    void withdrawFundsToAccount_shouldDecreaseAccountBalanceAndTriggerAllocation() {
        testUser.setAccount(200.0); // Set initial balance for withdrawal
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        double initialBalance = testUser.getAccount();
        double amountToWithdraw = 50.0;

        accountService.withdrawFundsToAccount("testuser", amountToWithdraw);

        assertEquals(initialBalance - amountToWithdraw, testUser.getAccount());
        verify(userRepository, times(1)).findByName("testuser");
        verify(userRepository, times(1)).save(testUser);
        verify(allocationService, times(1)).allocateWithdrawal(testUser, amountToWithdraw);
    }

    @Test
    void withdrawFundsToAccount_shouldThrowInsufficientFundsExceptionWhenBalanceIsLow() {
        testUser.setAccount(30.0); // Set initial balance lower than withdrawal amount
        when(userRepository.findByName(anyString())).thenReturn(Optional.of(testUser));

        assertThrows(IllegalArgumentException.class, () -> accountService.withdrawFundsToAccount("testuser", 50.0));
        verify(userRepository, times(1)).findByName("testuser");
        verify(userRepository, never()).save(any(User.class));
        verify(allocationService, never()).allocateWithdrawal(any(User.class), anyDouble());
    }
}