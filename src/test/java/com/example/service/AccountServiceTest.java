package com.example.service;

import com.example.model.User;
import com.example.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

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

    @Test
    void updateAllPendingInterestOnStartup_shouldSaveOnlyModifiedUsers() {
        // Given
        User userToUpdate = new User();
        userToUpdate.setName("userToUpdate");
        userToUpdate.setLastPendingInterestUpdate(LocalDate.now().minusDays(2));

        User adminUser = new User();
        adminUser.setName("admin");
        adminUser.setRole("ADMIN");
        adminUser.setLastPendingInterestUpdate(LocalDate.now().minusDays(5));

        User upToDateUser = new User();
        upToDateUser.setName("upToDateUser");
        upToDateUser.setLastPendingInterestUpdate(LocalDate.now());

        List<User> users = Arrays.asList(userToUpdate, adminUser, upToDateUser);
        when(userRepository.findAll()).thenReturn(users);

        // When
        accountService.updateAllPendingInterestOnStartup();

        // Then
        // Verify that findAll was called to get all the users
        verify(userRepository, times(1)).findAll();

        // Capture the user passed to the save method
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        // Verify that save was called only ONCE, for the user who needed an update
        verify(userRepository, times(1)).save(userCaptor.capture());

        // Assert that the user who was saved is the correct one
        assertEquals("userToUpdate", userCaptor.getValue().getName());
    }

    @Test
    void updateUserInterest_shouldCalculateCorrectlyForMissedDays() {
        // Given
        User user = new User();
        user.setName("testuser");
        user.setAccount(1000.0);
        user.setInterest(0.05); // 5% interest
        user.setLastPendingInterestUpdate(LocalDate.now().minusDays(2));
        user.setPendingInterestMonthlyPayment(10.0);

        // When
        accountService.updateUserInterest(user);

        // Then
        double dailyRate = 0.05 / 365.0;
        double expectedInterest = 10.0 + (1000.0 * dailyRate * 2);
        assertEquals(expectedInterest, user.getPendingInterestMonthlyPayment(), 0.0001);
        assertEquals(LocalDate.now(), user.getLastPendingInterestUpdate());
    }

    @Test
    void updateUserInterest_shouldNotUpdateAdminUser() {
        // Given
        User admin = new User();
        admin.setRole("ADMIN");
        admin.setLastPendingInterestUpdate(LocalDate.now().minusDays(5));
        admin.setPendingInterestMonthlyPayment(100.0);

        // When
        accountService.updateUserInterest(admin);

        // Then
        assertEquals(100.0, admin.getPendingInterestMonthlyPayment());
        assertEquals(LocalDate.now().minusDays(5), admin.getLastPendingInterestUpdate());
    }

    @Test
    void updateUserInterest_shouldNotUpdateUserWithNoMissedDays() {
        // Given
        User user = new User();
        user.setLastPendingInterestUpdate(LocalDate.now());
        user.setPendingInterestMonthlyPayment(50.0);

        // When
        accountService.updateUserInterest(user);

        // Then
        assertEquals(50.0, user.getPendingInterestMonthlyPayment());
        assertEquals(LocalDate.now(), user.getLastPendingInterestUpdate());
    }

    @Test
    void updateUserInterest_shouldHandleNullLastUpdateDate() {
        // Given
        User user = new User();
        user.setAccount(1000.0);
        user.setInterest(0.0365); // 3.65% for easy calculation (0.01 per day)
        user.setLastPendingInterestUpdate(null);
        user.setPendingInterestMonthlyPayment(0.0);

        // When
        accountService.updateUserInterest(user);

        // Then
        // Expects 1 day of interest as per the logic for null lastUpdateDate
        assertEquals(0.1, user.getPendingInterestMonthlyPayment(), 0.0001);
        assertEquals(LocalDate.now(), user.getLastPendingInterestUpdate());
    }
}