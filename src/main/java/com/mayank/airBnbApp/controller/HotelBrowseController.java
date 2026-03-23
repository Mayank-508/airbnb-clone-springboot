package com.mayank.airBnbApp.controller;


import com.mayank.airBnbApp.dto.HotelDto;
import com.mayank.airBnbApp.dto.HotelInfoDto;
import com.mayank.airBnbApp.dto.HotelSearchRequestDto;
import com.mayank.airBnbApp.entity.Hotel;
import com.mayank.airBnbApp.service.HotelService;
import com.mayank.airBnbApp.service.InventoryService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/hotels")
public class HotelBrowseController {

    private final HotelService hotelService;
    private final InventoryService inventoryService;

    @GetMapping("/search")
    public ResponseEntity<Page<HotelDto>> searchHotels(@RequestBody HotelSearchRequestDto hotelSearchRequestDto) {

        Page<HotelDto> page = inventoryService.searchHotels(hotelSearchRequestDto);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @GetMapping("/{hotelId}/info")
    public ResponseEntity<HotelInfoDto> getHotelInfo(@PathVariable Long hotelId)
    {

      HotelInfoDto hotelInfoDto=  hotelService.getHotelInfoById(hotelId);
      return new ResponseEntity<>(hotelInfoDto, HttpStatus.OK);

    }

}
