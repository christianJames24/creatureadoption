package com.creatureadoption.adoptions.dataaccesslayer;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface AdoptionRepository extends MongoRepository<Adoption, String> {

    Adoption findByAdoptionIdentifier_AdoptionId(String adoptionId);

    @Query("{'customerId': ?0}")
    List<Adoption> findByCustomerId(String customerId);

    @Query("{'creatureId': ?0}")
    List<Adoption> findByCreatureId(String creatureId);

    @Query("{'adoptionStatus': ?0}")
    List<Adoption> findByAdoptionStatus(AdoptionStatus adoptionStatus);

    @Query("{'profileStatus': ?0}")
    List<Adoption> findByProfileStatus(ProfileStatus profileStatus);
}