package com.jpa.controller;

import com.jpa.dto.FootballCompetitionDTO;
import com.jpa.service.FootballCompetitionService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class FootballCompetitionController {

    private final FootballCompetitionService competitionService;

    public FootballCompetitionController(FootballCompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    // Obtener todas las competiciones
    @GetMapping
    public List<FootballCompetitionDTO> getAllCompetitions() {
        return competitionService.getAllCompetitions();
    }

    // Obtener competición por ID
    @GetMapping("/{id}")
    public FootballCompetitionDTO getCompetitionById(@PathVariable Long id) {
        return competitionService.getCompetitionById(id);
    }

    // Crear competición
    @PostMapping
    public FootballCompetitionDTO createCompetition(@RequestParam String name,
                                                    @RequestParam Integer cuantityPrice,
                                                    @RequestParam String startDate,
                                                    @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return competitionService.createCompetition(name, cuantityPrice, start, end);
    }

    // Actualizar competición
    @PutMapping("/{id}")
    public FootballCompetitionDTO updateCompetition(@PathVariable Long id,
                                                    @RequestParam String name,
                                                    @RequestParam Integer cuantityPrice,
                                                    @RequestParam String startDate,
                                                    @RequestParam String endDate) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return competitionService.updateCompetition(id, name, cuantityPrice, start, end);
    }

    // Eliminar competición
    @DeleteMapping("/{id}")
    public void deleteCompetition(@PathVariable Long id) {
        competitionService.deleteCompetition(id);
    }

    // Buscar competiciones
    @GetMapping("/search")
    public List<FootballCompetitionDTO> searchCompetitions(@RequestParam String search) {
        return competitionService.searchCompetitions(search);
    }
}