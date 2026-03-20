package edu.eci.dosw.DOSW_Library.core.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.model.UserType;
import org.junit.jupiter.api.Test;

class UserServiceTest {

    @Test
    void shouldCreateAndFindUser() {
        UserService userService = new UserService();

        User user = new User(null, "Ana", "ana@email.com", UserType.STANDARD);
        User created = userService.createUser(user);

        assertNotNull(created.getId());
        assertEquals("Ana", userService.getUserById(created.getId()).getName());
        assertEquals(1, userService.getAllUsers().size());
    }
}

