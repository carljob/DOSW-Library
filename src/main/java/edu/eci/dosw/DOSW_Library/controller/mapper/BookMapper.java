package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.BookRequestDTO;
import edu.eci.dosw.DOSW_Library.controller.dto.BookResponseDTO;
import edu.eci.dosw.DOSW_Library.core.model.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    Book toEntity(BookRequestDTO dto);

    BookResponseDTO toResponse(Book book);

    @Mapping(target = "id", ignore = true)
    void updateEntity(BookRequestDTO dto, @MappingTarget Book book);
}
