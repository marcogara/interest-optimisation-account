package com.example.authcore.controller;

import com.example.authcore.service.AccountService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

public class AccountController {

    private AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/account/add")
    public String addFunds(@RequestParam double amount, Principal principal) {
        String email = principal.getName(); // or however you identify users
        accountService.addFundsToAccount(email, amount);
        return "redirect:/dashboard"; // or wherever the user should go next
    }
}
