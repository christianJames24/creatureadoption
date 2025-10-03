package com.creatureadoption.creatures.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
public class CreatureRepositoryIntegrationTest {

    @Autowired
    private CreatureRepository repository;

    private Creature savedEntity;
    private CreatureIdentifier identifier;

    @BeforeEach
    public void setupDb() {
        repository.deleteAll();

        //so i dont make data in every thing
        identifier = new CreatureIdentifier();
        CreatureTraits traits = new CreatureTraits(10, 10, 10, Temperament.DOCILE);

        Creature entity = new Creature(
                "TestCreature",
                "TestSpecies",
                CreatureType.FIRE,
                Rarity.COMMON,
                1,
                2,
                100,
                0,
                CreatureStatus.AVAILABLE,
                traits
        );
        entity.setCreatureIdentifier(identifier);

        savedEntity = repository.save(entity);

        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isNotNull();
    }

    @Test
    public void whenValidCreatureId_thenCreatureShouldBeFound() {
        //arrange
        String creatureId = savedEntity.getCreatureIdentifier().getCreatureId();

        //act
        Creature found = repository.findByCreatureIdentifier_CreatureId(creatureId);

        //assert
        assertThat(found).isNotNull();
        assertThat(found.getCreatureIdentifier().getCreatureId()).isEqualTo(creatureId);
        assertThat(found.getName()).isEqualTo(savedEntity.getName());
    }

    @Test
    public void whenInvalidCreatureId_thenCreatureShouldNotBeFound() {
        //arrange
        String invalidId = "invalidId";

        //act
        Creature found = repository.findByCreatureIdentifier_CreatureId(invalidId);

        //assert
        assertThat(found).isNull();
    }

    @Test
    public void whenSaveNewCreature_thenItShouldBePersisted() {
        //arrange
        CreatureTraits traits = new CreatureTraits(15, 15, 15, Temperament.FRIENDLY);
        CreatureIdentifier newIdentifier = new CreatureIdentifier();

        Creature newEntity = new Creature(
                "AnotherCreature",
                "AnotherSpecies",
                CreatureType.WATER,
                Rarity.RARE,
                5,
                3,
                120,
                50,
                CreatureStatus.AVAILABLE,
                traits
        );
        newEntity.setCreatureIdentifier(newIdentifier);

        // act
        Creature saved = repository.save(newEntity);

        //assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("AnotherCreature");

        //see if the created data can be retrieved
        Creature retrieved = repository.findByCreatureIdentifier_CreatureId(newIdentifier.getCreatureId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getCreatureIdentifier().getCreatureId()).isEqualTo(newIdentifier.getCreatureId());
    }

    @Test
    public void whenDeleteCreature_thenItShouldBeRemoved() {
        //arrange
        String creatureId = savedEntity.getCreatureIdentifier().getCreatureId();

        //act
        repository.delete(savedEntity);

        //assert
        Creature found = repository.findByCreatureIdentifier_CreatureId(creatureId);
        assertThat(found).isNull();
    }

    @Test
    public void whenFindAll_thenAllCreaturesShouldBeReturned() {
        //arrange
        CreatureTraits traits = new CreatureTraits(15, 15, 15, Temperament.FRIENDLY);
        CreatureIdentifier newIdentifier = new CreatureIdentifier();

        Creature newEntity = new Creature(
                "AnotherCreature",
                "AnotherSpecies",
                CreatureType.WATER,
                Rarity.RARE,
                5,
                3,
                120,
                50,
                CreatureStatus.AVAILABLE,
                traits
        );
        newEntity.setCreatureIdentifier(newIdentifier);
        repository.save(newEntity);

        //act
        List<Creature> creatures = repository.findAll();

        //assert
        assertThat(creatures).isNotNull();
        assertThat(creatures.size()).isEqualTo(2);
    }

    @Test
    public void whenUpdateCreature_thenChangesAreSaved() {
        // arrange
        String creatureId = savedEntity.getCreatureIdentifier().getCreatureId();
        Creature toUpdate = repository.findByCreatureIdentifier_CreatureId(creatureId);
        toUpdate.setName("UpdatedName");
        toUpdate.setLevel(10);

        // act
        Creature updated = repository.save(toUpdate);

        // assert
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("UpdatedName");
        assertThat(updated.getLevel()).isEqualTo(10);

        // verify from fresh query
        Creature retrieved = repository.findByCreatureIdentifier_CreatureId(creatureId);
        assertThat(retrieved.getName()).isEqualTo("UpdatedName");
        assertThat(retrieved.getLevel()).isEqualTo(10);
    }

    @Test
    public void testCreatureIdentifierCreation() {
        // arrange & act
        CreatureIdentifier identifier1 = new CreatureIdentifier();
        CreatureIdentifier identifier2 = new CreatureIdentifier();

        // assert
        assertThat(identifier1.getCreatureId()).isNotNull();
        assertThat(identifier1.getRegistrationCode()).isNotNull();
        assertThat(identifier1.getRegistrationCode()).startsWith("REG-");

        // verify uniqueness
        assertThat(identifier1.getCreatureId()).isNotEqualTo(identifier2.getCreatureId());
        assertThat(identifier1.getRegistrationCode()).isNotEqualTo(identifier2.getRegistrationCode());
    }

    @Test
    public void testCreatureTraitsCreation() {
        // arrange & act
        CreatureTraits traits = new CreatureTraits(50, 75, 30, Temperament.AGGRESSIVE);

        // assert
        assertThat(traits.getStrength()).isEqualTo(50);
        assertThat(traits.getIntelligence()).isEqualTo(75);
        assertThat(traits.getAgility()).isEqualTo(30);
        assertThat(traits.getTemperament()).isEqualTo(Temperament.AGGRESSIVE);
    }

    @Test
    public void testCreatureFullConstructor() {
        // arrange
        CreatureTraits traits = new CreatureTraits(25, 30, 35, Temperament.TIMID);

        // act
        Creature creature = new Creature(
                "ConstructorTest",
                "TestingSpecies",
                CreatureType.DRAGON,
                Rarity.LEGENDARY,
                20,
                100,
                500,
                1000,
                CreatureStatus.RESERVED,
                traits
        );

        // assert
        assertThat(creature.getName()).isEqualTo("ConstructorTest");
        assertThat(creature.getSpecies()).isEqualTo("TestingSpecies");
        assertThat(creature.getType()).isEqualTo(CreatureType.DRAGON);
        assertThat(creature.getRarity()).isEqualTo(Rarity.LEGENDARY);
        assertThat(creature.getLevel()).isEqualTo(20);
        assertThat(creature.getAge()).isEqualTo(100);
        assertThat(creature.getHealth()).isEqualTo(500);
        assertThat(creature.getExperience()).isEqualTo(1000);
        assertThat(creature.getStatus()).isEqualTo(CreatureStatus.RESERVED);
        assertThat(creature.getCreatureTraits()).isEqualTo(traits);
    }
}