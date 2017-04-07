package org.cardiacatlas.xpacs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A CapModel.
 */
@Entity
@Table(name = "cap_model")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class CapModel implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private LocalDate creationDate;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type")
    private String type;

    @Column(name = "comment")
    private String comment;

    @Lob
    @Column(name = "model_file")
    private byte[] modelFile;

    @Column(name = "model_file_content_type")
    private String modelFileContentType;

    @Lob
    @Column(name = "xml_file")
    private byte[] xmlFile;

    @Column(name = "xml_file_content_type")
    private String xmlFileContentType;

    @ManyToOne(optional = false)
    @NotNull
    private PatientInfo patientInfoFK;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreationDate() {
        return creationDate;
    }

    public CapModel creationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public void setCreationDate(LocalDate creationDate) {
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public CapModel name(String name) {
        this.name = name;
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public CapModel type(String type) {
        this.type = type;
        return this;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getComment() {
        return comment;
    }

    public CapModel comment(String comment) {
        this.comment = comment;
        return this;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public byte[] getModelFile() {
        return modelFile;
    }

    public CapModel modelFile(byte[] modelFile) {
        this.modelFile = modelFile;
        return this;
    }

    public void setModelFile(byte[] modelFile) {
        this.modelFile = modelFile;
    }

    public String getModelFileContentType() {
        return modelFileContentType;
    }

    public CapModel modelFileContentType(String modelFileContentType) {
        this.modelFileContentType = modelFileContentType;
        return this;
    }

    public void setModelFileContentType(String modelFileContentType) {
        this.modelFileContentType = modelFileContentType;
    }

    public byte[] getXmlFile() {
        return xmlFile;
    }

    public CapModel xmlFile(byte[] xmlFile) {
        this.xmlFile = xmlFile;
        return this;
    }

    public void setXmlFile(byte[] xmlFile) {
        this.xmlFile = xmlFile;
    }

    public String getXmlFileContentType() {
        return xmlFileContentType;
    }

    public CapModel xmlFileContentType(String xmlFileContentType) {
        this.xmlFileContentType = xmlFileContentType;
        return this;
    }

    public void setXmlFileContentType(String xmlFileContentType) {
        this.xmlFileContentType = xmlFileContentType;
    }

    public PatientInfo getPatientInfoFK() {
        return patientInfoFK;
    }

    public CapModel patientInfoFK(PatientInfo patientInfo) {
        this.patientInfoFK = patientInfo;
        return this;
    }

    public void setPatientInfoFK(PatientInfo patientInfo) {
        this.patientInfoFK = patientInfo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CapModel capModel = (CapModel) o;
        if (capModel.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, capModel.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "CapModel{" +
            "id=" + id +
            ", creationDate='" + creationDate + "'" +
            ", name='" + name + "'" +
            ", type='" + type + "'" +
            ", comment='" + comment + "'" +
            ", modelFile='" + modelFile + "'" +
            ", modelFileContentType='" + modelFileContentType + "'" +
            ", xmlFile='" + xmlFile + "'" +
            ", xmlFileContentType='" + xmlFileContentType + "'" +
            '}';
    }
}
