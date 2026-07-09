package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.entity.Booking;

public interface CheckoutService {
    String getCheckoutSession(Booking booking, String successUrl, String failureUrl);


}
