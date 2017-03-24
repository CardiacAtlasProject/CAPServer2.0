package org.cardiacatlas.xpacs.web.rest;

import org.cardiacatlas.xpacs.XpacswebApp;

import org.cardiacatlas.xpacs.domain.ClinicalNote;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.repository.ClinicalNoteRepository;
import org.cardiacatlas.xpacs.repository.search.ClinicalNoteSearchRepository;
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
import org.springframework.util.Base64Utils;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the ClinicalNoteResource REST controller.
 *
 * @see ClinicalNoteResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = XpacswebApp.class)
public class ClinicalNoteResourceIntTest {

    private static final LocalDate DEFAULT_ASSESSMENT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ASSESSMENT_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final Float DEFAULT_AGE = 0F;
    private static final Float UPDATED_AGE = 1F;

    private static final String DEFAULT_HEIGHT = "AAAAAAAAAA";
    private static final String UPDATED_HEIGHT = "BBBBBBBBBB";

    private static final String DEFAULT_WEIGHT = "AAAAAAAAAA";
    private static final String UPDATED_WEIGHT = "BBBBBBBBBB";

    private static final String DEFAULT_DIAGNOSIS = "AAAAAAAAAA";
    private static final String UPDATED_DIAGNOSIS = "BBBBBBBBBB";

    private static final String DEFAULT_NOTE = "AAAAAAAAAA";
    private static final String UPDATED_NOTE = "BBBBBBBBBB";

    @Autowired
    private ClinicalNoteRepository clinicalNoteRepository;

    @Autowired
    private ClinicalNoteSearchRepository clinicalNoteSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restClinicalNoteMockMvc;

    private ClinicalNote clinicalNote;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        ClinicalNoteResource clinicalNoteResource = new ClinicalNoteResource(clinicalNoteRepository, clinicalNoteSearchRepository);
        this.restClinicalNoteMockMvc = MockMvcBuilders.standaloneSetup(clinicalNoteResource)
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
    public static ClinicalNote createEntity(EntityManager em) {
        ClinicalNote clinicalNote = new ClinicalNote()
            .assessment_date(DEFAULT_ASSESSMENT_DATE)
            .age(DEFAULT_AGE)
            .height(DEFAULT_HEIGHT)
            .weight(DEFAULT_WEIGHT)
            .diagnosis(DEFAULT_DIAGNOSIS)
            .note(DEFAULT_NOTE);
        // Add required entity
        PatientInfo patientInfoFK = PatientInfoResourceIntTest.createEntity(em);
        em.persist(patientInfoFK);
        em.flush();
        clinicalNote.setPatientInfoFK(patientInfoFK);
        return clinicalNote;
    }

    @Before
    public void initTest() {
        clinicalNoteSearchRepository.deleteAll();
        clinicalNote = createEntity(em);
    }

    @Test
    @Transactional
    public void createClinicalNote() throws Exception {
        int databaseSizeBeforeCreate = clinicalNoteRepository.findAll().size();

        // Create the ClinicalNote
        restClinicalNoteMockMvc.perform(post("/api/clinical-notes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clinicalNote)))
            .andExpect(status().isCreated());

        // Validate the ClinicalNote in the database
        List<ClinicalNote> clinicalNoteList = clinicalNoteRepository.findAll();
        assertThat(clinicalNoteList).hasSize(databaseSizeBeforeCreate + 1);
        ClinicalNote testClinicalNote = clinicalNoteList.get(clinicalNoteList.size() - 1);
        assertThat(testClinicalNote.getAssessment_date()).isEqualTo(DEFAULT_ASSESSMENT_DATE);
        assertThat(testClinicalNote.getAge()).isEqualTo(DEFAULT_AGE);
        assertThat(testClinicalNote.getHeight()).isEqualTo(DEFAULT_HEIGHT);
        assertThat(testClinicalNote.getWeight()).isEqualTo(DEFAULT_WEIGHT);
        assertThat(testClinicalNote.getDiagnosis()).isEqualTo(DEFAULT_DIAGNOSIS);
        assertThat(testClinicalNote.getNote()).isEqualTo(DEFAULT_NOTE);

        // Validate the ClinicalNote in Elasticsearch
        ClinicalNote clinicalNoteEs = clinicalNoteSearchRepository.findOne(testClinicalNote.getId());
        assertThat(clinicalNoteEs).isEqualToComparingFieldByField(testClinicalNote);
    }

    @Test
    @Transactional
    public void createClinicalNoteWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = clinicalNoteRepository.findAll().size();

        // Create the ClinicalNote with an existing ID
        clinicalNote.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restClinicalNoteMockMvc.perform(post("/api/clinical-notes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clinicalNote)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<ClinicalNote> clinicalNoteList = clinicalNoteRepository.findAll();
        assertThat(clinicalNoteList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkAssessment_dateIsRequired() throws Exception {
        int databaseSizeBeforeTest = clinicalNoteRepository.findAll().size();
        // set the field null
        clinicalNote.setAssessment_date(null);

        // Create the ClinicalNote, which fails.

        restClinicalNoteMockMvc.perform(post("/api/clinical-notes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clinicalNote)))
            .andExpect(status().isBadRequest());

        List<ClinicalNote> clinicalNoteList = clinicalNoteRepository.findAll();
        assertThat(clinicalNoteList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllClinicalNotes() throws Exception {
        // Initialize the database
        clinicalNoteRepository.saveAndFlush(clinicalNote);

        // Get all the clinicalNoteList
        restClinicalNoteMockMvc.perform(get("/api/clinical-notes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clinicalNote.getId().intValue())))
            .andExpect(jsonPath("$.[*].assessment_date").value(hasItem(DEFAULT_ASSESSMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE.doubleValue())))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT.toString())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT.toString())))
            .andExpect(jsonPath("$.[*].diagnosis").value(hasItem(DEFAULT_DIAGNOSIS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE.toString())));
    }

    @Test
    @Transactional
    public void getClinicalNote() throws Exception {
        // Initialize the database
        clinicalNoteRepository.saveAndFlush(clinicalNote);

        // Get the clinicalNote
        restClinicalNoteMockMvc.perform(get("/api/clinical-notes/{id}", clinicalNote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(clinicalNote.getId().intValue()))
            .andExpect(jsonPath("$.assessment_date").value(DEFAULT_ASSESSMENT_DATE.toString()))
            .andExpect(jsonPath("$.age").value(DEFAULT_AGE.doubleValue()))
            .andExpect(jsonPath("$.height").value(DEFAULT_HEIGHT.toString()))
            .andExpect(jsonPath("$.weight").value(DEFAULT_WEIGHT.toString()))
            .andExpect(jsonPath("$.diagnosis").value(DEFAULT_DIAGNOSIS.toString()))
            .andExpect(jsonPath("$.note").value(DEFAULT_NOTE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingClinicalNote() throws Exception {
        // Get the clinicalNote
        restClinicalNoteMockMvc.perform(get("/api/clinical-notes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateClinicalNote() throws Exception {
        // Initialize the database
        clinicalNoteRepository.saveAndFlush(clinicalNote);
        clinicalNoteSearchRepository.save(clinicalNote);
        int databaseSizeBeforeUpdate = clinicalNoteRepository.findAll().size();

        // Update the clinicalNote
        ClinicalNote updatedClinicalNote = clinicalNoteRepository.findOne(clinicalNote.getId());
        updatedClinicalNote
            .assessment_date(UPDATED_ASSESSMENT_DATE)
            .age(UPDATED_AGE)
            .height(UPDATED_HEIGHT)
            .weight(UPDATED_WEIGHT)
            .diagnosis(UPDATED_DIAGNOSIS)
            .note(UPDATED_NOTE);

        restClinicalNoteMockMvc.perform(put("/api/clinical-notes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedClinicalNote)))
            .andExpect(status().isOk());

        // Validate the ClinicalNote in the database
        List<ClinicalNote> clinicalNoteList = clinicalNoteRepository.findAll();
        assertThat(clinicalNoteList).hasSize(databaseSizeBeforeUpdate);
        ClinicalNote testClinicalNote = clinicalNoteList.get(clinicalNoteList.size() - 1);
        assertThat(testClinicalNote.getAssessment_date()).isEqualTo(UPDATED_ASSESSMENT_DATE);
        assertThat(testClinicalNote.getAge()).isEqualTo(UPDATED_AGE);
        assertThat(testClinicalNote.getHeight()).isEqualTo(UPDATED_HEIGHT);
        assertThat(testClinicalNote.getWeight()).isEqualTo(UPDATED_WEIGHT);
        assertThat(testClinicalNote.getDiagnosis()).isEqualTo(UPDATED_DIAGNOSIS);
        assertThat(testClinicalNote.getNote()).isEqualTo(UPDATED_NOTE);

        // Validate the ClinicalNote in Elasticsearch
        ClinicalNote clinicalNoteEs = clinicalNoteSearchRepository.findOne(testClinicalNote.getId());
        assertThat(clinicalNoteEs).isEqualToComparingFieldByField(testClinicalNote);
    }

    @Test
    @Transactional
    public void updateNonExistingClinicalNote() throws Exception {
        int databaseSizeBeforeUpdate = clinicalNoteRepository.findAll().size();

        // Create the ClinicalNote

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restClinicalNoteMockMvc.perform(put("/api/clinical-notes")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(clinicalNote)))
            .andExpect(status().isCreated());

        // Validate the ClinicalNote in the database
        List<ClinicalNote> clinicalNoteList = clinicalNoteRepository.findAll();
        assertThat(clinicalNoteList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteClinicalNote() throws Exception {
        // Initialize the database
        clinicalNoteRepository.saveAndFlush(clinicalNote);
        clinicalNoteSearchRepository.save(clinicalNote);
        int databaseSizeBeforeDelete = clinicalNoteRepository.findAll().size();

        // Get the clinicalNote
        restClinicalNoteMockMvc.perform(delete("/api/clinical-notes/{id}", clinicalNote.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean clinicalNoteExistsInEs = clinicalNoteSearchRepository.exists(clinicalNote.getId());
        assertThat(clinicalNoteExistsInEs).isFalse();

        // Validate the database is empty
        List<ClinicalNote> clinicalNoteList = clinicalNoteRepository.findAll();
        assertThat(clinicalNoteList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchClinicalNote() throws Exception {
        // Initialize the database
        clinicalNoteRepository.saveAndFlush(clinicalNote);
        clinicalNoteSearchRepository.save(clinicalNote);

        // Search the clinicalNote
        restClinicalNoteMockMvc.perform(get("/api/_search/clinical-notes?query=id:" + clinicalNote.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(clinicalNote.getId().intValue())))
            .andExpect(jsonPath("$.[*].assessment_date").value(hasItem(DEFAULT_ASSESSMENT_DATE.toString())))
            .andExpect(jsonPath("$.[*].age").value(hasItem(DEFAULT_AGE.doubleValue())))
            .andExpect(jsonPath("$.[*].height").value(hasItem(DEFAULT_HEIGHT.toString())))
            .andExpect(jsonPath("$.[*].weight").value(hasItem(DEFAULT_WEIGHT.toString())))
            .andExpect(jsonPath("$.[*].diagnosis").value(hasItem(DEFAULT_DIAGNOSIS.toString())))
            .andExpect(jsonPath("$.[*].note").value(hasItem(DEFAULT_NOTE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ClinicalNote.class);
    }
}
