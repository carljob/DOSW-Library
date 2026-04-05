package edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper;

import edu.eci.dosw.DOSW_Library.core.model.Role;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.UserDocument;
import org.springframework.stereotype.Component;

@Component
public class UserDocumentMapper {

    public User toDomain(UserDocument doc) {
        if (doc == null) {
            return null;
        }

        Role role = null;
        if (doc.getRol() != null) {
            try {
                role = Role.valueOf(doc.getRol());
            } catch (IllegalArgumentException ex) {
                role = null;
            }
        }

        return new User(
                toLong(doc.getId()),
                doc.getNombre(),
                doc.getEmail(),
                doc.getUsername(),
                doc.getPassword(),
                role,
                doc.getTipoMembresia(),
                doc.getFechaAgregado()
        );
    }

    public UserDocument toDocument(User user) {
        if (user == null) {
            return null;
        }

        return new UserDocument(
                toStringId(user.getId()),
                user.getName(),
                user.getEmail(),
                user.getUsername(),
                user.getPassword(),
                user.getRole() == null ? null : user.getRole().name(),
                user.getMembershipType(),
                user.getAddedDate()
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

