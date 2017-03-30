package org.cardiacatlas.xpacs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A AuxFile.
 */
@Entity
@Table(name = "aux_file")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AuxFile implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "creation_date", nullable = false)
    private LocalDate creation_date;

    @Column(name = "description")
    private String description;

    @NotNull
    @Lob
    @Column(name = "file", nullable = false)
    private byte[] file;

    @Column(name = "file_content_type", nullable = false)
    private String fileContentType;

    @ManyToOne(optional = false)
    @NotNull
    private PatientInfo patientInfoFK;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getCreation_date() {
        return creation_date;
    }

    public AuxFile creation_date(LocalDate creation_date) {
        this.creation_date = creation_date;
        return this;
    }

    public void setCreation_date(LocalDate creation_date) {
        this.creation_date = creation_date;
    }

    public String getDescription() {
        return description;
    }

    public AuxFile description(String description) {
        this.description = description;
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public byte[] getFile() {
        return file;
    }

    public AuxFile file(byte[] file) {
        this.file = file;
        return this;
    }

    public void setFile(byte[] file) {
        this.file = file;
    }

    public String getFileContentType() {
        return fileContentType;
    }

    public AuxFile fileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
        return this;
    }

    public void setFileContentType(String fileContentType) {
        this.fileContentType = fileContentType;
    }

    public PatientInfo getPatientInfoFK() {
        return patientInfoFK;
    }

    public AuxFile patientInfoFK(PatientInfo patientInfo) {
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
        AuxFile auxFile = (AuxFile) o;
        if (auxFile.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, auxFile.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "AuxFile{" +
            "id=" + id +
            ", creation_date='" + creation_date + "'" +
            ", description='" + description + "'" +
            ", file='" + file + "'" +
            ", fileContentType='" + fileContentType + "'" +
            '}';
    }
}
