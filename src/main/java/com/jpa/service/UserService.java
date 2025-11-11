package com.jpa.service;

import com.jpa.dto.UserDTO;
import com.jpa.entity.User;
import com.jpa.repository.IUserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Crear usuario
    public UserDTO createUser(String firstName, String lastName, String username, String email, String password) {
        // Validar si el email ya existe
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Validar si el username ya existe
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El username ya está en uso");
        }

        User user = User.builder()
                .firstName(firstName)
                .lastName(lastName)
                .username(username)
                .email(email)
                .password(password) // TODO: Implementar BCrypt para hash
                .build();

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // Validación de login
    public UserDTO validateLogin(String username, String password) {
        // Buscar por username o email
        Optional<User> userByUsername = userRepository.findByUsername(username);
        Optional<User> userByEmail = userRepository.findByEmail(username);

        User user = userByUsername.orElse(userByEmail.orElse(null));

        if (user != null && user.getPassword().equals(password)) {
            return convertToDTO(user);
        }

        return null; // Credenciales inválidas
    }

    // Obtener todos los usuarios
    public Set<UserDTO> getAllUsersDTO() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toSet());
    }

    // Obtener usuario por ID
    public UserDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));
        return convertToDTO(user);
    }

    // Borrar usuario por id
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario con ID " + id + " no encontrado");
        }
        userRepository.deleteById(id);
    }

    // Actualizar usuario
    public UserDTO updateUser(Long id, String firstName, String lastName, String username, String email) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario con ID " + id + " no encontrado"));

        // Validar si el nuevo email ya existe en otro usuario
        if (!user.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El email ya está registrado por otro usuario");
        }

        // Validar si el nuevo username ya existe en otro usuario
        if (!user.getUsername().equals(username) && userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El username ya está en uso por otro usuario");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUsername(username);
        user.setEmail(email);

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    // Actualizar contraseña
    public void updatePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Contraseña actual incorrecta");
        }

        user.setPassword(newPassword); // TODO: Implementar BCrypt para hash
        userRepository.save(user);
    }

    // Convertir User a UserDTO
    private UserDTO convertToDTO(User user) {
        return new UserDTO(
                user.getIdUser(),
                user.getFirstName(),
                user.getLastName(),
                user.getUsername(),
                user.getEmail()
        );
    }

    // Obtener usuario por email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    // Obtener usuario por username
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }

    // Verificar si un email existe
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    // Verificar si un username existe
    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}