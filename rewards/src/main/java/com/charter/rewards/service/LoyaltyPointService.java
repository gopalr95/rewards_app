package com.charter.rewards.service;

import com.charter.rewards.domain.CustomerRewardResponse;
import com.charter.rewards.domain.MonthlyReward;
import com.charter.rewards.entity.Customer;
import com.charter.rewards.entity.Transaction;
import com.charter.rewards.exception.InvalidDateRangeException;
import com.charter.rewards.exception.NoSuchCustomerExistsException;
import com.charter.rewards.repository.CustomerRepository;
import com.charter.rewards.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LoyaltyPointService {

    private static final Logger logger = LoggerFactory.getLogger(LoyaltyPointService.class);
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    TransactionRepository transactionRepository;

    public CustomerRewardResponse calculateRewardPoints(Long customerId, LocalDate startDate, LocalDate endDate){
        validateTheRequest(customerId, startDate, endDate);
        CustomerRewardResponse customerRewardResponse = null;
        List<MonthlyReward> monthlyRewards = null;
        List<Transaction> transactionList = null;
        try {
            transactionList = transactionRepository.findTransactionsBetweenGivenDatesByCustomerId(customerId, startDate, endDate);
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving the transactions for customerId: {}, caused by: {}", customerId, e.getMessage());
        }
        LocalDate firstMonthEndDate = startDate.plusMonths(1);
        LocalDate secondMonthEndDate = firstMonthEndDate.plusMonths(1);

        monthlyRewards = new ArrayList<>();
        double firstMonthRewards = calculateTheRewardsForMonth
                (transactionList.stream()
                        .filter(t -> t.getTransactionDate().isAfter(startDate.minusDays(1)) && t.getTransactionDate().isBefore(firstMonthEndDate))
                        .collect(Collectors.toList()));
        monthlyRewards.add(generateMonthlyReward(new StringBuilder().append(startDate).append(" to ").append(firstMonthEndDate.minusDays(1)).toString(), firstMonthRewards));

        double secondMonthRewards = calculateTheRewardsForMonth
                (transactionList.stream()
                        .filter(t -> t.getTransactionDate().isAfter(firstMonthEndDate.minusDays(1)) && t.getTransactionDate().isBefore(secondMonthEndDate))
                        .collect(Collectors.toList()));
        monthlyRewards.add(generateMonthlyReward(new StringBuilder().append(firstMonthEndDate).append(" to ").append(secondMonthEndDate.minusDays(1)).toString(), secondMonthRewards));

        double thirdMonthRewards = calculateTheRewardsForMonth
                (transactionList.stream().
                        filter(t -> t.getTransactionDate().isAfter(secondMonthEndDate.minusDays(1)) && t.getTransactionDate().isBefore(endDate))
                        .collect(Collectors.toList()));
        monthlyRewards.add(generateMonthlyReward(new StringBuilder().append(secondMonthEndDate).append(" to ").append(endDate).toString(), thirdMonthRewards));

        customerRewardResponse = new CustomerRewardResponse();
        customerRewardResponse.setMonthlyRewards(monthlyRewards);
        customerRewardResponse.setTotalRewards(
                monthlyRewards.stream()
                        .mapToDouble(MonthlyReward::getMonthlyRewards)
                        .sum());
        return customerRewardResponse;
    }

    private void validateTheRequest(Long customerId, LocalDate startDate, LocalDate endDate) {
        Optional<Customer> customer = null;
        try {
            customer = customerRepository.findById(customerId);
        } catch (Exception e) {
            logger.error("Exception occurred while retrieving the customer info for customerId: {}", customerId);
        }

        assert customer != null;
        if (customer.isPresent()) {
            logger.error("Customer is not found with id: {}", customerId);
            throw new NoSuchCustomerExistsException("Customer not found!");
        }
        Period period = Period.between(startDate, endDate.plusDays(1));

        if (period.getMonths() != 3 || period.getYears() > 0 || period.getDays() > 0) {
            logger.error("Invalid date range provided for the customer with Id: {}", customerId);
            throw new InvalidDateRangeException("Date range must be 3 months");
        }

    }

    public double calculateTheRewardsForMonth(List<Transaction> transactionList) {
        double amount;
        int rewardPoints = 0;
        for(Transaction transaction: transactionList) {
            amount = transaction.getAmount();
            if (amount > 100) {
                rewardPoints += (int) ((amount - 100) * 2) + (50);
            }
        }
        return rewardPoints;
    }

    private MonthlyReward generateMonthlyReward(String period, double rewards) {
        MonthlyReward monthlyReward = new MonthlyReward();
        monthlyReward.setMonthPeriod(period);
        monthlyReward.setMonthlyRewards(rewards);

        return monthlyReward;
    }

}

