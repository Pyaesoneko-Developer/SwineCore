package com.example.swinecore.controller;

import com.example.swinecore.entity.Building;
import com.example.swinecore.entity.Room;
import com.example.swinecore.service.BuildingService;
import com.example.swinecore.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/buildings/{buildingId}/rooms")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final BuildingService buildingService;

    @GetMapping
    public String listRooms(@PathVariable Long buildingId, Model model) {
        Building building = buildingService.findById(buildingId).orElseThrow();
        model.addAttribute("building", building);
        model.addAttribute("rooms", roomService.findByBuilding(building));
        return "admin/rooms";
    }

    @PostMapping("/create")
    public String createRoom(@PathVariable Long buildingId,
                              @ModelAttribute Room room,
                              RedirectAttributes ra) {
        try {
            Building building = buildingService.findById(buildingId).orElseThrow();
            room.setBuilding(building);
            roomService.create(room);
            ra.addFlashAttribute("success", "Room created successfully.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/buildings/" + buildingId + "/rooms";
    }

    @PostMapping("/{id}/delete")
    public String deleteRoom(@PathVariable Long buildingId,
                              @PathVariable Long id,
                              @RequestParam String confirmName,
                              RedirectAttributes ra) {
        try {
            roomService.delete(id, confirmName);
            ra.addFlashAttribute("success", "Room deleted.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/buildings/" + buildingId + "/rooms";
    }
}
