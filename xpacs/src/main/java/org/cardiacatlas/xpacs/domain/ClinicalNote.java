package org.cardiacatlas.xpacs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

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
public class ClinicalNote implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "assessment_date", nullable = false)
    private LocalDate assessmentDate;

    @DecimalMin(value = "0")
    @Column(name = "age")
    private Float age;

    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Lob
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

    public LocalDate getAssessmentDate() {
        return assessmentDate;
    }

    public ClinicalNote assessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
        return this;
    }

    public void setAssessmentDate(LocalDate assessmentDate) {
        this.assessmentDate = assessmentDate;
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
        if (clinicalNote.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), clinicalNote.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "ClinicalNote{" +
            "id=" + getId() +
            ", assessmentDate='" + getAssessmentDate() + "'" +
            ", age='" + getAge() + "'" +
            ", height='" + getHeight() + "'" +
            ", weight='" + getWeight() + "'" +
            ", diagnosis='" + getDiagnosis() + "'" +
            ", note='" + getNote() + "'" +
            "}";
    }
}
