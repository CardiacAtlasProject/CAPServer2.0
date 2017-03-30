package org.cardiacatlas.xpacs.web.rest;

import org.cardiacatlas.xpacs.XpacswebApp;

import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.repository.PatientInfoRepository;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.cardiacatlas.xpacs.domain.enumeration.GenderType;
/**
 * Test class for the PatientInfoResource REST controller.
 *
 * @see PatientInfoResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = XpacswebApp.class)
public class PatientInfoResourceIntTest {

    private static final String DEFAULT_PATIENT_ID = "AAAAAAAAAA";
    private static final String UPDATED_PATIENT_ID = "BBBBBBBBBB";

    private static final String DEFAULT_COHORT = "AAAAAAAAAA";
    private static final String UPDATED_COHORT = "BBBBBBBBBB";

    private static final String DEFAULT_ETHNICITY = "AAAAAAAAAA";
    private static final String UPDATED_ETHNICITY = "BBBBBBBBBB";

    private static final GenderType DEFAULT_GENDER = GenderType.male;
    private static final GenderType UPDATED_GENDER = GenderType.female;

    private static final String DEFAULT_PRIMARY_DIAGNOSIS = "AAAAAAAAAA";
    private static final String UPDATED_PRIMARY_DIAGNOSIS = "BBBBBBBBBB";

    @Autowired
    private PatientInfoRepository patientInfoRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restPatientInfoMockMvc;

    private PatientInfo patientInfo;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        PatientInfoResource patientInfoResource = new PatientInfoResource(patientInfoRepository);
        this.restPatientInfoMockMvc = MockMvcBuilders.standaloneSetup(patientInfoResource)
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
    public static PatientInfo createEntity(EntityManager em) {
        PatientInfo patientInfo = new PatientInfo()
            .patient_id(DEFAULT_PATIENT_ID)
            .cohort(DEFAULT_COHORT)
            .ethnicity(DEFAULT_ETHNICITY)
            .gender(DEFAULT_GENDER)
            .primary_diagnosis(DEFAULT_PRIMARY_DIAGNOSIS);
        return patientInfo;
    }

    @Before
    public void initTest() {
        patientInfo = createEntity(em);
    }

    @Test
    @Transactional
    public void createPatientInfo() throws Exception {
        int databaseSizeBeforeCreate = patientInfoRepository.findAll().size();

        // Create the PatientInfo
        restPatientInfoMockMvc.perform(post("/api/patient-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(patientInfo)))
            .andExpect(status().isCreated());

        // Validate the PatientInfo in the database
        List<PatientInfo> patientInfoList = patientInfoRepository.findAll();
        assertThat(patientInfoList).hasSize(databaseSizeBeforeCreate + 1);
        PatientInfo testPatientInfo = patientInfoList.get(patientInfoList.size() - 1);
        assertThat(testPatientInfo.getPatient_id()).isEqualTo(DEFAULT_PATIENT_ID);
        assertThat(testPatientInfo.getCohort()).isEqualTo(DEFAULT_COHORT);
        assertThat(testPatientInfo.getEthnicity()).isEqualTo(DEFAULT_ETHNICITY);
        assertThat(testPatientInfo.getGender()).isEqualTo(DEFAULT_GENDER);
        assertThat(testPatientInfo.getPrimary_diagnosis()).isEqualTo(DEFAULT_PRIMARY_DIAGNOSIS);
    }

    @Test
    @Transactional
    public void createPatientInfoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = patientInfoRepository.findAll().size();

        // Create the PatientInfo with an existing ID
        patientInfo.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restPatientInfoMockMvc.perform(post("/api/patient-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(patientInfo)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<PatientInfo> patientInfoList = patientInfoRepository.findAll();
        assertThat(patientInfoList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkPatient_idIsRequired() throws Exception {
        int databaseSizeBeforeTest = patientInfoRepository.findAll().size();
        // set the field null
        patientInfo.setPatient_id(null);

        // Create the PatientInfo, which fails.

        restPatientInfoMockMvc.perform(post("/api/patient-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(patientInfo)))
            .andExpect(status().isBadRequest());

        List<PatientInfo> patientInfoList = patientInfoRepository.findAll();
        assertThat(patientInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkGenderIsRequired() throws Exception {
        int databaseSizeBeforeTest = patientInfoRepository.findAll().size();
        // set the field null
        patientInfo.setGender(null);

        // Create the PatientInfo, which fails.

        restPatientInfoMockMvc.perform(post("/api/patient-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(patientInfo)))
            .andExpect(status().isBadRequest());

        List<PatientInfo> patientInfoList = patientInfoRepository.findAll();
        assertThat(patientInfoList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllPatientInfos() throws Exception {
        // Initialize the database
        patientInfoRepository.saveAndFlush(patientInfo);

        // Get all the patientInfoList
        restPatientInfoMockMvc.perform(get("/api/patient-infos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(patientInfo.getId().intValue())))
            .andExpect(jsonPath("$.[*].patient_id").value(hasItem(DEFAULT_PATIENT_ID.toString())))
            .andExpect(jsonPath("$.[*].cohort").value(hasItem(DEFAULT_COHORT.toString())))
            .andExpect(jsonPath("$.[*].ethnicity").value(hasItem(DEFAULT_ETHNICITY.toString())))
            .andExpect(jsonPath("$.[*].gender").value(hasItem(DEFAULT_GENDER.toString())))
            .andExpect(jsonPath("$.[*].primary_diagnosis").value(hasItem(DEFAULT_PRIMARY_DIAGNOSIS.toString())));
    }

    @Test
    @Transactional
    public void getPatientInfo() throws Exception {
        // Initialize the database
        patientInfoRepository.saveAndFlush(patientInfo);

        // Get the patientInfo
        restPatientInfoMockMvc.perform(get("/api/patient-infos/{id}", patientInfo.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(patientInfo.getId().intValue()))
            .andExpect(jsonPath("$.patient_id").value(DEFAULT_PATIENT_ID.toString()))
            .andExpect(jsonPath("$.cohort").value(DEFAULT_COHORT.toString()))
            .andExpect(jsonPath("$.ethnicity").value(DEFAULT_ETHNICITY.toString()))
            .andExpect(jsonPath("$.gender").value(DEFAULT_GENDER.toString()))
            .andExpect(jsonPath("$.primary_diagnosis").value(DEFAULT_PRIMARY_DIAGNOSIS.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingPatientInfo() throws Exception {
        // Get the patientInfo
        restPatientInfoMockMvc.perform(get("/api/patient-infos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updatePatientInfo() throws Exception {
        // Initialize the database
        patientInfoRepository.saveAndFlush(patientInfo);
        int databaseSizeBeforeUpdate = patientInfoRepository.findAll().size();

        // Update the patientInfo
        PatientInfo updatedPatientInfo = patientInfoRepository.findOne(patientInfo.getId());
        updatedPatientInfo
            .patient_id(UPDATED_PATIENT_ID)
            .cohort(UPDATED_COHORT)
            .ethnicity(UPDATED_ETHNICITY)
            .gender(UPDATED_GENDER)
            .primary_diagnosis(UPDATED_PRIMARY_DIAGNOSIS);

        restPatientInfoMockMvc.perform(put("/api/patient-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedPatientInfo)))
            .andExpect(status().isOk());

        // Validate the PatientInfo in the database
        List<PatientInfo> patientInfoList = patientInfoRepository.findAll();
        assertThat(patientInfoList).hasSize(databaseSizeBeforeUpdate);
        PatientInfo testPatientInfo = patientInfoList.get(patientInfoList.size() - 1);
        assertThat(testPatientInfo.getPatient_id()).isEqualTo(UPDATED_PATIENT_ID);
        assertThat(testPatientInfo.getCohort()).isEqualTo(UPDATED_COHORT);
        assertThat(testPatientInfo.getEthnicity()).isEqualTo(UPDATED_ETHNICITY);
        assertThat(testPatientInfo.getGender()).isEqualTo(UPDATED_GENDER);
        assertThat(testPatientInfo.getPrimary_diagnosis()).isEqualTo(UPDATED_PRIMARY_DIAGNOSIS);
    }

    @Test
    @Transactional
    public void updateNonExistingPatientInfo() throws Exception {
        int databaseSizeBeforeUpdate = patientInfoRepository.findAll().size();

        // Create the PatientInfo

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restPatientInfoMockMvc.perform(put("/api/patient-infos")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(patientInfo)))
            .andExpect(status().isCreated());

        // Validate the PatientInfo in the database
        List<PatientInfo> patientInfoList = patientInfoRepository.findAll();
        assertThat(patientInfoList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deletePatientInfo() throws Exception {
        // Initialize the database
        patientInfoRepository.saveAndFlush(patientInfo);
        int databaseSizeBeforeDelete = patientInfoRepository.findAll().size();

        // Get the patientInfo
        restPatientInfoMockMvc.perform(delete("/api/patient-infos/{id}", patientInfo.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<PatientInfo> patientInfoList = patientInfoRepository.findAll();
        assertThat(patientInfoList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(PatientInfo.class);
    }
}
