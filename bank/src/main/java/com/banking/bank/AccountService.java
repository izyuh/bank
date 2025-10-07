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
        Object usernameObj = session.getAttribute("username");
        if (usernameObj == null) {
            return Optional.empty();
        }
        String username = usernameObj.toString();
        return accountRepository.findByUsername(username);
    }
}