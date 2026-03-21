package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.controller.dto.AuthRequestDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.AuthResponseDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UserRequestDTO;
import edu.eci.dosw.DOSW_Library.core.model.Role;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.repository.UserRepository;
import edu.eci.dosw.DOSW_Library.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final UserRepository userRepository;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            UserService userService,
            UserRepository userRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.userRepository = userRepository;
    }

    public AuthResponseDTO register(UserRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setRole(request.getRole() == null ? Role.USER : request.getRole());

        User created = userService.createUser(user);
        return new AuthResponseDTO(jwtService.generateToken(created));
    }

    public AuthResponseDTO login(AuthRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );
        User user = userService.findByUsername(request.getUsername());
        return new AuthResponseDTO(jwtService.generateToken(user));
    }
}

