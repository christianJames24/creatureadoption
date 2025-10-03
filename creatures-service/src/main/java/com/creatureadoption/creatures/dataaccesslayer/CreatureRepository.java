package com.creatureadoption.creatures.dataaccesslayer;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CreatureRepository extends JpaRepository<Creature, Integer> {

    Creature findByCreatureIdentifier_CreatureId(String creatureId);
}