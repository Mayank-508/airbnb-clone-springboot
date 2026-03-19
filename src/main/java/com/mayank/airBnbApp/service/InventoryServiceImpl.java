package com.mayank.airBnbApp.service;


import com.mayank.airBnbApp.entity.Inventory;
import com.mayank.airBnbApp.entity.Room;
import com.mayank.airBnbApp.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl  implements InventoryService{

 private final InventoryRepository inventoryRepository;

    @Override
    public void initializeRoomForAYear(Room room) {

        LocalDate today= LocalDate.now();
        LocalDate endDate= today.plusYears(1);

        for(; !today.isAfter(endDate); today=today.plusDays(1))
        {
            Inventory inventory= Inventory.builder()
                    .room(room)
                    .date(today)
                    .hotel(room.getHotel())
                    .bookedCount(0)
                    .city(room.getHotel().getCity())
                    .price(room.getBasePrice())
                    .surgeFactor(BigDecimal.ONE)
                    .totalCount(room.getTotalCount())
                    .closed(false)
                    .build();

            inventoryRepository.save(inventory);


        }

    }

    @Override
    public void deleteInventoryById(Long id) {
        inventoryRepository.deleteById(id);
    }

    @Override
    public void deleteAllInventories( Room room) {
        LocalDate today= LocalDate.now();
        inventoryRepository.deleteByRoom( room);
    }




}
