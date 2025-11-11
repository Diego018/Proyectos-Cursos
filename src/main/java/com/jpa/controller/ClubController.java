package com.jpa.controller;

import com.jpa.dto.ClubDTO;
import com.jpa.service.ClubService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clubs")
public class ClubController {

    private final ClubService clubService;

    public ClubController(ClubService clubService) {
        this.clubService = clubService;
    }

    // Obtener todos los clubes
    @GetMapping
    public List<ClubDTO> getAllClubs() {
        return clubService.getAllClubs();
    }

    // Obtener club por ID
    @GetMapping("/{id}")
    public ClubDTO getClubById(@PathVariable Long id) {
        return clubService.getClubById(id);
    }

    // Crear club
    @PostMapping
    public ClubDTO createClub(@RequestParam String nameClub,
                              @RequestParam(required = false) Long coachId,
                              @RequestParam(required = false) Long associationId) {
        return clubService.createClub(nameClub, coachId, associationId);
    }

    // Actualizar club
    @PutMapping("/{id}")
    public ClubDTO updateClub(@PathVariable Long id,
                              @RequestParam String nameClub,
                              @RequestParam(required = false) Long coachId,
                              @RequestParam(required = false) Long associationId) {
        return clubService.updateClub(id, nameClub, coachId, associationId);
    }

    // Eliminar club
    @DeleteMapping("/{id}")
    public void deleteClub(@PathVariable Long id) {
        clubService.deleteClub(id);
    }

    // Buscar clubes
    @GetMapping("/search")
    public List<ClubDTO> searchClubs(@RequestParam String search) {
        return clubService.searchClubs(search);
    }
}
