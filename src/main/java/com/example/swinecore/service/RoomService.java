package com.example.swinecore.service;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Room;
import com.example.swinecore.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RoomService {

    private final RoomRepository roomRepository;

    public Room create(Room room) {
        room.setCode(nextRoomCode());
        if (roomRepository.existsByBuildingAndCode(room.getBuilding(), room.getCode()))
            throw new IllegalArgumentException("Room code already exists in this building: " + room.getCode());
        return roomRepository.save(room);
    }

    private synchronized String nextRoomCode() {
        for (int number = 1; number <= 99999; number++) {
            String code = "R" + String.format("%05d", number);
            if (!roomRepository.existsByCode(code)) return code;
        }
        throw new IllegalStateException("Room code capacity has been reached.");
    }

    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Transactional(readOnly = true)
    public List<Room> findByBuilding(Building building) {
        return roomRepository.findActiveByBuildingWithBuilding(building);
    }

    @Transactional(readOnly = true)
    public Optional<Room> findById(Long id) {
        return roomRepository.findById(id);
    }

    public void delete(Long id, String confirmName) {
        Room room = roomRepository.findById(id).orElseThrow();
        if (!room.getName().equalsIgnoreCase(confirmName))
            throw new IllegalArgumentException("Name confirmation does not match.");
        roomRepository.deleteById(id);
    }
}
