package br.com.actionlabs.carboncalc.repository;

import br.com.actionlabs.carboncalc.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsersRepository extends MongoRepository<Users, String> {
    Optional<Users> findByEmail(String email);
}
