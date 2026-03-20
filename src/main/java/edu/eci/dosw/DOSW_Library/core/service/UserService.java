package edu.eci.dosw.DOSW_Library.core.service;

import edu.eci.dosw.DOSW_Library.core.exception.UserNotFoundException;
import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.core.util.IdGeneratorUtil;
import edu.eci.dosw.DOSW_Library.core.validator.UserValidator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private final IdGeneratorUtil idGenerator = new IdGeneratorUtil();

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(Long id) {
        User user = users.get(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
        return user;
    }

    public User createUser(User user) {
        UserValidator.validate(user);
        Long id = idGenerator.nextId();
        user.setId(id);
        users.put(id, user);
        return user;
    }

    public User updateUser(Long id, User updatedUser) {
        UserValidator.validate(updatedUser);
        getUserById(id);
        updatedUser.setId(id);
        users.put(id, updatedUser);
        return updatedUser;
    }

    public void deleteUser(Long id) {
        User user = users.remove(id);
        if (user == null) {
            throw new UserNotFoundException("User not found with id: " + id);
        }
    }
}
