package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.UserDTO;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.model.UserType;

public final class UserMapper {
    private UserMapper() {
    }

    public static User toModel(UserDTO dto) {
        UserType type = dto.getUserType() == null ? UserType.STANDARD : UserType.valueOf(dto.getUserType().toUpperCase());
        return new User(dto.getId(), dto.getName(), dto.getEmail(), type);
    }

    public static UserDTO toDto(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getUserType().name());
    }
}
