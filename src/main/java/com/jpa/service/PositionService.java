package com.jpa.service;

import com.jpa.dto.PositionDTO;
import com.jpa.entity.Position;
import com.jpa.repository.IPositionRepository;
import com.jpa.repository.IPlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PositionService {

    private final IPositionRepository positionRepository;
    private final IPlayerRepository playerRepository;

    public PositionService(IPositionRepository positionRepository,
                           IPlayerRepository playerRepository) {
        this.positionRepository = positionRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public List<PositionDTO> getAllPositions() {
        return positionRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PositionDTO getPositionById(Long id) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posición no encontrada"));
        return convertToDTO(position);
    }

    @Transactional
    public PositionDTO createPosition(String descPosition) {
        if (positionRepository.existsByDescPosition(descPosition)) {
            throw new RuntimeException("Ya existe una posición con esa descripción");
        }

        Position position = new Position();
        position.setDescPosition(descPosition);

        Position savedPosition = positionRepository.save(position);
        return convertToDTO(savedPosition);
    }

    @Transactional
    public PositionDTO updatePosition(Long id, String descPosition) {
        Position position = positionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Posición no encontrada"));

        if (!position.getDescPosition().equals(descPosition) && positionRepository.existsByDescPosition(descPosition)) {
            throw new RuntimeException("Ya existe una posición con esa descripción");
        }

        position.setDescPosition(descPosition);

        Position updatedPosition = positionRepository.save(position);
        return convertToDTO(updatedPosition);
    }

    @Transactional
    public void deletePosition(Long id) {
        if (!positionRepository.existsById(id)) {
            throw new RuntimeException("Posición no encontrada");
        }
        positionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PositionDTO> searchPositions(String search) {
        return positionRepository.findAll().stream()
                .filter(p -> p.getDescPosition().toLowerCase().contains(search.toLowerCase()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PositionDTO convertToDTO(Position position) {
        PositionDTO dto = new PositionDTO();
        dto.setIdPosition(position.getIdPosition());
        dto.setDescPosition(position.getDescPosition());

        // Contar usando query separada
        dto.setTotalPlayers((int) playerRepository.countByPosition_IdPosition(position.getIdPosition()));

        return dto;
    }
}