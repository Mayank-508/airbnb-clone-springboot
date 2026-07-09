package com.mayank.airBnbApp.controller;

import com.mayank.airBnbApp.dto.BookingDto;
import com.mayank.airBnbApp.dto.HotelDto;
import com.mayank.airBnbApp.dto.HotelReportDto;
import com.mayank.airBnbApp.service.BookingService;
import com.mayank.airBnbApp.service.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/hotels")
@Slf4j
public class HotelController {

    private final HotelService hotelService;
    private final BookingService bookingService;

    @PostMapping
   public ResponseEntity<HotelDto> createNewHotel(@RequestBody @Validated HotelDto hotelDto)
    {
        log.info("Attempting to create a new hotel with name {}",hotelDto.getName());
        HotelDto hotel = hotelService.createNewHotel(hotelDto);
        return new ResponseEntity<>(hotel, HttpStatus.CREATED);
    }

    @GetMapping("/{hotelId}")
    public ResponseEntity<HotelDto> getHotelById(@PathVariable Long hotelId)
    {
        log.info("Attempting to fetch a hotel with id: {}",hotelId);
        HotelDto hotel= hotelService.getHotelById(hotelId);
        return new ResponseEntity<>(hotel,HttpStatus.OK);
    }

    // in put mapping we change the entire thing, hence
    // using modelMapper is better
    @PutMapping("/{hotelId}")
    public ResponseEntity<HotelDto> updateHotelById(@PathVariable(name = "hotelId") Long id, @RequestBody HotelDto hotelDto)
    {
       HotelDto hotel= hotelService.updateHotelById(id,hotelDto);
       return ResponseEntity.ok(hotel);
    }

    @DeleteMapping("/{hotelId}")
    public ResponseEntity<Void> deleteHotelById(@PathVariable(name = "hotelId") Long id)
    {
         hotelService.deleteHotelById(id);
         return ResponseEntity.noContent().build();

    }

    // in patch mapping, it is to update one or sometimes few but not all fields hence,
    // for changing just manually doing it is feasible, while
    // for more than one field update we do use Reflections.
    @PatchMapping("/{hotelId}/activate")
    public ResponseEntity<Void> activateHotel(@PathVariable  Long hotelId)
    {
        hotelService.activateHotel(hotelId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping
    public ResponseEntity<List<HotelDto>> getAllHotels() {
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{hotelId}/bookings")
    public ResponseEntity<List<BookingDto>> getAllBookingsByHotelId(@PathVariable Long hotelId) {
        return ResponseEntity.ok(bookingService.getAllBookingsByHotelId(hotelId));
    }

    @GetMapping("/{hotelId}/reports")
    public ResponseEntity<HotelReportDto> getHotelReport(@PathVariable Long hotelId,
                                                         @RequestParam(required = false) LocalDate startDate,
                                                         @RequestParam(required = false) LocalDate endDate) {

        if (startDate == null) startDate = LocalDate.now().minusMonths(1);
        if (endDate == null) endDate = LocalDate.now();

        return ResponseEntity.ok(bookingService.getHotelReport(hotelId, startDate, endDate));
    }

}
