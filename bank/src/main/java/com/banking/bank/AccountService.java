package com.banking.bank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.banking.bank.repository.AccountRepository;
import java.util.Optional;
import jakarta.servlet.http.HttpSession;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    public Optional<Account> getAccountFromSession(HttpSession session) {
        System.out.println("AccountService - Session ID: " + session.getId());
        System.out.println("AccountService - Session attributes: " + session.getAttributeNames());
        
        Object usernameObj = session.getAttribute("username");
        System.out.println("AccountService - Username from session: " + usernameObj);
        
        if (usernameObj == null) {
            System.out.println("AccountService - No username found in session!");
            return Optional.empty();
        }
        String username = usernameObj.toString();
        return accountRepository.findByUsername(username);
    }
}