package edu.eci.dosw.DOSW_Library.persistence.relational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.relational.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserPersistenceMapper {

    public User toDomain(UserEntity entity) {
        if (entity == null) return null;
        return new User(
                entity.getId(),
                entity.getName(),
                entity.getEmail(),
                entity.getUsername(),
                entity.getPassword(),
                entity.getRole(),
                entity.getMembershipType(),
                entity.getAddedDate()
        );
    }

    public UserEntity toEntity(User domain) {
        if (domain == null) return null;
        UserEntity entity = new UserEntity();
        entity.setId(domain.getId());
        entity.setName(domain.getName());
        entity.setEmail(domain.getEmail());
        entity.setUsername(domain.getUsername());
        entity.setPassword(domain.getPassword());
        entity.setRole(domain.getRole());
        entity.setMembershipType(domain.getMembershipType());
        entity.setAddedDate(domain.getAddedDate());
        return entity;
    }
}
