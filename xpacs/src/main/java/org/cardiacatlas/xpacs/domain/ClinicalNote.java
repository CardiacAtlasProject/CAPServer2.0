package org.cardiacatlas.xpacs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.springframework.data.elasticsearch.annotations.Document;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A ClinicalNote.
 */
@Entity
@Table(name = "clinical_note")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "clinicalnote")
public class ClinicalNote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessment_date;

    @DecimalMin(value = "0")
    @Column(name = "age")
    private Float age;

    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "diagnosis")
    private String diagnosis;

    @Lob
    @Column(name = "note")
    private String note;

    @ManyToOne(optional = false)
    @NotNull
    private PatientInfo patientInfoFK;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getAssessment_date() {
        return assessment_date;
    }

    public ClinicalNote assessment_date(LocalDate assessment_date) {
        this.assessment_date = assessment_date;
        return this;
    }

    public void setAssessment_date(LocalDate assessment_date) {
        this.assessment_date = assessment_date;
    }

    public Float getAge() {
        return age;
    }

    public ClinicalNote age(Float age) {
        this.age = age;
        return this;
    }

    public void setAge(Float age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public ClinicalNote height(String height) {
        this.height = height;
        return this;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public ClinicalNote weight(String weight) {
        this.weight = weight;
        return this;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public ClinicalNote diagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
        return this;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getNote() {
        return note;
    }

    public ClinicalNote note(String note) {
        this.note = note;
        return this;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public PatientInfo getPatientInfoFK() {
        return patientInfoFK;
    }

    public ClinicalNote patientInfoFK(PatientInfo patientInfo) {
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
        ClinicalNote clinicalNote = (ClinicalNote) o;
        if (clinicalNote.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, clinicalNote.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "ClinicalNote{" +
            "id=" + id +
            ", assessment_date='" + assessment_date + "'" +
            ", age='" + age + "'" +
            ", height='" + height + "'" +
            ", weight='" + weight + "'" +
            ", diagnosis='" + diagnosis + "'" +
            ", note='" + note + "'" +
            '}';
    }
}
