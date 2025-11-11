package com.jpa.service;

import com.jpa.dto.CountryDTO;
import com.jpa.entity.Country;
import com.jpa.repository.ICountryRepository;
import com.jpa.repository.IFootballAssociationRepository;
import com.jpa.repository.IPlayerRepository;
import com.jpa.repository.ICoachRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CountryService {

    private final ICountryRepository countryRepository;
    private final IFootballAssociationRepository associationRepository;
    private final IPlayerRepository playerRepository;
    private final ICoachRepository coachRepository;

    public CountryService(ICountryRepository countryRepository,
                          IFootballAssociationRepository associationRepository,
                          IPlayerRepository playerRepository,
                          ICoachRepository coachRepository) {
        this.countryRepository = countryRepository;
        this.associationRepository = associationRepository;
        this.playerRepository = playerRepository;
        this.coachRepository = coachRepository;
    }

    @Transactional(readOnly = true)
    public List<CountryDTO> getAllCountries() {
        return countryRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CountryDTO getCountryById(Long id) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("País no encontrado"));
        return convertToDTO(country);
    }

    @Transactional
    public CountryDTO createCountry(String nombreCountry) {
        if (countryRepository.existsByNombreCountry(nombreCountry)) {
            throw new RuntimeException("Ya existe un país con ese nombre");
        }

        Country country = new Country();
        country.setNombreCountry(nombreCountry);

        Country savedCountry = countryRepository.save(country);
        return convertToDTO(savedCountry);
    }

    @Transactional
    public CountryDTO updateCountry(Long id, String nombreCountry) {
        Country country = countryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("País no encontrado"));

        if (!country.getNombreCountry().equals(nombreCountry) && countryRepository.existsByNombreCountry(nombreCountry)) {
            throw new RuntimeException("Ya existe un país con ese nombre");
        }

        country.setNombreCountry(nombreCountry);

        Country updatedCountry = countryRepository.save(country);
        return convertToDTO(updatedCountry);
    }

    @Transactional
    public void deleteCountry(Long id) {
        if (!countryRepository.existsById(id)) {
            throw new RuntimeException("País no encontrado");
        }
        countryRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<CountryDTO> searchCountries(String search) {
        return countryRepository.findAll().stream()
                .filter(c -> c.getNombreCountry().toLowerCase().contains(search.toLowerCase()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private CountryDTO convertToDTO(Country country) {
        CountryDTO dto = new CountryDTO();
        dto.setIdCountry(country.getIdCountry());
        dto.setNameCountry(country.getNombreCountry());

        // Contar usando queries separadas en lugar de cargar las colecciones
        dto.setTotalAssociations((long) associationRepository.countByCountry_IdCountry(country.getIdCountry()));
        dto.setTotalPlayers((long) playerRepository.countByCountry_IdCountry(country.getIdCountry()));
        dto.setTotalCoaches((long) coachRepository.countByCountry_IdCountry(country.getIdCountry()));

        return dto;
    }
}