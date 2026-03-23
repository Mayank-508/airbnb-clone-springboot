package com.mayank.airBnbApp.dto;

import com.mayank.airBnbApp.entity.User;
import com.mayank.airBnbApp.entity.enums.Gender;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class GuestDto {


    private Long id;
    private User user;
    private String name;
    private Gender gender;
    private Integer age;


}
