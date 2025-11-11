package com.jpa.service;

import com.jpa.dto.CoachDTO;
import com.jpa.entity.Coach;
import com.jpa.entity.Country;
import com.jpa.repository.ICoachRepository;
import com.jpa.repository.ICountryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoachService {

    private final ICoachRepository coachRepository;
    private final ICountryRepository countryRepository;

    public CoachService(ICoachRepository coachRepository, ICountryRepository countryRepository) {
        this.coachRepository = coachRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public List<CoachDTO> getAllCoaches() {
        return coachRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CoachDTO getCoachById(Long id) {
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));
        return convertToDTO(coach);
    }

    @Transactional
    public CoachDTO createCoach(String nameCoach, String lastName, Integer age, Long countryId) {
        Coach coach = new Coach();
        coach.setNameCoach(nameCoach);
        coach.setLastName(lastName);
        coach.setAge(age);

        if (countryId != null) {
            Country country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new RuntimeException("País no encontrado"));
            coach.setCountry(country);
        }

        Coach savedCoach = coachRepository.save(coach);
        return convertToDTO(savedCoach);
    }

    @Transactional
    public CoachDTO updateCoach(Long id, String nameCoach, String lastName, Integer age, Long countryId) {
        Coach coach = coachRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Entrenador no encontrado"));

        coach.setNameCoach(nameCoach);
        coach.setLastName(lastName);
        coach.setAge(age);

        if (countryId != null) {
            Country country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new RuntimeException("País no encontrado"));
            coach.setCountry(country);
        } else {
            coach.setCountry(null);
        }

        Coach updatedCoach = coachRepository.save(coach);
        return convertToDTO(updatedCoach);
    }

    @Transactional
    public void deleteCoach(Long id) {
        if (!coachRepository.existsById(id)) {
            throw new RuntimeException("Entrenador no encontrado");
        }
        coachRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CoachDTO> searchCoaches(String search) {
        List<Coach> coaches = coachRepository.findByNameCoachContainingIgnoreCase(search).stream().toList();
        coaches.addAll(coachRepository.findByLastNameContainingIgnoreCase(search));

        return coaches.stream()
                .distinct()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CoachDTO> getAvailableCoaches() {
        return coachRepository.findByClubIsNull().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CoachDTO convertToDTO(Coach coach) {
        CoachDTO dto = new CoachDTO();
        dto.setIdCoach(coach.getId_coach());
        dto.setNameCoach(coach.getNameCoach());
        dto.setLastName(coach.getLastName());
        dto.setAge(coach.getAge());

        // Acceder a country dentro de la transacción
        if (coach.getCountry() != null) {
            dto.setCountryName(coach.getCountry().getNombreCountry());
            dto.setCountryId(coach.getCountry().getIdCountry());
        }

        // Acceder a club dentro de la transacción
        if (coach.getClub() != null) {
            dto.setClubName(coach.getClub().getNameClub());
            dto.setClubId(coach.getClub().getIdClub());
        }

        return dto;
    }
}