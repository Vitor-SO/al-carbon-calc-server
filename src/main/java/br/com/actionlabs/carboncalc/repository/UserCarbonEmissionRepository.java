package br.com.actionlabs.carboncalc.repository;

import br.com.actionlabs.carboncalc.model.UserCarbonEmission;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCarbonEmissionRepository  extends MongoRepository<UserCarbonEmission, String> {
}
