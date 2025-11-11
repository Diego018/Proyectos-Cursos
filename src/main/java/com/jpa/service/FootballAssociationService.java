package com.jpa.service;

import com.jpa.dto.FootballAssociationDTO;
import com.jpa.entity.Country;
import com.jpa.entity.FootballAssociation;
import com.jpa.repository.ICountryRepository;
import com.jpa.repository.IFootballAssociationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FootballAssociationService {

    private final IFootballAssociationRepository associationRepository;
    private final ICountryRepository countryRepository;

    public FootballAssociationService(IFootballAssociationRepository associationRepository,
                                      ICountryRepository countryRepository) {
        this.associationRepository = associationRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public List<FootballAssociationDTO> getAllAssociations() {
        return associationRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public FootballAssociationDTO getAssociationById(Long id) {
        FootballAssociation association = associationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asociación no encontrada"));
        return convertToDTO(association);
    }

    @Transactional
    public FootballAssociationDTO createAssociation(String nameAssociation, String president, Long countryId) {
        FootballAssociation association = new FootballAssociation();
        association.setNameAssociation(nameAssociation);
        association.setPresident(president);

        if (countryId != null) {
            Country country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new RuntimeException("País no encontrado"));
            association.setCountry(country);
        }

        FootballAssociation savedAssociation = associationRepository.save(association);
        return convertToDTO(savedAssociation);
    }

    @Transactional
    public FootballAssociationDTO updateAssociation(Long id, String nameAssociation, String president, Long countryId) {
        FootballAssociation association = associationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asociación no encontrada"));

        association.setNameAssociation(nameAssociation);
        association.setPresident(president);

        if (countryId != null) {
            Country country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new RuntimeException("País no encontrado"));
            association.setCountry(country);
        } else {
            association.setCountry(null);
        }

        FootballAssociation updatedAssociation = associationRepository.save(association);
        return convertToDTO(updatedAssociation);
    }

    @Transactional
    public void deleteAssociation(Long id) {
        if (!associationRepository.existsById(id)) {
            throw new RuntimeException("Asociación no encontrada");
        }
        associationRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<FootballAssociationDTO> searchAssociations(String search) {
        return associationRepository.findAll().stream()
                .filter(a -> a.getNameAssociation().toLowerCase().contains(search.toLowerCase()))
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private FootballAssociationDTO convertToDTO(FootballAssociation association) {
        FootballAssociationDTO dto = new FootballAssociationDTO();
        dto.setIdAssociation(association.getId_fotballAssociation());
        dto.setNameAssociation(association.getNameAssociation());
        dto.setPresident(association.getPresident());


        if (association.getCountry() != null) {
            dto.setCountryName(association.getCountry().getNombreCountry());
            dto.setCountryId(association.getCountry().getIdCountry());
        }


        dto.setTotalClubs(association.getClubs() != null ? association.getClubs().size() : 0);

        return dto;
    }
}