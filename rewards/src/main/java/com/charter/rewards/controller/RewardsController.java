package com.charter.rewards.controller;

import com.charter.rewards.domain.CustomerRewardResponse;
import com.charter.rewards.exception.InvalidDateRangeException;
import com.charter.rewards.exception.NoSuchCustomerExistsException;
import com.charter.rewards.service.LoyaltyPointService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@CrossOrigin(origins = "http://localhost:3000", allowedHeaders = "Access-Control-Allow-Origin")
@RestController
@RequestMapping("/rewards")
public class RewardsController {

    private static final Logger logger = LoggerFactory.getLogger(RewardsController.class);
    @Autowired
    private LoyaltyPointService loyaltyPointService;

    @RequestMapping(method = RequestMethod.GET, value = "/customer/{customerId}", consumes = "*/*", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CustomerRewardResponse> calculateRewardPoints(@PathVariable Long customerId, @RequestParam String startDate, @RequestParam String endDate) {
        CustomerRewardResponse customerRewardResponse = new CustomerRewardResponse();
        LocalDate sd = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate ed = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        try {
            customerRewardResponse = loyaltyPointService.calculateRewardPoints(customerId, sd, ed);
        } catch (NoSuchCustomerExistsException ne) {
            customerRewardResponse.setErrorMessage(ne.getMessage());
            return new ResponseEntity<>(customerRewardResponse, HttpStatus.NOT_FOUND);
        } catch (InvalidDateRangeException ie) {
            customerRewardResponse.setErrorMessage(ie.getMessage());
            return new ResponseEntity<>(customerRewardResponse, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return ResponseEntity.ok(customerRewardResponse);
    }

}

