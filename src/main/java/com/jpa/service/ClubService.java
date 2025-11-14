package com.jpa.service;

import com.jpa.dto.ClubDTO;
import com.jpa.entity.Club;
import com.jpa.entity.Coach;
import com.jpa.entity.FootballAssociation;
import com.jpa.repository.IClubRepository;
import com.jpa.repository.ICoachRepository;
import com.jpa.repository.IFootballAssociationRepository;
import com.jpa.repository.IPlayerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClubService {

    private final IClubRepository clubRepository;
    private final ICoachRepository coachRepository;
    private final IFootballAssociationRepository associationRepository;
    private final IPlayerRepository playerRepository;

    public ClubService(IClubRepository clubRepository,
                       ICoachRepository coachRepository,
                       IFootballAssociationRepository associationRepository,
                       IPlayerRepository playerRepository) {
        this.clubRepository = clubRepository;
        this.coachRepository = coachRepository;
        this.associationRepository = associationRepository;
        this.playerRepository = playerRepository;
    }

    @Transactional(readOnly = true)
    public List<ClubDTO> getAllClubs() {
        return clubRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ClubDTO getClubById(Long id) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club no encontrado"));
        return convertToDTO(club);
    }

    @Transactional
    public ClubDTO createClub(String nameClub, Long coachId, Long associationId) {
        if (nameClub == null || nameClub.trim().isEmpty()) {
            throw new RuntimeException("El nombre del club es obligatorio");
        }
        if (clubRepository.existsByNameClub(nameClub)) {
            throw new RuntimeException("Ya existe un club con ese nombre");
        }

        Club club = new Club();
        club.setNameClub(nameClub);

        if (coachId != null) {
            Coach coach = coachRepository.findById(coachId)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
            club.setCoach(coach);
        }

        if (associationId != null) {
            FootballAssociation association = associationRepository.findById(associationId)
                    .orElseThrow(() -> new RuntimeException("Asociación no encontrada"));
            club.setFootballAssociation(association);
        }

        Club savedClub = clubRepository.save(club);
        return convertToDTO(savedClub);

    }

    @Transactional
    public ClubDTO updateClub(Long id, String nameClub, Long coachId, Long associationId) {
        Club club = clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Club no encontrado"));

        if (nameClub == null || nameClub.trim().isEmpty()) {
            throw new RuntimeException("El nombre del club es obligatorio");
        }
        if (!club.getNameClub().equals(nameClub) && clubRepository.existsByNameClub(nameClub)) {
            throw new RuntimeException("Ya existe un club con ese nombre");
        }

        club.setNameClub(nameClub);

        if (coachId != null) {
            Coach coach = coachRepository.findById(coachId)
                    .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
            club.setCoach(coach);
        } else {
            club.setCoach(null);
        }

        if (associationId != null) {
            FootballAssociation association = associationRepository.findById(associationId)
                    .orElseThrow(() -> new RuntimeException("Asociación no encontrada"));
            club.setFootballAssociation(association);
        } else {
            club.setFootballAssociation(null);
        }

        Club updatedClub = clubRepository.save(club);
        return convertToDTO(updatedClub);
    }

    @Transactional
    public void deleteClub(Long id) {
        if (!clubRepository.existsById(id)) {
            throw new RuntimeException("Club no encontrado");
        }
        clubRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<ClubDTO> searchClubs(String search) {
        return clubRepository.findByNameClubContainingIgnoreCase(search).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ClubDTO convertToDTO(Club club) {
        ClubDTO dto = new ClubDTO();
        dto.setIdClub(club.getIdClub());
        dto.setNameClub(club.getNameClub() != null ? club.getNameClub() : "");

        if (club.getCoach() != null) {
            dto.setCoachId(club.getCoach().getId_coach());
            dto.setCoachName((club.getCoach().getNameCoach() != null ? club.getCoach().getNameCoach() : "") +
                    " " + (club.getCoach().getLastName() != null ? club.getCoach().getLastName() : ""));
        } else {
            dto.setCoachId(null);
            dto.setCoachName("");
        }

        if (club.getFootballAssociation() != null) {
            dto.setAssociationId(club.getFootballAssociation().getId_fotballAssociation());
            dto.setAssociationName(club.getFootballAssociation().getNameAssociation() != null ?
                    club.getFootballAssociation().getNameAssociation() : "");
        } else {
            dto.setAssociationId(null);
            dto.setAssociationName("");
        }

        dto.setTotalPlayers((int) playerRepository.countByClub_IdClub(club.getIdClub()));

        return dto;
    }
}