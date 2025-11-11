package com.jpa.controller;

import com.jpa.dto.PositionDTO;
import com.jpa.service.PositionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/positions")
public class PositionController {

    private final PositionService positionService;

    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    // Obtener todas las posiciones
    @GetMapping
    public List<PositionDTO> getAllPositions() {
        return positionService.getAllPositions();
    }

    // Obtener posición por ID
    @GetMapping("/{id}")
    public PositionDTO getPositionById(@PathVariable Long id) {
        return positionService.getPositionById(id);
    }

    // Crear posición
    @PostMapping
    public PositionDTO createPosition(@RequestParam String descPosition) {
        return positionService.createPosition(descPosition);
    }

    // Actualizar posición
    @PutMapping("/{id}")
    public PositionDTO updatePosition(@PathVariable Long id,
                                      @RequestParam String descPosition) {
        return positionService.updatePosition(id, descPosition);
    }

    // Eliminar posición
    @DeleteMapping("/{id}")
    public void deletePosition(@PathVariable Long id) {
        positionService.deletePosition(id);
    }

    // Buscar posiciones
    @GetMapping("/search")
    public List<PositionDTO> searchPositions(@RequestParam String search) {
        return positionService.searchPositions(search);
    }
}