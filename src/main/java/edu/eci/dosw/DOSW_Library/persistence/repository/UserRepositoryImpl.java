package edu.eci.dosw.DOSW_Library.persistence.repository;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.dao.JpaUserDao;
import edu.eci.dosw.DOSW_Library.persistence.mapper.UserPersistenceMapper;
import edu.eci.dosw.DOSW_Library.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserDao jpaUserDao;
    private final UserPersistenceMapper mapper;

    public UserRepositoryImpl(JpaUserDao jpaUserDao, UserPersistenceMapper mapper) {
        this.jpaUserDao = jpaUserDao;
        this.mapper = mapper;
    }

    @Override
    public List<User> findAll() {
        return jpaUserDao.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return jpaUserDao.findById(id).map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        return mapper.toDomain(jpaUserDao.save(mapper.toEntity(user)));
    }

    @Override
    public void delete(User user) {
        jpaUserDao.delete(mapper.toEntity(user));
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return jpaUserDao.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return jpaUserDao.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaUserDao.existsByEmail(email);
    }
}

