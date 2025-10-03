package com.creatureadoption.trainings.dataaccesslayer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

@DataJpaTest
@Transactional(propagation = NOT_SUPPORTED)
public class TrainingRepositoryIntegrationTest {

    @Autowired
    private TrainingRepository repository;

    private Training savedEntity;
    private TrainingIdentifier identifier;

    @BeforeEach
    public void setupDb() {
        repository.deleteAll();

        //so i dont make data in every thing
        identifier = new TrainingIdentifier();

        Training entity = new Training(
                "Basic Spell Training",
                "Learn the fundamentals of magical spells",
                Difficulty.BEGINNER,
                30,
                TrainingStatus.ACTIVE,
                TrainingCategory.ATTACK,
                99.99,
                "Magic Academy"
        );
        entity.setTrainingIdentifier(identifier);

        savedEntity = repository.save(entity);

        assertThat(savedEntity).isNotNull();
        assertThat(savedEntity.getId()).isNotNull();
    }

    @Test
    public void whenValidTrainingId_thenTrainingShouldBeFound() {
        //arrange
        String trainingId = savedEntity.getTrainingIdentifier().getTrainingId();

        //act
        Training found = repository.findByTrainingIdentifier_TrainingId(trainingId);

        //assert
        assertThat(found).isNotNull();
        assertThat(found.getTrainingIdentifier().getTrainingId()).isEqualTo(trainingId);
        assertThat(found.getName()).isEqualTo(savedEntity.getName());
    }

    @Test
    public void whenInvalidTrainingId_thenTrainingShouldNotBeFound() {
        //arrange
        String invalidId = "invalidId";

        //act
        Training found = repository.findByTrainingIdentifier_TrainingId(invalidId);

        //assert
        assertThat(found).isNull();
    }

    @Test
    public void whenSaveNewTraining_thenItShouldBePersisted() {
        //arrange
        TrainingIdentifier newIdentifier = new TrainingIdentifier();

        Training newEntity = new Training(
                "Advanced Defense",
                "Master defensive magical techniques",
                Difficulty.ADVANCED,
                60,
                TrainingStatus.ACTIVE,
                TrainingCategory.DEFENSE,
                199.99,
                "Advanced Academy"
        );
        newEntity.setTrainingIdentifier(newIdentifier);

        //act
        Training saved = repository.save(newEntity);

        //assert
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getName()).isEqualTo("Advanced Defense");

        //see if the created data can be retrieved
        Training retrieved = repository.findByTrainingIdentifier_TrainingId(newIdentifier.getTrainingId());
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getTrainingIdentifier().getTrainingId()).isEqualTo(newIdentifier.getTrainingId());
    }

    @Test
    public void whenDeleteTraining_thenItShouldBeRemoved() {
        //arrange
        String trainingId = savedEntity.getTrainingIdentifier().getTrainingId();

        //act
        repository.delete(savedEntity);

        //assert
        Training found = repository.findByTrainingIdentifier_TrainingId(trainingId);
        assertThat(found).isNull();
    }

    @Test
    public void whenFindAll_thenAllTrainingsShouldBeReturned() {
        //arrange
        TrainingIdentifier newIdentifier = new TrainingIdentifier();

        Training newEntity = new Training(
                "Advanced Defense",
                "Master defensive magical techniques",
                Difficulty.ADVANCED,
                60,
                TrainingStatus.ACTIVE,
                TrainingCategory.DEFENSE,
                199.99,
                "Advanced Academy"
        );
        newEntity.setTrainingIdentifier(newIdentifier);
        repository.save(newEntity);

        //act
        List<Training> trainings = repository.findAll();

        //assert
        assertThat(trainings).isNotNull();
        assertThat(trainings.size()).isEqualTo(2);
    }

    @Test
    public void whenUpdateTraining_thenChangesAreSaved() {
        // arrange
        String trainingId = savedEntity.getTrainingIdentifier().getTrainingId();
        Training toUpdate = repository.findByTrainingIdentifier_TrainingId(trainingId);
        toUpdate.setName("Updated Training Name");
        toUpdate.setDescription("Updated Description");
        toUpdate.setDifficulty(Difficulty.INTERMEDIATE);
        toUpdate.setPrice(149.99);

        // act
        Training updated = repository.save(toUpdate);

        // assert
        assertThat(updated).isNotNull();
        assertThat(updated.getName()).isEqualTo("Updated Training Name");
        assertThat(updated.getDescription()).isEqualTo("Updated Description");
        assertThat(updated.getDifficulty()).isEqualTo(Difficulty.INTERMEDIATE);
        assertThat(updated.getPrice()).isEqualTo(149.99);

        // verify from fresh query
        Training retrieved = repository.findByTrainingIdentifier_TrainingId(trainingId);
        assertThat(retrieved.getName()).isEqualTo("Updated Training Name");
        assertThat(retrieved.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    public void testTrainingIdentifierCreation() {
        // arrange & act
        TrainingIdentifier identifier1 = new TrainingIdentifier();
        TrainingIdentifier identifier2 = new TrainingIdentifier();

        // assert
        assertThat(identifier1.getTrainingId()).isNotNull();
        assertThat(identifier1.getTrainingCode()).isNotNull();
        assertThat(identifier1.getTrainingCode()).startsWith("TRN-");

        // verify uniqueness
        assertThat(identifier1.getTrainingId()).isNotEqualTo(identifier2.getTrainingId());
        assertThat(identifier1.getTrainingCode()).isNotEqualTo(identifier2.getTrainingCode());
    }

    @Test
    public void testTrainingIdentifierWithParams() {
        // arrange
        String customId = "custom-id";
        String customCode = "CUSTOM-CODE";

        // act
        TrainingIdentifier identifier = new TrainingIdentifier(customId, customCode);

        // assert
        assertThat(identifier.getTrainingId()).isEqualTo(customId);
        assertThat(identifier.getTrainingCode()).isEqualTo(customCode);
    }

    @Test
    public void testTrainingFullConstructor() {
        // arrange & act
        Training training = new Training(
                "Test Training",
                "Test Description",
                Difficulty.BEGINNER,
                45,
                TrainingStatus.ACTIVE,
                TrainingCategory.SPECIAL,
                149.99,
                "Test Location"
        );

        // assert
        assertThat(training.getName()).isEqualTo("Test Training");
        assertThat(training.getDescription()).isEqualTo("Test Description");
        assertThat(training.getDifficulty()).isEqualTo(Difficulty.BEGINNER);
        assertThat(training.getDuration()).isEqualTo(45);
        assertThat(training.getStatus()).isEqualTo(TrainingStatus.ACTIVE);
        assertThat(training.getCategory()).isEqualTo(TrainingCategory.SPECIAL);
        assertThat(training.getPrice()).isEqualTo(149.99);
        assertThat(training.getLocation()).isEqualTo("Test Location");
        assertThat(training.getTrainingIdentifier()).isNotNull();
    }

    @Test
    public void testTrainingNoArgsConstructor() {
        // act
        Training training = new Training();

        // assert
        assertThat(training).isNotNull();
    }

    @Test
    public void testEnumCoverage() {
        // Test all enum values to ensure coverage

        // Difficulty
        assertThat(Difficulty.BEGINNER).isNotNull();
        assertThat(Difficulty.INTERMEDIATE).isNotNull();
        assertThat(Difficulty.ADVANCED).isNotNull();

        // TrainingStatus
        assertThat(TrainingStatus.ACTIVE).isNotNull();
        assertThat(TrainingStatus.INACTIVE).isNotNull();
        assertThat(TrainingStatus.FULL).isNotNull();

        // TrainingCategory
        assertThat(TrainingCategory.ATTACK).isNotNull();
        assertThat(TrainingCategory.DEFENSE).isNotNull();
        assertThat(TrainingCategory.CONTEST).isNotNull();
        assertThat(TrainingCategory.SPECIAL).isNotNull();
    }
}