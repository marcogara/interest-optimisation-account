package com.example.controller;

import com.example.service.AccountService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
public class AccountController {

    private AccountService accountService;
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping("/account/add")
    public String addFunds(@RequestParam ("amount") double amount, Principal principal) {
        String email = principal.getName(); // or however you identify users
        accountService.addFundsToAccount(email, amount);
        System.out.println("Adding funds for: " + principal.getName() + " amount: " + amount);

        return "redirect:/dashboard"; // or wherever the user should go next
    }
}
