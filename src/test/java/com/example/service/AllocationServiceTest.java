package com.example.service;

import com.example.model.Bank;
import com.example.model.BankAllocation;
import com.example.model.User;
import com.example.repository.BankAllocationRepository;
import com.example.repository.BankRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AllocationServiceTest {

    @Mock
    private BankRepository bankRepository;

    @Mock
    private BankAllocationRepository bankAllocationRepository;

    @InjectMocks
    private AllocationService allocationService;

    private User testUser;
    private Bank bank1;
    private Bank bank2;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");
        testUser.setAccount(1000.0);

        bank1 = new Bank();
        bank1.setId(1L);
        bank1.setName("Bank A");
        bank1.setInterest(0.05); // 5%
        bank1.setAccount(5000.0);

        bank2 = new Bank();
        bank2.setId(2L);
        bank2.setName("Bank B");
        bank2.setInterest(0.07); // 7%
        bank2.setAccount(7000.0);
    }

    @Test
    void allocateDeposit_shouldAllocateToBankWithHighestInterest() {
        when(bankRepository.findAll()).thenReturn(Arrays.asList(bank1, bank2));
        when(bankAllocationRepository.save(any(BankAllocation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bankRepository.save(any(Bank.class))).thenAnswer(invocation -> invocation.getArgument(0));

        double depositAmount = 100.0;
        allocationService.allocateDeposit(testUser, depositAmount);

        // Verify allocation to bank2 (highest interest)
        assertEquals(7100.0, bank2.getAccount()); // bank2 had 7000, +100 deposit
        verify(bankAllocationRepository, times(1)).save(any(BankAllocation.class));
        verify(bankRepository, times(1)).save(bank2); // Only bank2 should be saved
    }

    @Test
    void allocateDeposit_shouldThrowIllegalStateExceptionWhenNoBanksAvailable() {
        when(bankRepository.findAll()).thenReturn(Collections.emptyList());

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            allocationService.allocateDeposit(testUser, 100.0);
        });
        assertEquals("❌ No banks available for allocation", thrown.getMessage());
        verify(bankAllocationRepository, never()).save(any(BankAllocation.class));
        verify(bankRepository, never()).save(any(Bank.class));
    }

    @Test
    void allocateWithdrawal_shouldWithdrawFromBankWithLowestInterest() {
        when(bankRepository.findAll()).thenReturn(Arrays.asList(bank1, bank2));
        when(bankAllocationRepository.save(any(BankAllocation.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bankRepository.save(any(Bank.class))).thenAnswer(invocation -> invocation.getArgument(0));

        double withdrawalAmount = 50.0;
        allocationService.allocateWithdrawal(testUser, withdrawalAmount);

        // Verify withdrawal from bank1 (lowest interest)
        assertEquals(4950.0, bank1.getAccount()); // bank1 had 5000, -50 withdrawal
        verify(bankAllocationRepository, times(1)).save(any(BankAllocation.class));
        verify(bankRepository, times(1)).save(bank1); // Only bank1 should be saved
    }

    @Test
    void allocateWithdrawal_shouldThrowIllegalStateExceptionWhenNoBanksAvailable() {
        when(bankRepository.findAll()).thenReturn(Collections.emptyList());

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            allocationService.allocateWithdrawal(testUser, 50.0);
        });
        assertEquals("❌ No banks available for withdrawal", thrown.getMessage());
        verify(bankAllocationRepository, never()).save(any(BankAllocation.class));
        verify(bankRepository, never()).save(any(Bank.class));
    }

    @Test
    void findAllocationsByUserName_shouldReturnAllocations() {
        BankAllocation allocation1 = new BankAllocation(testUser, bank1, 50.0, "deposit");
        BankAllocation allocation2 = new BankAllocation(testUser, bank2, 20.0, "withdrawal");
        List<BankAllocation> expectedAllocations = Arrays.asList(allocation1, allocation2);

        when(bankAllocationRepository.findByUser_Name(testUser.getName())).thenReturn(expectedAllocations);

        List<BankAllocation> actualAllocations = allocationService.findAllocationsByUserName(testUser.getName());

        assertNotNull(actualAllocations);
        assertEquals(2, actualAllocations.size());
        assertEquals(expectedAllocations, actualAllocations);
        verify(bankAllocationRepository, times(1)).findByUser_Name(testUser.getName());
    }
}