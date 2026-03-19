package com.mayank.airBnbApp.advice;


import jdk.jfr.SettingDefinition;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Getter
@Setter
public class ApiError {

    private String message;
    private List<String> subErrors;
    private HttpStatus status;

}
