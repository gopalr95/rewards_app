package com.charter.rewards.domain;

import lombok.Data;
import lombok.NonNull;

import java.time.LocalDate;

@Data
public class CustomerRewardRequest {

    @NonNull
    private Long customerId;
    @NonNull
    private LocalDate startDate;
    @NonNull
    private LocalDate endDate;
}
