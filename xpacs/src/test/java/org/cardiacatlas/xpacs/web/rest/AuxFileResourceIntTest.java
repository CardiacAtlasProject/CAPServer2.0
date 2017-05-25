package org.cardiacatlas.xpacs.web.rest;

import org.cardiacatlas.xpacs.XpacswebApp;

import org.cardiacatlas.xpacs.domain.AuxFile;
import org.cardiacatlas.xpacs.domain.PatientInfo;
import org.cardiacatlas.xpacs.repository.AuxFileRepository;
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
 * Test class for the AuxFileResource REST controller.
 *
 * @see AuxFileResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = XpacswebApp.class)
public class AuxFileResourceIntTest {

    private static final LocalDate DEFAULT_CREATION_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_CREATION_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final byte[] DEFAULT_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_FILE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_FILE_CONTENT_TYPE = "image/png";

    @Autowired
    private AuxFileRepository auxFileRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restAuxFileMockMvc;

    private AuxFile auxFile;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        AuxFileResource auxFileResource = new AuxFileResource(auxFileRepository);
        this.restAuxFileMockMvc = MockMvcBuilders.standaloneSetup(auxFileResource)
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
    public static AuxFile createEntity(EntityManager em) {
        AuxFile auxFile = new AuxFile()
            .creationDate(DEFAULT_CREATION_DATE)
            .description(DEFAULT_DESCRIPTION)
            .file(DEFAULT_FILE)
            .fileContentType(DEFAULT_FILE_CONTENT_TYPE);
        // Add required entity
        PatientInfo patientInfoFK = PatientInfoResourceIntTest.createEntity(em);
        em.persist(patientInfoFK);
        em.flush();
        auxFile.setPatientInfoFK(patientInfoFK);
        return auxFile;
    }

    @Before
    public void initTest() {
        auxFile = createEntity(em);
    }

    @Test
    @Transactional
    public void createAuxFile() throws Exception {
        int databaseSizeBeforeCreate = auxFileRepository.findAll().size();

        // Create the AuxFile
        restAuxFileMockMvc.perform(post("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isCreated());

        // Validate the AuxFile in the database
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeCreate + 1);
        AuxFile testAuxFile = auxFileList.get(auxFileList.size() - 1);
        assertThat(testAuxFile.getCreationDate()).isEqualTo(DEFAULT_CREATION_DATE);
        assertThat(testAuxFile.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
        assertThat(testAuxFile.getFile()).isEqualTo(DEFAULT_FILE);
        assertThat(testAuxFile.getFileContentType()).isEqualTo(DEFAULT_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createAuxFileWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = auxFileRepository.findAll().size();

        // Create the AuxFile with an existing ID
        auxFile.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restAuxFileMockMvc.perform(post("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCreationDateIsRequired() throws Exception {
        int databaseSizeBeforeTest = auxFileRepository.findAll().size();
        // set the field null
        auxFile.setCreationDate(null);

        // Create the AuxFile, which fails.

        restAuxFileMockMvc.perform(post("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isBadRequest());

        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkFileIsRequired() throws Exception {
        int databaseSizeBeforeTest = auxFileRepository.findAll().size();
        // set the field null
        auxFile.setFile(null);

        // Create the AuxFile, which fails.

        restAuxFileMockMvc.perform(post("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isBadRequest());

        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllAuxFiles() throws Exception {
        // Initialize the database
        auxFileRepository.saveAndFlush(auxFile);

        // Get all the auxFileList
        restAuxFileMockMvc.perform(get("/api/aux-files?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(auxFile.getId().intValue())))
            .andExpect(jsonPath("$.[*].creationDate").value(hasItem(DEFAULT_CREATION_DATE.toString())))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
            .andExpect(jsonPath("$.[*].fileContentType").value(hasItem(DEFAULT_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].file").value(hasItem(Base64Utils.encodeToString(DEFAULT_FILE))));
    }

    @Test
    @Transactional
    public void getAuxFile() throws Exception {
        // Initialize the database
        auxFileRepository.saveAndFlush(auxFile);

        // Get the auxFile
        restAuxFileMockMvc.perform(get("/api/aux-files/{id}", auxFile.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(auxFile.getId().intValue()))
            .andExpect(jsonPath("$.creationDate").value(DEFAULT_CREATION_DATE.toString()))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
            .andExpect(jsonPath("$.fileContentType").value(DEFAULT_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.file").value(Base64Utils.encodeToString(DEFAULT_FILE)));
    }

    @Test
    @Transactional
    public void getNonExistingAuxFile() throws Exception {
        // Get the auxFile
        restAuxFileMockMvc.perform(get("/api/aux-files/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateAuxFile() throws Exception {
        // Initialize the database
        auxFileRepository.saveAndFlush(auxFile);
        int databaseSizeBeforeUpdate = auxFileRepository.findAll().size();

        // Update the auxFile
        AuxFile updatedAuxFile = auxFileRepository.findOne(auxFile.getId());
        updatedAuxFile
            .creationDate(UPDATED_CREATION_DATE)
            .description(UPDATED_DESCRIPTION)
            .file(UPDATED_FILE)
            .fileContentType(UPDATED_FILE_CONTENT_TYPE);

        restAuxFileMockMvc.perform(put("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedAuxFile)))
            .andExpect(status().isOk());

        // Validate the AuxFile in the database
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeUpdate);
        AuxFile testAuxFile = auxFileList.get(auxFileList.size() - 1);
        assertThat(testAuxFile.getCreationDate()).isEqualTo(UPDATED_CREATION_DATE);
        assertThat(testAuxFile.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
        assertThat(testAuxFile.getFile()).isEqualTo(UPDATED_FILE);
        assertThat(testAuxFile.getFileContentType()).isEqualTo(UPDATED_FILE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingAuxFile() throws Exception {
        int databaseSizeBeforeUpdate = auxFileRepository.findAll().size();

        // Create the AuxFile

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restAuxFileMockMvc.perform(put("/api/aux-files")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(auxFile)))
            .andExpect(status().isCreated());

        // Validate the AuxFile in the database
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteAuxFile() throws Exception {
        // Initialize the database
        auxFileRepository.saveAndFlush(auxFile);
        int databaseSizeBeforeDelete = auxFileRepository.findAll().size();

        // Get the auxFile
        restAuxFileMockMvc.perform(delete("/api/aux-files/{id}", auxFile.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<AuxFile> auxFileList = auxFileRepository.findAll();
        assertThat(auxFileList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(AuxFile.class);
        AuxFile auxFile1 = new AuxFile();
        auxFile1.setId(1L);
        AuxFile auxFile2 = new AuxFile();
        auxFile2.setId(auxFile1.getId());
        assertThat(auxFile1).isEqualTo(auxFile2);
        auxFile2.setId(2L);
        assertThat(auxFile1).isNotEqualTo(auxFile2);
        auxFile1.setId(null);
        assertThat(auxFile1).isNotEqualTo(auxFile2);
    }
}
