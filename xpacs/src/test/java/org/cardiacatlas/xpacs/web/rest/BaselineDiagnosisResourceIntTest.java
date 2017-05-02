package org.cardiacatlas.xpacs.web.rest;

import org.cardiacatlas.xpacs.XpacswebApp;

import org.cardiacatlas.xpacs.domain.BaselineDiagnosis;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.repository.BaselineDiagnosisRepository;
import org.cardiacatlas.xpacs.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the BaselineDiagnosisResource REST controller.
 *
 * @see BaselineDiagnosisResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = XpacswebApp.class)
public class BaselineDiagnosisResourceIntTest {

    private static final LocalDate DEFAULT_DIAGNOSIS_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DIAGNOSIS_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Float DEFAULT_AGE = 0F;
    private static final Float UPDATED_AGE = 1F;

    private static final String DEFAULT_HEIGHT = "AAAAAAAAAA";
    private static final String UPDATED_HEIGHT = "BBBBBBBBBB";

    private static final String DEFAULT_WEIGHT = "AAAAAAAAAA";
    private static final String UPDATED_WEIGHT = "BBBBBBBBBB";

    private static final String DEFAULT_HEART_RATE = "AAAAAAAAAA";
    private static final String UPDATED_HEART_RATE = "BBBBBBBBBB";

    private static final String DEFAULT_DBP = "AAAAAAAAAA";
    private static final String UPDATED_DBP = "BBBBBBBBBB";

    private static final String DEFAULT_SBP = "AAAAAAAAAA";
    private static final String UPDATED_SBP = "BBBBBBBBBB";

    private static final String DEFAULT_HISTORY_OF_ALCOHOL = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_OF_ALCOHOL = "BBBBBBBBBB";

    private static final String DEFAULT_HISTORY_OF_DIABETES = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_OF_DIABETES = "BBBBBBBBBB";

    private static final String DEFAULT_HISTORY_OF_HYPERTENSION = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_OF_HYPERTENSION = "BBBBBBBBBB";

    private static final String DEFAULT_HISTORY_OF_SMOKING = "AAAAAAAAAA";
    private static final String UPDATED_HISTORY_OF_SMOKING = "BBBBBBBBBB";

    @Autowired
    private BaselineDiagnosisRepository baselineDiagnosisRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restBaselineDiagnosisMockMvc;

    private BaselineDiagnosis baselineDiagnosis;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        BaselineDiagnosisResource baselineDiagnosisResource = new BaselineDiagnosisResource(baselineDiagnosisRepository);
        this.restBaselineDiagnosisMockMvc = MockMvcBuilders.standaloneSetup(baselineDiagnosisResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BaselineDiagnosis createEntity(EntityManager em) {
        BaselineDiagnosis baselineDiagnosis = new BaselineDiagnosis()
            .diagnosisDate(DEFAULT_DIAGNOSIS_DATE)
            .age(DEFAULT_AGE)
            .height(DEFAULT_HEIGHT)
            .weight(DEFAULT_WEIGHT)
            .heartRate(DEFAULT_HEART_RATE)
            .dbp(DEFAULT_DBP)
            .sbp(DEFAULT_SBP)
            .historyOfAlcohol(DEFAULT_HISTORY_OF_ALCOHOL)
            .historyOfDiabetes(DEFAULT_HISTORY_OF_DIABETES)
            .historyOfHypertension(DEFAULT_HISTORY_OF_HYPERTENSION)
            .historyOfSmoking(DEFAULT_HISTORY_OF_SMOKING);
        // Add required entity
        PatientInfo patientInfoFK = PatientInfoResourceIntTest.createEntity(em);
        em.persist(patientInfoFK);
        em.flush();
        baselineDiagnosis.setPatientInfoFK(patientInfoFK);
        return baselineDiagnosis;
    }

    @Before
    public void initTest() {
        baselineDiagnosis = createEntity(em);
    }

    @Test
    @Transactional
    public void createBaselineDiagnosis() throws Exception {
        int databaseSizeBeforeCreate = baselineDiagnosisRepository.findAll().size();

        // Create the BaselineDiagnosis
        restBaselineDiagnosisMockMvc.perform(post("/api/baseline-diagnoses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(baselineDiagnosis)))
            .andExpect(status().isCreated());

        // Validate the BaselineDiagnosis in the database
        List<BaselineDiagnosis> baselineDiagnosisList = baselineDiagnosisRepository.findAll();
        assertThat(baselineDiagnosisList).hasSize(databaseSizeBeforeCreate + 1);
        BaselineDiagnosis testBaselineDiagnosis = baselineDiagnosisList.get(baselineDiagnosisList.size() - 1);
        assertThat(testBaselineDiagnosis.getDiagnosisDate()).isEqualTo(DEFAULT_DIAGNOSIS_DATE);
        assertThat(testBaselineDiagnosis.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testBaselineDiagnosis.getHeight()).isEqualTo(DEFAULT_HEIGHT);
        assertThat(testBaselineDiagnosis.getWeight()).isEqualTo(DEFAULT_WEIGHT);
        assertThat(testBaselineDiagnosis.getHeartRate()).isEqualTo(DEFAULT_HEART_RATE);
        assertThat(testBaselineDiagnosis.getDbp()).isEqualTo(DEFAULT_DBP);
        assertThat(testBaselineDiagnosis.getSbp()).isEqualTo(DEFAULT_SBP);
        assertThat(testBaselineDiagnosis.getHistoryOfAlcohol()).isEqualTo(DEFAULT_HISTORY_OF_ALCOHOL);
        assertThat(testBaselineDiagnosis.getHistoryOfDiabetes()).isEqualTo(DEFAULT_HISTORY_OF_DIABETES);
        assertThat(testBaselineDiagnosis.getHistoryOfHypertension()).isEqualTo(DEFAULT_HISTORY_OF_HYPERTENSION);
        assertThat(testBaselineDiagnosis.getHistoryOfSmoking()).isEqualTo(DEFAULT_HISTORY_OF_SMOKING);
    }

    @Test
    @Transactional
    public void createBaselineDiagnosisWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = baselineDiagnosisRepository.findAll().size();

        // Create the BaselineDiagnosis with an existing ID
        baselineDiagnosis.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBaselineDiagnosisMockMvc.perform(post("/api/baseline-diagnoses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(baselineDiagnosis)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<BaselineDiagnosis> baselineDiagnosisList = baselineDiagnosisRepository.findAll();
        assertThat(baselineDiagnosisList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDiagnosisDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = baselineDiagnosisRepository.findAll().size();
        // set the field null
        baselineDiagnosis.setDiagnosisDate(null);

        // Create the BaselineDiagnosis, which fails.

        restBaselineDiagnosisMockMvc.perform(post("/api/baseline-diagnoses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(baselineDiagnosis)))
            .andExpect(status().isBadRequest());

        List<BaselineDiagnosis> baselineDiagnosisList = baselineDiagnosisRepository.findAll();
        assertThat(baselineDiagnosisList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllBaselineDiagnoses() throws Exception {
        // Initialize the database
        baselineDiagnosisRepository.saveAndFlush(baselineDiagnosis);

        // Get all the baselineDiagnosisList
        restBaselineDiagnosisMockMvc.perform(get("/api/baseline-diagnoses?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(baselineDiagnosis.getId().intValue())))
            .andExpect(jsonPath("$.[*].diagnosisDate").value(hasItem(DEFAULT_DIAGNOSIS_DATE.toString())))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE.doubleValue())))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT.toString())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT.toString())))
            .andExpect(jsonPath("$.[*].heartRate").value(hasItem(DEFAULT_HEART_RATE.toString())))
            .andExpect(jsonPath("$.[*].dbp").value(hasItem(DEFAULT_DBP.toString())))
            .andExpect(jsonPath("$.[*].sbp").value(hasItem(DEFAULT_SBP.toString())))
            .andExpect(jsonPath("$.[*].historyOfAlcohol").value(hasItem(DEFAULT_HISTORY_OF_ALCOHOL.toString())))
            .andExpect(jsonPath("$.[*].historyOfDiabetes").value(hasItem(DEFAULT_HISTORY_OF_DIABETES.toString())))
            .andExpect(jsonPath("$.[*].historyOfHypertension").value(hasItem(DEFAULT_HISTORY_OF_HYPERTENSION.toString())))
            .andExpect(jsonPath("$.[*].historyOfSmoking").value(hasItem(DEFAULT_HISTORY_OF_SMOKING.toString())));
    }

    @Test
    @Transactional
    public void getBaselineDiagnosis() throws Exception {
        // Initialize the database
        baselineDiagnosisRepository.saveAndFlush(baselineDiagnosis);

        // Get the baselineDiagnosis
        restBaselineDiagnosisMockMvc.perform(get("/api/baseline-diagnoses/{id}", baselineDiagnosis.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(baselineDiagnosis.getId().intValue()))
            .andExpect(jsonPath("$.diagnosisDate").value(DEFAULT_DIAGNOSIS_DATE.toString()))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE.doubleValue()))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT.toString()))
            .andExpect(jsonPath("$.weight").value(DEFAULT_WEIGHT.toString()))
            .andExpect(jsonPath("$.heartRate").value(DEFAULT_HEART_RATE.toString()))
            .andExpect(jsonPath("$.dbp").value(DEFAULT_DBP.toString()))
            .andExpect(jsonPath("$.sbp").value(DEFAULT_SBP.toString()))
            .andExpect(jsonPath("$.historyOfAlcohol").value(DEFAULT_HISTORY_OF_ALCOHOL.toString()))
            .andExpect(jsonPath("$.historyOfDiabetes").value(DEFAULT_HISTORY_OF_DIABETES.toString()))
            .andExpect(jsonPath("$.historyOfHypertension").value(DEFAULT_HISTORY_OF_HYPERTENSION.toString()))
            .andExpect(jsonPath("$.historyOfSmoking").value(DEFAULT_HISTORY_OF_SMOKING.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingBaselineDiagnosis() throws Exception {
        // Get the baselineDiagnosis
        restBaselineDiagnosisMockMvc.perform(get("/api/baseline-diagnoses/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBaselineDiagnosis() throws Exception {
        // Initialize the database
        baselineDiagnosisRepository.saveAndFlush(baselineDiagnosis);
        int databaseSizeBeforeUpdate = baselineDiagnosisRepository.findAll().size();

        // Update the baselineDiagnosis
        BaselineDiagnosis updatedBaselineDiagnosis = baselineDiagnosisRepository.findOne(baselineDiagnosis.getId());
        updatedBaselineDiagnosis
            .diagnosisDate(UPDATED_DIAGNOSIS_DATE)
            .age(UPDATED_AGE)
            .height(UPDATED_HEIGHT)
            .weight(UPDATED_WEIGHT)
            .heartRate(UPDATED_HEART_RATE)
            .dbp(UPDATED_DBP)
            .sbp(UPDATED_SBP)
            .historyOfAlcohol(UPDATED_HISTORY_OF_ALCOHOL)
            .historyOfDiabetes(UPDATED_HISTORY_OF_DIABETES)
            .historyOfHypertension(UPDATED_HISTORY_OF_HYPERTENSION)
            .historyOfSmoking(UPDATED_HISTORY_OF_SMOKING);

        restBaselineDiagnosisMockMvc.perform(put("/api/baseline-diagnoses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedBaselineDiagnosis)))
            .andExpect(status().isOk());

        // Validate the BaselineDiagnosis in the database
        List<BaselineDiagnosis> baselineDiagnosisList = baselineDiagnosisRepository.findAll();
        assertThat(baselineDiagnosisList).hasSize(databaseSizeBeforeUpdate);
        BaselineDiagnosis testBaselineDiagnosis = baselineDiagnosisList.get(baselineDiagnosisList.size() - 1);
        assertThat(testBaselineDiagnosis.getDiagnosisDate()).isEqualTo(UPDATED_DIAGNOSIS_DATE);
        assertThat(testBaselineDiagnosis.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testBaselineDiagnosis.getHeight()).isEqualTo(UPDATED_HEIGHT);
        assertThat(testBaselineDiagnosis.getWeight()).isEqualTo(UPDATED_WEIGHT);
        assertThat(testBaselineDiagnosis.getHeartRate()).isEqualTo(UPDATED_HEART_RATE);
        assertThat(testBaselineDiagnosis.getDbp()).isEqualTo(UPDATED_DBP);
        assertThat(testBaselineDiagnosis.getSbp()).isEqualTo(UPDATED_SBP);
        assertThat(testBaselineDiagnosis.getHistoryOfAlcohol()).isEqualTo(UPDATED_HISTORY_OF_ALCOHOL);
        assertThat(testBaselineDiagnosis.getHistoryOfDiabetes()).isEqualTo(UPDATED_HISTORY_OF_DIABETES);
        assertThat(testBaselineDiagnosis.getHistoryOfHypertension()).isEqualTo(UPDATED_HISTORY_OF_HYPERTENSION);
        assertThat(testBaselineDiagnosis.getHistoryOfSmoking()).isEqualTo(UPDATED_HISTORY_OF_SMOKING);
    }

    @Test
    @Transactional
    public void updateNonExistingBaselineDiagnosis() throws Exception {
        int databaseSizeBeforeUpdate = baselineDiagnosisRepository.findAll().size();

        // Create the BaselineDiagnosis

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restBaselineDiagnosisMockMvc.perform(put("/api/baseline-diagnoses")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(baselineDiagnosis)))
            .andExpect(status().isCreated());

        // Validate the BaselineDiagnosis in the database
        List<BaselineDiagnosis> baselineDiagnosisList = baselineDiagnosisRepository.findAll();
        assertThat(baselineDiagnosisList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteBaselineDiagnosis() throws Exception {
        // Initialize the database
        baselineDiagnosisRepository.saveAndFlush(baselineDiagnosis);
        int databaseSizeBeforeDelete = baselineDiagnosisRepository.findAll().size();

        // Get the baselineDiagnosis
        restBaselineDiagnosisMockMvc.perform(delete("/api/baseline-diagnoses/{id}", baselineDiagnosis.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<BaselineDiagnosis> baselineDiagnosisList = baselineDiagnosisRepository.findAll();
        assertThat(baselineDiagnosisList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(BaselineDiagnosis.class);
    }
}
