package com.jpa.service;

import com.jpa.dto.PlayerDTO;
import com.jpa.entity.Club;
import com.jpa.entity.Country;
import com.jpa.entity.Player;
import com.jpa.entity.Position;
import com.jpa.repository.IClubRepository;
import com.jpa.repository.ICountryRepository;
import com.jpa.repository.IPlayerRepository;
import com.jpa.repository.IPositionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final IPlayerRepository playerRepository;
    private final IClubRepository clubRepository;
    private final IPositionRepository positionRepository;
    private final ICountryRepository countryRepository;

    public PlayerService(IPlayerRepository playerRepository,
                         IClubRepository clubRepository,
                         IPositionRepository positionRepository,
                         ICountryRepository countryRepository) {
        this.playerRepository = playerRepository;
        this.clubRepository = clubRepository;
        this.positionRepository = positionRepository;
        this.countryRepository = countryRepository;
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));
        return convertToDTO(player);
    }

    @Transactional
    public PlayerDTO createPlayer(String namePlayer, String lastName, Integer age,
                                  Long clubId, Long positionId, Long countryId) {
        Player player = new Player();
        player.setNamePlayer(namePlayer);
        player.setLastName(lastName);
        player.setAge(age);

        if (clubId != null) {
            Club club = clubRepository.findById(clubId)
                    .orElseThrow(() -> new RuntimeException("Club no encontrado"));
            player.setClub(club);
        }

        if (positionId != null) {
            Position position = positionRepository.findById(positionId)
                    .orElseThrow(() -> new RuntimeException("Posición no encontrada"));
            player.setPosition(position);
        }

        if (countryId != null) {
            Country country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new RuntimeException("País no encontrado"));
            player.setCountry(country);
        }

        Player savedPlayer = playerRepository.save(player);
        return convertToDTO(savedPlayer);
    }

    @Transactional
    public PlayerDTO updatePlayer(Long id, String namePlayer, String lastName, Integer age,
                                  Long clubId, Long positionId, Long countryId) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Jugador no encontrado"));

        player.setNamePlayer(namePlayer);
        player.setLastName(lastName);
        player.setAge(age);

        if (clubId != null) {
            Club club = clubRepository.findById(clubId)
                    .orElseThrow(() -> new RuntimeException("Club no encontrado"));
            player.setClub(club);
        } else {
            player.setClub(null);
        }

        if (positionId != null) {
            Position position = positionRepository.findById(positionId)
                    .orElseThrow(() -> new RuntimeException("Posición no encontrada"));
            player.setPosition(position);
        } else {
            player.setPosition(null);
        }

        if (countryId != null) {
            Country country = countryRepository.findById(countryId)
                    .orElseThrow(() -> new RuntimeException("País no encontrado"));
            player.setCountry(country);
        } else {
            player.setCountry(null);
        }

        Player updatedPlayer = playerRepository.save(player);
        return convertToDTO(updatedPlayer);
    }

    @Transactional
    public void deletePlayer(Long id) {
        if (!playerRepository.existsById(id)) {
            throw new RuntimeException("Jugador no encontrado");
        }
        playerRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> searchPlayers(String search) {
        List<Player> players = playerRepository.findByNamePlayerContainingIgnoreCase(search).stream().toList();
        players.addAll(playerRepository.findByLastNameContainingIgnoreCase(search));

        return players.stream()
                .distinct()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlayerDTO> getPlayersByClub(Long clubId) {
        return playerRepository.findByClub_IdClub(clubId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PlayerDTO convertToDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setIdPlayer(player.getIdPlayer());
        dto.setNamePlayer(player.getNamePlayer());
        dto.setLastName(player.getLastName());
        dto.setAge(player.getAge());

        // Acceder a relaciones dentro de la transacción
        if (player.getClub() != null) {
            dto.setClubName(player.getClub().getNameClub());
            dto.setClubId(player.getClub().getIdClub());
        }

        if (player.getPosition() != null) {
            dto.setPositionName(player.getPosition().getDescPosition());
            dto.setPositionId(player.getPosition().getIdPosition());
        }

        if (player.getCountry() != null) {
            dto.setCountryName(player.getCountry().getNombreCountry());
            dto.setCountryId(player.getCountry().getIdCountry());
        }

        return dto;
    }
}