package edu.eci.dosw.DOSW_Library.persistence.relational.dao;

import edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaUserDao extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}


