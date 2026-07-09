package com.mayank.airBnbApp.service;


import com.mayank.airBnbApp.dto.*;
import com.mayank.airBnbApp.entity.Inventory;
import com.mayank.airBnbApp.entity.Room;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface InventoryService {
    void initializeRoomForAYear(Room room);

    void deleteInventoryById(Long id);


    void deleteAllInventories( Room room);


    Page<HotelPriceDto> searchHotels(HotelSearchRequestDto hotelSearchRequestDto);

    List<InventoryDto> getAllInventoryByRoom(Long roomId);

    void updateInventory(Long roomId, UpdateInventoryRequestDto updateInventoryRequestDto);
}
