package com.jpa.controller;

import com.jpa.dto.PlayerDTO;
import com.jpa.service.PlayerService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Obtener todos los jugadores
    @GetMapping
    public List<PlayerDTO> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    // Obtener jugador por ID
    @GetMapping("/{id}")
    public PlayerDTO getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id);
    }

    // Crear jugador
    @PostMapping
    public PlayerDTO createPlayer(@RequestParam String namePlayer,
                                  @RequestParam String lastName,
                                  @RequestParam Integer age,
                                  @RequestParam(required = false) Long clubId,
                                  @RequestParam(required = false) Long positionId,
                                  @RequestParam(required = false) Long countryId) {
        return playerService.createPlayer(namePlayer, lastName, age, clubId, positionId, countryId);
    }

    // Actualizar jugador
    @PutMapping("/{id}")
    public PlayerDTO updatePlayer(@PathVariable Long id,
                                  @RequestParam String namePlayer,
                                  @RequestParam String lastName,
                                  @RequestParam Integer age,
                                  @RequestParam(required = false) Long clubId,
                                  @RequestParam(required = false) Long positionId,
                                  @RequestParam(required = false) Long countryId) {
        return playerService.updatePlayer(id, namePlayer, lastName, age, clubId, positionId, countryId);
    }

    // Eliminar jugador
    @DeleteMapping("/{id}")
    public void deletePlayer(@PathVariable Long id) {
        playerService.deletePlayer(id);
    }

    // Buscar jugadores
    @GetMapping("/search")
    public List<PlayerDTO> searchPlayers(@RequestParam String search) {
        return playerService.searchPlayers(search);
    }

    // Obtener jugadores por club
    @GetMapping("/by-club/{clubId}")
    public List<PlayerDTO> getPlayersByClub(@PathVariable Long clubId) {
        return playerService.getPlayersByClub(clubId);
    }
}