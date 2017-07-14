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
    private String patientId;

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
    private String primaryDiagnosis;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public PatientInfo patientId(String patientId) {
        this.patientId = patientId;
        return this;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
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

    public String getPrimaryDiagnosis() {
        return primaryDiagnosis;
    }

    public PatientInfo primaryDiagnosis(String primaryDiagnosis) {
        this.primaryDiagnosis = primaryDiagnosis;
        return this;
    }

    public void setPrimaryDiagnosis(String primaryDiagnosis) {
        this.primaryDiagnosis = primaryDiagnosis;
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
        if (patientInfo.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), patientInfo.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "PatientInfo{" +
            "id=" + getId() +
            ", patientId='" + getPatientId() + "'" +
            ", cohort='" + getCohort() + "'" +
            ", ethnicity='" + getEthnicity() + "'" +
            ", gender='" + getGender() + "'" +
            ", primaryDiagnosis='" + getPrimaryDiagnosis() + "'" +
            "}";
    }
}
