package org.cardiacatlas.xpacs.web.rest;

import org.cardiacatlas.xpacs.XpacswebApp;

import org.cardiacatlas.xpacs.domain.CapModel;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.repository.CapModelRepository;
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
 * Test class for the CapModelResource REST controller.
 *
 * @see CapModelResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = XpacswebApp.class)
public class CapModelResourceIntTest {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_TYPE = "AAAAAAAAAA";
    private static final String UPDATED_TYPE = "BBBBBBBBBB";

    private static final String DEFAULT_COMMENT = "AAAAAAAAAA";
    private static final String UPDATED_COMMENT = "BBBBBBBBBB";

    private static final byte[] DEFAULT_MODEL_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_MODEL_FILE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_MODEL_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_MODEL_FILE_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_XML_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_XML_FILE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_XML_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_XML_FILE_CONTENT_TYPE = "image/png";

    @Autowired
    private CapModelRepository capModelRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restCapModelMockMvc;

    private CapModel capModel;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        CapModelResource capModelResource = new CapModelResource(capModelRepository);
        this.restCapModelMockMvc = MockMvcBuilders.standaloneSetup(capModelResource)
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
    public static CapModel createEntity(EntityManager em) {
        CapModel capModel = new CapModel()
            .creationDate(DEFAULT_CREATION_DATE)
            .name(DEFAULT_NAME)
            .type(DEFAULT_TYPE)
            .comment(DEFAULT_COMMENT)
            .modelFile(DEFAULT_MODEL_FILE)
            .modelFileContentType(DEFAULT_MODEL_FILE_CONTENT_TYPE)
            .xmlFile(DEFAULT_XML_FILE)
            .xmlFileContentType(DEFAULT_XML_FILE_CONTENT_TYPE);
        // Add required entity
        PatientInfo patientInfoFK = PatientInfoResourceIntTest.createEntity(em);
        em.persist(patientInfoFK);
        em.flush();
        capModel.setPatientInfoFK(patientInfoFK);
        return capModel;
    }

    @Before
    public void initTest() {
        capModel = createEntity(em);
    }

    @Test
    @Transactional
    public void createCapModel() throws Exception {
        int databaseSizeBeforeCreate = capModelRepository.findAll().size();

        // Create the CapModel
        restCapModelMockMvc.perform(post("/api/cap-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capModel)))
            .andExpect(status().isCreated());

        // Validate the CapModel in the database
        List<CapModel> capModelList = capModelRepository.findAll();
        assertThat(capModelList).hasSize(databaseSizeBeforeCreate + 1);
        CapModel testCapModel = capModelList.get(capModelList.size() - 1);
        assertThat(testCapModel.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testCapModel.getName()).isEqualTo(DEFAULT_NAME);
        assertThat(testCapModel.getType()).isEqualTo(DEFAULT_TYPE);
        assertThat(testCapModel.getComment()).isEqualTo(DEFAULT_COMMENT);
        assertThat(testCapModel.getModelFile()).isEqualTo(DEFAULT_MODEL_FILE);
        assertThat(testCapModel.getModelFileContentType()).isEqualTo(DEFAULT_MODEL_FILE_CONTENT_TYPE);
        assertThat(testCapModel.getXmlFile()).isEqualTo(DEFAULT_XML_FILE);
        assertThat(testCapModel.getXmlFileContentType()).isEqualTo(DEFAULT_XML_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createCapModelWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = capModelRepository.findAll().size();

        // Create the CapModel with an existing ID
        capModel.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restCapModelMockMvc.perform(post("/api/cap-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capModel)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<CapModel> capModelList = capModelRepository.findAll();
        assertThat(capModelList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = capModelRepository.findAll().size();
        // set the field null
        capModel.setCreationDate(null);

        // Create the CapModel, which fails.

        restCapModelMockMvc.perform(post("/api/cap-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capModel)))
            .andExpect(status().isBadRequest());

        List<CapModel> capModelList = capModelRepository.findAll();
        assertThat(capModelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = capModelRepository.findAll().size();
        // set the field null
        capModel.setName(null);

        // Create the CapModel, which fails.

        restCapModelMockMvc.perform(post("/api/cap-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capModel)))
            .andExpect(status().isBadRequest());

        List<CapModel> capModelList = capModelRepository.findAll();
        assertThat(capModelList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllCapModels() throws Exception {
        // Initialize the database
        capModelRepository.saveAndFlush(capModel);

        // Get all the capModelList
        restCapModelMockMvc.perform(get("/api/cap-models?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(capModel.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())))
            .andExpect(jsonPath("$.[*].type").value(hasItem(DEFAULT_TYPE.toString())))
            .andExpect(jsonPath("$.[*].comment").value(hasItem(DEFAULT_COMMENT.toString())))
            .andExpect(jsonPath("$.[*].modelFileContentType").value(hasItem(DEFAULT_MODEL_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].modelFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_MODEL_FILE))))
            .andExpect(jsonPath("$.[*].xmlFileContentType").value(hasItem(DEFAULT_XML_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].xmlFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_XML_FILE))));
    }

    @Test
    @Transactional
    public void getCapModel() throws Exception {
        // Initialize the database
        capModelRepository.saveAndFlush(capModel);

        // Get the capModel
        restCapModelMockMvc.perform(get("/api/cap-models/{id}", capModel.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(capModel.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()))
            .andExpect(jsonPath("$.type").value(DEFAULT_TYPE.toString()))
            .andExpect(jsonPath("$.comment").value(DEFAULT_COMMENT.toString()))
            .andExpect(jsonPath("$.modelFileContentType").value(DEFAULT_MODEL_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.modelFile").value(Base64Utils.encodeToString(DEFAULT_MODEL_FILE)))
            .andExpect(jsonPath("$.xmlFileContentType").value(DEFAULT_XML_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.xmlFile").value(Base64Utils.encodeToString(DEFAULT_XML_FILE)));
    }

    @Test
    @Transactional
    public void getNonExistingCapModel() throws Exception {
        // Get the capModel
        restCapModelMockMvc.perform(get("/api/cap-models/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateCapModel() throws Exception {
        // Initialize the database
        capModelRepository.saveAndFlush(capModel);
        int databaseSizeBeforeUpdate = capModelRepository.findAll().size();

        // Update the capModel
        CapModel updatedCapModel = capModelRepository.findOne(capModel.getId());
        updatedCapModel
            .creationDate(UPDATED_CREATION_DATE)
            .name(UPDATED_NAME)
            .type(UPDATED_TYPE)
            .comment(UPDATED_COMMENT)
            .modelFile(UPDATED_MODEL_FILE)
            .modelFileContentType(UPDATED_MODEL_FILE_CONTENT_TYPE)
            .xmlFile(UPDATED_XML_FILE)
            .xmlFileContentType(UPDATED_XML_FILE_CONTENT_TYPE);

        restCapModelMockMvc.perform(put("/api/cap-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedCapModel)))
            .andExpect(status().isOk());

        // Validate the CapModel in the database
        List<CapModel> capModelList = capModelRepository.findAll();
        assertThat(capModelList).hasSize(databaseSizeBeforeUpdate);
        CapModel testCapModel = capModelList.get(capModelList.size() - 1);
        assertThat(testCapModel.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testCapModel.getName()).isEqualTo(UPDATED_NAME);
        assertThat(testCapModel.getType()).isEqualTo(UPDATED_TYPE);
        assertThat(testCapModel.getComment()).isEqualTo(UPDATED_COMMENT);
        assertThat(testCapModel.getModelFile()).isEqualTo(UPDATED_MODEL_FILE);
        assertThat(testCapModel.getModelFileContentType()).isEqualTo(UPDATED_MODEL_FILE_CONTENT_TYPE);
        assertThat(testCapModel.getXmlFile()).isEqualTo(UPDATED_XML_FILE);
        assertThat(testCapModel.getXmlFileContentType()).isEqualTo(UPDATED_XML_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingCapModel() throws Exception {
        int databaseSizeBeforeUpdate = capModelRepository.findAll().size();

        // Create the CapModel

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restCapModelMockMvc.perform(put("/api/cap-models")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(capModel)))
            .andExpect(status().isCreated());

        // Validate the CapModel in the database
        List<CapModel> capModelList = capModelRepository.findAll();
        assertThat(capModelList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteCapModel() throws Exception {
        // Initialize the database
        capModelRepository.saveAndFlush(capModel);
        int databaseSizeBeforeDelete = capModelRepository.findAll().size();

        // Get the capModel
        restCapModelMockMvc.perform(delete("/api/cap-models/{id}", capModel.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<CapModel> capModelList = capModelRepository.findAll();
        assertThat(capModelList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(CapModel.class);
        CapModel capModel1 = new CapModel();
        capModel1.setId(1L);
        CapModel capModel2 = new CapModel();
        capModel2.setId(capModel1.getId());
        assertThat(capModel1).isEqualTo(capModel2);
        capModel2.setId(2L);
        assertThat(capModel1).isNotEqualTo(capModel2);
        capModel1.setId(null);
        assertThat(capModel1).isNotEqualTo(capModel2);
    }
}
