package com.jpa.controller;

import com.jpa.dto.CountryDTO;
import com.jpa.service.CountryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/countries")
public class CountryController {

    private final CountryService countryService;

    public CountryController(CountryService countryService) {
        this.countryService = countryService;
    }

    // Obtener todos los países
    @GetMapping
    public List<CountryDTO> getAllCountries() {
        return countryService.getAllCountries();
    }

    // Obtener país por ID
    @GetMapping("/{id}")
    public CountryDTO getCountryById(@PathVariable Long id) {
        return countryService.getCountryById(id);
    }

    // Crear país
    @PostMapping
    public CountryDTO createCountry(@RequestParam String nombreCountry) {
        return countryService.createCountry(nombreCountry);
    }

    // Actualizar país
    @PutMapping("/{id}")
    public CountryDTO updateCountry(@PathVariable Long id,
                                    @RequestParam String nombreCountry) {
        return countryService.updateCountry(id, nombreCountry);
    }

    // Eliminar país
    @DeleteMapping("/{id}")
    public void deleteCountry(@PathVariable Long id) {
        countryService.deleteCountry(id);
    }

    // Buscar países
    @GetMapping("/search")
    public List<CountryDTO> searchCountries(@RequestParam String search) {
        return countryService.searchCountries(search);
    }
}