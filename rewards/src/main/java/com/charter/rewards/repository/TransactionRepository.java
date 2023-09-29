package com.charter.rewards.repository;

import com.charter.rewards.entity.Transaction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@Transactional
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.customerId = :customerId AND t.transactionDate BETWEEN :startDate AND :endDate")
    List<Transaction> findTransactionsBetweenGivenDatesByCustomerId
            (Long customerId,  LocalDate startDate,  LocalDate endDate);
}