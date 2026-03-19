package com.mayank.airBnbApp.service;


import com.mayank.airBnbApp.entity.Room;

import java.time.LocalDate;

public interface InventoryService {
    void initializeRoomForAYear(Room room);

    void deleteInventoryById(Long id);


    void deleteAllInventories( Room room);
}
