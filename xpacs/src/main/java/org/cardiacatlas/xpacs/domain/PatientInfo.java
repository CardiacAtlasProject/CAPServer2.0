package org.cardiacatlas.xpacs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

import org.cardiacatlas.xpacs.domain.enumeration.GenderType;

/**
 * A PatientInfo.
 */
@Entity
@Table(name = "patient_info")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class PatientInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "patient_id", nullable = false)
    private String patient_id;

    @Column(name = "cohort")
    private String cohort;

    @Column(name = "ethnicity")
    private String ethnicity;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private GenderType gender;

    @Size(max = 255)
    @Column(name = "primary_diagnosis", length = 255)
    private String primary_diagnosis;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public PatientInfo patient_id(String patient_id) {
        this.patient_id = patient_id;
        return this;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public String getCohort() {
        return cohort;
    }

    public PatientInfo cohort(String cohort) {
        this.cohort = cohort;
        return this;
    }

    public void setCohort(String cohort) {
        this.cohort = cohort;
    }

    public String getEthnicity() {
        return ethnicity;
    }

    public PatientInfo ethnicity(String ethnicity) {
        this.ethnicity = ethnicity;
        return this;
    }

    public void setEthnicity(String ethnicity) {
        this.ethnicity = ethnicity;
    }

    public GenderType getGender() {
        return gender;
    }

    public PatientInfo gender(GenderType gender) {
        this.gender = gender;
        return this;
    }

    public void setGender(GenderType gender) {
        this.gender = gender;
    }

    public String getPrimary_diagnosis() {
        return primary_diagnosis;
    }

    public PatientInfo primary_diagnosis(String primary_diagnosis) {
        this.primary_diagnosis = primary_diagnosis;
        return this;
    }

    public void setPrimary_diagnosis(String primary_diagnosis) {
        this.primary_diagnosis = primary_diagnosis;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PatientInfo patientInfo = (PatientInfo) o;
        if (patientInfo.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, patientInfo.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "PatientInfo{" +
            "id=" + id +
            ", patient_id='" + patient_id + "'" +
            ", cohort='" + cohort + "'" +
            ", ethnicity='" + ethnicity + "'" +
            ", gender='" + gender + "'" +
            ", primary_diagnosis='" + primary_diagnosis + "'" +
            '}';
    }
}
