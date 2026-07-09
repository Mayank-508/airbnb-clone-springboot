package com.mayank.airBnbApp.dto;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UpdateInventoryRequestDto {

    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal surgefactor;
    private Boolean closed;
}
