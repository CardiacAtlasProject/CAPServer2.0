package org.cardiacatlas.xpacs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DICOM.
 */
@Entity
@Table(name = "dicom")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DICOM implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Lob
    @Column(name = "dicom_file", nullable = false)
    private byte[] dicomFile;

    @Column(name = "dicom_file_content_type", nullable = false)
    private String dicomFileContentType;

    @Lob
    @Column(name = "image")
    private byte[] image;

    @Column(name = "image_content_type")
    private String imageContentType;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getDicomFile() {
        return dicomFile;
    }

    public DICOM dicomFile(byte[] dicomFile) {
        this.dicomFile = dicomFile;
        return this;
    }

    public void setDicomFile(byte[] dicomFile) {
        this.dicomFile = dicomFile;
    }

    public String getDicomFileContentType() {
        return dicomFileContentType;
    }

    public DICOM dicomFileContentType(String dicomFileContentType) {
        this.dicomFileContentType = dicomFileContentType;
        return this;
    }

    public void setDicomFileContentType(String dicomFileContentType) {
        this.dicomFileContentType = dicomFileContentType;
    }

    public byte[] getImage() {
        return image;
    }

    public DICOM image(byte[] image) {
        this.image = image;
        return this;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public DICOM imageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
        return this;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        DICOM dICOM = (DICOM) o;
        if (dICOM.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), dICOM.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DICOM{" +
            "id=" + getId() +
            ", dicomFile='" + getDicomFile() + "'" +
            ", dicomFileContentType='" + dicomFileContentType + "'" +
            ", image='" + getImage() + "'" +
            ", imageContentType='" + imageContentType + "'" +
            "}";
    }
}
