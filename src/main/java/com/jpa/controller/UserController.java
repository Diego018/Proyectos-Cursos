package com.jpa.controller;

import com.jpa.dto.UserDTO;
import com.jpa.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Obtener todos los usuarios
    @GetMapping
    public Set<UserDTO> getAllUsers() {
        return userService.getAllUsersDTO();
    }

    // Crear usuario
    @PostMapping
    public UserDTO createUser(@RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String username,
                              @RequestParam String email,
                              @RequestParam String password) {
        return userService.createUser(firstName, lastName, username, email, password);
    }

    // Actualizar usuario
    @PutMapping("/{id}")
    public UserDTO updateUser(@PathVariable Long id,
                              @RequestParam String firstName,
                              @RequestParam String lastName,
                              @RequestParam String username,
                              @RequestParam String email) {
        return userService.updateUser(id, firstName, lastName, username, email);
    }

    // Eliminar usuario
    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }
}
