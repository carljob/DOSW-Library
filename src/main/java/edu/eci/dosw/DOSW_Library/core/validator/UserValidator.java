package edu.eci.dosw.DOSW_Library.core.validator;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.util.ValidationUtil;

public final class UserValidator {
    private UserValidator() {
    }

    public static void validate(User user) {
        ValidationUtil.requireNotNull(user, "User cannot be null");
        ValidationUtil.requireNotBlank(user.getName(), "User name is required");
        ValidationUtil.requireNotBlank(user.getEmail(), "User email is required");
        ValidationUtil.requireNotBlank(user.getUsername(), "Username is required");
        ValidationUtil.requireNotBlank(user.getPassword(), "Password is required");
        ValidationUtil.requireNotNull(user.getRole(), "Role is required");
    }
}
