package edu.eci.dosw.DOSW_Library.repository;

import edu.eci.dosw.DOSW_Library.core.model.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    void delete(User user);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}

