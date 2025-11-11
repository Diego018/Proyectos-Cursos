package com.jpa.controller;

import com.jpa.dto.FootballAssociationDTO;
import com.jpa.service.FootballAssociationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/associations")
public class FootballAssociationController {

    private final FootballAssociationService associationService;

    public FootballAssociationController(FootballAssociationService associationService) {
        this.associationService = associationService;
    }

    // Obtener todas las asociaciones
    @GetMapping
    public List<FootballAssociationDTO> getAllAssociations() {
        return associationService.getAllAssociations();
    }

    // Obtener asociación por ID
    @GetMapping("/{id}")
    public FootballAssociationDTO getAssociationById(@PathVariable Long id) {
        return associationService.getAssociationById(id);
    }

    // Crear asociación
    @PostMapping
    public FootballAssociationDTO createAssociation(@RequestParam String nameAssociation,
                                                    @RequestParam String president,
                                                    @RequestParam Long countryId) {
        return associationService.createAssociation(nameAssociation, president, countryId);
    }

    // Actualizar asociación
    @PutMapping("/{id}")
    public FootballAssociationDTO updateAssociation(@PathVariable Long id,
                                                    @RequestParam String nameAssociation,
                                                    @RequestParam String president,
                                                    @RequestParam Long countryId) {
        return associationService.updateAssociation(id, nameAssociation, president, countryId);
    }

    // Eliminar asociación
    @DeleteMapping("/{id}")
    public void deleteAssociation(@PathVariable Long id) {
        associationService.deleteAssociation(id);
    }

    // Buscar asociaciones
    @GetMapping("/search")
    public List<FootballAssociationDTO> searchAssociations(@RequestParam String search) {
        return associationService.searchAssociations(search);
    }
}