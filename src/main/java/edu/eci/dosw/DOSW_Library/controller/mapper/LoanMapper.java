package edu.eci.dosw.DOSW_Library.controller.mapper;

import edu.eci.dosw.DOSW_Library.controller.dto.LoanResponseDTO;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LoanMapper {

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    LoanResponseDTO toResponse(Loan loan);
}
