package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.UserRequestDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.UserResponseDTO;
import edu.eci.dosw.DOSW_Library.core.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponse(User user);

    @Mapping(target = "id", ignore = true)
    void updateEntity(UserRequestDTO dto, @MappingTarget User user);
}
