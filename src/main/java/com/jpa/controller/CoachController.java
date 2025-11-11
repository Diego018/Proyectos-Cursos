package com.jpa.controller;

import com.jpa.dto.CoachDTO;
import com.jpa.service.CoachService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coaches")
public class CoachController {

    private final CoachService coachService;

    public CoachController(CoachService coachService) {
        this.coachService = coachService;
    }

    // Obtener todos los entrenadores
    @GetMapping
    public List<CoachDTO> getAllCoaches() {
        return coachService.getAllCoaches();
    }

    // Obtener entrenador por ID
    @GetMapping("/{id}")
    public CoachDTO getCoachById(@PathVariable Long id) {
        return coachService.getCoachById(id);
    }

    // Crear entrenador
    @PostMapping
    public CoachDTO createCoach(@RequestParam String nameCoach,
                                @RequestParam String lastName,
                                @RequestParam Integer age,
                                @RequestParam(required = false) Long countryId) {
        return coachService.createCoach(nameCoach, lastName, age, countryId);
    }

    // Actualizar entrenador
    @PutMapping("/{id}")
    public CoachDTO updateCoach(@PathVariable Long id,
                                @RequestParam String nameCoach,
                                @RequestParam String lastName,
                                @RequestParam Integer age,
                                @RequestParam(required = false) Long countryId) {
        return coachService.updateCoach(id, nameCoach, lastName, age, countryId);
    }

    // Eliminar entrenador
    @DeleteMapping("/{id}")
    public void deleteCoach(@PathVariable Long id) {
        coachService.deleteCoach(id);
    }

    // Buscar entrenadores
    @GetMapping("/search")
    public List<CoachDTO> searchCoaches(@RequestParam String search) {
        return coachService.searchCoaches(search);
    }

    // Obtener entrenadores disponibles
    @GetMapping("/available")
    public List<CoachDTO> getAvailableCoaches() {
        return coachService.getAvailableCoaches();
    }
}