package com.mayank.airBnbApp.service;

import com.mayank.airBnbApp.dto.HotelDto;
import com.mayank.airBnbApp.entity.Hotel;
import org.springframework.stereotype.Service;

@Service
public interface HotelService {

   HotelDto createNewHotel(HotelDto hotelDto);

   HotelDto getHotelById(Long id);

   HotelDto updateHotelById(Long id,HotelDto hotelDto);

   void deleteHotelById(Long id);

   void activateHotel(Long id);
}
