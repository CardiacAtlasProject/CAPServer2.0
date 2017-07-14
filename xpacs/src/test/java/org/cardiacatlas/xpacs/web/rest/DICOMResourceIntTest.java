package org.cardiacatlas.xpacs.web.rest;

import org.cardiacatlas.xpacs.XpacswebApp;

import org.cardiacatlas.xpacs.domain.DICOM;
import org.cardiacatlas.xpacs.repository.DICOMRepository;
import org.cardiacatlas.xpacs.service.DICOMService;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DICOMResource REST controller.
 *
 * @see DICOMResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = XpacswebApp.class)
public class DICOMResourceIntTest {

    private static final byte[] DEFAULT_DICOM_FILE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_DICOM_FILE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_DICOM_FILE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_DICOM_FILE_CONTENT_TYPE = "image/png";

    private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
    private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(2, "1");
    private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
    private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

    @Autowired
    private DICOMRepository dICOMRepository;

    @Autowired
    private DICOMService dICOMService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDICOMMockMvc;

    private DICOM dICOM;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        DICOMResource dICOMResource = new DICOMResource(dICOMService);
        this.restDICOMMockMvc = MockMvcBuilders.standaloneSetup(dICOMResource)
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
    public static DICOM createEntity(EntityManager em) {
        DICOM dICOM = new DICOM()
            .dicomFile(DEFAULT_DICOM_FILE)
            .dicomFileContentType(DEFAULT_DICOM_FILE_CONTENT_TYPE)
            .image(DEFAULT_IMAGE)
            .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
        return dICOM;
    }

    @Before
    public void initTest() {
        dICOM = createEntity(em);
    }

    @Test
    @Transactional
    public void createDICOM() throws Exception {
        int databaseSizeBeforeCreate = dICOMRepository.findAll().size();

        // Create the DICOM
        restDICOMMockMvc.perform(post("/api/d-icoms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dICOM)))
            .andExpect(status().isCreated());

        // Validate the DICOM in the database
        List<DICOM> dICOMList = dICOMRepository.findAll();
        assertThat(dICOMList).hasSize(databaseSizeBeforeCreate + 1);
        DICOM testDICOM = dICOMList.get(dICOMList.size() - 1);
        assertThat(testDICOM.getDicomFile()).isEqualTo(DEFAULT_DICOM_FILE);
        assertThat(testDICOM.getDicomFileContentType()).isEqualTo(DEFAULT_DICOM_FILE_CONTENT_TYPE);
        assertThat(testDICOM.getImage()).isEqualTo(DEFAULT_IMAGE);
        assertThat(testDICOM.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void createDICOMWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = dICOMRepository.findAll().size();

        // Create the DICOM with an existing ID
        dICOM.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDICOMMockMvc.perform(post("/api/d-icoms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dICOM)))
            .andExpect(status().isBadRequest());

        // Validate the Alice in the database
        List<DICOM> dICOMList = dICOMRepository.findAll();
        assertThat(dICOMList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkDicomFileIsRequired() throws Exception {
        int databaseSizeBeforeTest = dICOMRepository.findAll().size();
        // set the field null
        dICOM.setDicomFile(null);

        // Create the DICOM, which fails.

        restDICOMMockMvc.perform(post("/api/d-icoms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dICOM)))
            .andExpect(status().isBadRequest());

        List<DICOM> dICOMList = dICOMRepository.findAll();
        assertThat(dICOMList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllDICOMS() throws Exception {
        // Initialize the database
        dICOMRepository.saveAndFlush(dICOM);

        // Get all the dICOMList
        restDICOMMockMvc.perform(get("/api/d-icoms?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(dICOM.getId().intValue())))
            .andExpect(jsonPath("$.[*].dicomFileContentType").value(hasItem(DEFAULT_DICOM_FILE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].dicomFile").value(hasItem(Base64Utils.encodeToString(DEFAULT_DICOM_FILE))))
            .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
            .andExpect(jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
    }

    @Test
    @Transactional
    public void getDICOM() throws Exception {
        // Initialize the database
        dICOMRepository.saveAndFlush(dICOM);

        // Get the dICOM
        restDICOMMockMvc.perform(get("/api/d-icoms/{id}", dICOM.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(dICOM.getId().intValue()))
            .andExpect(jsonPath("$.dicomFileContentType").value(DEFAULT_DICOM_FILE_CONTENT_TYPE))
            .andExpect(jsonPath("$.dicomFile").value(Base64Utils.encodeToString(DEFAULT_DICOM_FILE)))
            .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
            .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
    }

    @Test
    @Transactional
    public void getNonExistingDICOM() throws Exception {
        // Get the dICOM
        restDICOMMockMvc.perform(get("/api/d-icoms/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDICOM() throws Exception {
        // Initialize the database
        dICOMService.save(dICOM);

        int databaseSizeBeforeUpdate = dICOMRepository.findAll().size();

        // Update the dICOM
        DICOM updatedDICOM = dICOMRepository.findOne(dICOM.getId());
        updatedDICOM
            .dicomFile(UPDATED_DICOM_FILE)
            .dicomFileContentType(UPDATED_DICOM_FILE_CONTENT_TYPE)
            .image(UPDATED_IMAGE)
            .imageContentType(UPDATED_IMAGE_CONTENT_TYPE);

        restDICOMMockMvc.perform(put("/api/d-icoms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedDICOM)))
            .andExpect(status().isOk());

        // Validate the DICOM in the database
        List<DICOM> dICOMList = dICOMRepository.findAll();
        assertThat(dICOMList).hasSize(databaseSizeBeforeUpdate);
        DICOM testDICOM = dICOMList.get(dICOMList.size() - 1);
        assertThat(testDICOM.getDicomFile()).isEqualTo(UPDATED_DICOM_FILE);
        assertThat(testDICOM.getDicomFileContentType()).isEqualTo(UPDATED_DICOM_FILE_CONTENT_TYPE);
        assertThat(testDICOM.getImage()).isEqualTo(UPDATED_IMAGE);
        assertThat(testDICOM.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
    }

    @Test
    @Transactional
    public void updateNonExistingDICOM() throws Exception {
        int databaseSizeBeforeUpdate = dICOMRepository.findAll().size();

        // Create the DICOM

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDICOMMockMvc.perform(put("/api/d-icoms")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(dICOM)))
            .andExpect(status().isCreated());

        // Validate the DICOM in the database
        List<DICOM> dICOMList = dICOMRepository.findAll();
        assertThat(dICOMList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDICOM() throws Exception {
        // Initialize the database
        dICOMService.save(dICOM);

        int databaseSizeBeforeDelete = dICOMRepository.findAll().size();

        // Get the dICOM
        restDICOMMockMvc.perform(delete("/api/d-icoms/{id}", dICOM.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<DICOM> dICOMList = dICOMRepository.findAll();
        assertThat(dICOMList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(DICOM.class);
        DICOM dICOM1 = new DICOM();
        dICOM1.setId(1L);
        DICOM dICOM2 = new DICOM();
        dICOM2.setId(dICOM1.getId());
        assertThat(dICOM1).isEqualTo(dICOM2);
        dICOM2.setId(2L);
        assertThat(dICOM1).isNotEqualTo(dICOM2);
        dICOM1.setId(null);
        assertThat(dICOM1).isNotEqualTo(dICOM2);
    }
}
