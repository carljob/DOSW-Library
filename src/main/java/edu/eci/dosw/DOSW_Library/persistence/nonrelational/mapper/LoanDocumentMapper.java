package edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Book;
import edu.eci.dosw.DOSW_Library.core.model.Loan;
import edu.eci.dosw.DOSW_Library.core.model.LoanHistoryEntry;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.BookDocument;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.LoanDocument;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.UserDocument;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class LoanDocumentMapper {

    private final BookDocumentMapper bookDocumentMapper;
    private final UserDocumentMapper userDocumentMapper;

    public LoanDocumentMapper(BookDocumentMapper bookDocumentMapper, UserDocumentMapper userDocumentMapper) {
        this.bookDocumentMapper = bookDocumentMapper;
        this.userDocumentMapper = userDocumentMapper;
    }

    public Loan toDomain(LoanDocument doc) {
        if (doc == null) {
            return null;
        }

        Book book = null;
        if (doc.getLibroId() != null) {
            BookDocument bookDocument = new BookDocument();
            bookDocument.setId(doc.getLibroId());
            book = bookDocumentMapper.toDomain(bookDocument);
        }

        User user = null;
        if (doc.getUsuarioId() != null) {
            UserDocument userDocument = new UserDocument();
            userDocument.setId(doc.getUsuarioId());
            user = userDocumentMapper.toDomain(userDocument);
        }

        List<LoanHistoryEntry> history = doc.getHistorial() == null
                ? new ArrayList<>()
                : doc.getHistorial().stream()
                .map(entry -> new LoanHistoryEntry(entry.getStatus(), entry.getFechaEjecucion()))
                .toList();

        return new Loan(
                toLong(doc.getId()),
                book,
                user,
                doc.getFechaPrestamo(),
                doc.getFechaDevolucion(),
                doc.isDevuelto(),
                history
        );
    }

    public LoanDocument toDocument(Loan loan) {
        if (loan == null) {
            return null;
        }

        List<LoanDocument.LoanHistoryEntryDocument> history = loan.getHistory() == null
                ? new ArrayList<>()
                : loan.getHistory().stream()
                .map(entry -> new LoanDocument.LoanHistoryEntryDocument(entry.getStatus(), entry.getExecutedAt()))
                .toList();

        return new LoanDocument(
                toStringId(loan.getId()),
                loan.getBook() != null && loan.getBook().getId() != null ? String.valueOf(loan.getBook().getId()) : null,
                loan.getUser() != null && loan.getUser().getId() != null ? String.valueOf(loan.getUser().getId()) : null,
                loan.getLoanDate(),
                loan.getReturnDate(),
                loan.isReturned(),
                loan.getLoanDate(),
                history
        );
    }

    private Long toLong(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String toStringId(Long value) {
        return value == null ? null : String.valueOf(value);
    }
}

