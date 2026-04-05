package edu.eci.dosw.DOSW_Library.persistence.nonrelational.repository;

import edu.eci.dosw.DOSW_Library.core.model.User;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.document.UserDocument;
import edu.eci.dosw.DOSW_Library.persistence.nonrelational.mapper.UserDocumentMapper;
import edu.eci.dosw.DOSW_Library.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

@Repository
@Profile("mongo")
public class UserRepositoryMongoImpl implements UserRepository {

    private final MongoUserRepository mongoUserRepository;
    private final UserDocumentMapper mapper;

    public UserRepositoryMongoImpl(MongoUserRepository mongoUserRepository, UserDocumentMapper mapper) {
        this.mongoUserRepository = mongoUserRepository;
        this.mapper = mapper;
    }

    @Override
    public List<User> findAll() {
        return mongoUserRepository.findAll().stream().map(mapper::toDomain).toList();
    }

    @Override
    public Optional<User> findById(Long id) {
        return mongoUserRepository.findById(String.valueOf(id)).map(mapper::toDomain);
    }

    @Override
    public User save(User user) {
        UserDocument document = mapper.toDocument(user);
        if (document.getId() == null) {
            document.setId(String.valueOf(Math.abs(UUID.randomUUID().getMostSignificantBits())));
        }
        return mapper.toDomain(mongoUserRepository.save(document));
    }

    @Override
    public void delete(User user) {
        if (user != null && user.getId() != null) {
            mongoUserRepository.deleteById(String.valueOf(user.getId()));
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return mongoUserRepository.findByUsername(username).map(mapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return mongoUserRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return mongoUserRepository.existsByEmail(email);
    }
}

