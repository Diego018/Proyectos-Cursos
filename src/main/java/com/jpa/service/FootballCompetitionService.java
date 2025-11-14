package com.jpa.service;

import com.jpa.dto.FootballCompetitionDTO;
import com.jpa.entity.FootballCompetition;
import com.jpa.repository.IFootballCompetitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FootballCompetitionService {

    private final IFootballCompetitionRepository competitionRepository;

    public FootballCompetitionService(IFootballCompetitionRepository competitionRepository) {
        this.competitionRepository = competitionRepository;
    }

    @Transactional(readOnly = true)
    public List<FootballCompetitionDTO> getAllCompetitions() {
        List<FootballCompetition> competitions = competitionRepository.findAll();
        return competitions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FootballCompetitionDTO getCompetitionById(Long id) {
        FootballCompetition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competición no encontrada"));
        return convertToDTO(competition);
    }

    @Transactional
    public FootballCompetitionDTO createCompetition(String name, Integer cuantityPrice,
                                                    LocalDate startDate, LocalDate endDate) {
        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        FootballCompetition competition = new FootballCompetition();
        competition.setName(name);
        competition.setCuantityPrice(cuantityPrice);
        competition.setStartDate(startDate);
        competition.setEndDate(endDate);

        FootballCompetition savedCompetition = competitionRepository.save(competition);
        return convertToDTO(savedCompetition);
    }

    @Transactional
    public FootballCompetitionDTO updateCompetition(Long id, String name, Integer cuantityPrice,
                                                    LocalDate startDate, LocalDate endDate) {
        FootballCompetition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Competición no encontrada"));

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            throw new RuntimeException("La fecha de fin no puede ser anterior a la fecha de inicio");
        }

        competition.setName(name);
        competition.setCuantityPrice(cuantityPrice);
        competition.setStartDate(startDate);
        competition.setEndDate(endDate);

        FootballCompetition updatedCompetition = competitionRepository.save(competition);
        return convertToDTO(updatedCompetition);
    }

    @Transactional
    public void deleteCompetition(Long id) {
        if (!competitionRepository.existsById(id)) {
            throw new RuntimeException("Competición no encontrada");
        }
        competitionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<FootballCompetitionDTO> searchCompetitions(String search) {
        return competitionRepository.findByNameContainingIgnoreCase(search).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FootballCompetitionDTO convertToDTO(FootballCompetition competition) {
        FootballCompetitionDTO dto = new FootballCompetitionDTO();
        dto.setIdCompetition(competition.getIdFootballCompetition());
        dto.setName(competition.getName());
        dto.setCuantityPrice(competition.getCuantityPrice());
        dto.setStartDate(competition.getStartDate());
        dto.setEndDate(competition.getEndDate());

        // Manejo seguro de clubs (lazy loading)
        Set<String> clubNames = new HashSet<>();
        if (competition.getClubs() != null) {
            dto.setTotalClubs(competition.getClubs().size());
            clubNames = competition.getClubs().stream()
                    .map(club -> club.getNameClub() != null ? club.getNameClub() : "Sin nombre")
                    .collect(Collectors.toSet());
        } else {
            dto.setTotalClubs(0);
        }
        dto.setCompetitionClubs(clubNames);

        return dto;
    }
}