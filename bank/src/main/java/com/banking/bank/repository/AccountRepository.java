package com.banking.bank.repository;

import com.banking.bank.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Find account by username
    Optional<Account> findByUsername(String username);

    // Check if username exists
    boolean existsByUsername(String username);

    // Custom query for login validation
    @Query("SELECT a FROM Account a WHERE a.username = ?1 AND a.hashedPassword = ?2")
    Optional<Account> findByUsernameAndPassword(String username, String hashedPassword);

    // Find by account number
    Optional<Account> findByAccountNumber(String accountNumber);
}
