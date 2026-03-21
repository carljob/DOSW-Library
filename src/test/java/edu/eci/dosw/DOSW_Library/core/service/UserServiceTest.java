package edu.eci.dosw.DOSW_Library.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import edu.eci.dosw.DOSW_Library.core.model.Role;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldCreateAndFindUser() {
        User user = new User(null, "Ana", "ana@email.com", "ana", "plain", Role.USER);

        when(passwordEncoder.encode("plain")).thenReturn("encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User saved = inv.getArgument(0);
            saved.setId(1L);
            return saved;
        });
        when(userRepository.findById(1L)).thenReturn(Optional.of(new User(1L, "Ana", "ana@email.com", "ana", "encoded", Role.USER)));

        User created = userService.createUser(user);

        assertNotNull(created.getId());
        assertEquals("encoded", created.getPassword());
        assertEquals("Ana", userService.getUserById(created.getId()).getName());
        verify(userRepository).save(any(User.class));
    }
}
