package com.charter.rewards.domain;

import lombok.Data;

import java.util.List;

@Data
public class CustomerRewardResponse {
    private List<MonthlyReward> monthlyRewards;
    private double totalRewards;
    private String errorMessage;

}
