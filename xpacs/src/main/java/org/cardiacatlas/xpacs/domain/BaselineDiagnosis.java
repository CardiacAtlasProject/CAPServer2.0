package org.cardiacatlas.xpacs.domain;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

/**
 * A BaselineDiagnosis.
 */
@Entity
@Table(name = "baseline_diagnosis")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class BaselineDiagnosis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "diagnosis_date", nullable = false)
    private LocalDate diagnosisDate;

    @DecimalMin(value = "0")
    @Column(name = "age")
    private Float age;

    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "heart_rate")
    private String heartRate;

    @Column(name = "dbp")
    private String dbp;

    @Column(name = "sbp")
    private String sbp;

    @Column(name = "history_of_alcohol")
    private String historyOfAlcohol;

    @Column(name = "history_of_diabetes")
    private String historyOfDiabetes;

    @Column(name = "history_of_hypertension")
    private String historyOfHypertension;

    @Column(name = "history_of_smoking")
    private String historyOfSmoking;

    @ManyToOne(optional = false)
    @NotNull
    private PatientInfo patientInfoFK;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDiagnosisDate() {
        return diagnosisDate;
    }

    public BaselineDiagnosis diagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
        return this;
    }

    public void setDiagnosisDate(LocalDate diagnosisDate) {
        this.diagnosisDate = diagnosisDate;
    }

    public Float getAge() {
        return age;
    }

    public BaselineDiagnosis age(Float age) {
        this.age = age;
        return this;
    }

    public void setAge(Float age) {
        this.age = age;
    }

    public String getHeight() {
        return height;
    }

    public BaselineDiagnosis height(String height) {
        this.height = height;
        return this;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWeight() {
        return weight;
    }

    public BaselineDiagnosis weight(String weight) {
        this.weight = weight;
        return this;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getHeartRate() {
        return heartRate;
    }

    public BaselineDiagnosis heartRate(String heartRate) {
        this.heartRate = heartRate;
        return this;
    }

    public void setHeartRate(String heartRate) {
        this.heartRate = heartRate;
    }

    public String getDbp() {
        return dbp;
    }

    public BaselineDiagnosis dbp(String dbp) {
        this.dbp = dbp;
        return this;
    }

    public void setDbp(String dbp) {
        this.dbp = dbp;
    }

    public String getSbp() {
        return sbp;
    }

    public BaselineDiagnosis sbp(String sbp) {
        this.sbp = sbp;
        return this;
    }

    public void setSbp(String sbp) {
        this.sbp = sbp;
    }

    public String getHistoryOfAlcohol() {
        return historyOfAlcohol;
    }

    public BaselineDiagnosis historyOfAlcohol(String historyOfAlcohol) {
        this.historyOfAlcohol = historyOfAlcohol;
        return this;
    }

    public void setHistoryOfAlcohol(String historyOfAlcohol) {
        this.historyOfAlcohol = historyOfAlcohol;
    }

    public String getHistoryOfDiabetes() {
        return historyOfDiabetes;
    }

    public BaselineDiagnosis historyOfDiabetes(String historyOfDiabetes) {
        this.historyOfDiabetes = historyOfDiabetes;
        return this;
    }

    public void setHistoryOfDiabetes(String historyOfDiabetes) {
        this.historyOfDiabetes = historyOfDiabetes;
    }

    public String getHistoryOfHypertension() {
        return historyOfHypertension;
    }

    public BaselineDiagnosis historyOfHypertension(String historyOfHypertension) {
        this.historyOfHypertension = historyOfHypertension;
        return this;
    }

    public void setHistoryOfHypertension(String historyOfHypertension) {
        this.historyOfHypertension = historyOfHypertension;
    }

    public String getHistoryOfSmoking() {
        return historyOfSmoking;
    }

    public BaselineDiagnosis historyOfSmoking(String historyOfSmoking) {
        this.historyOfSmoking = historyOfSmoking;
        return this;
    }

    public void setHistoryOfSmoking(String historyOfSmoking) {
        this.historyOfSmoking = historyOfSmoking;
    }

    public PatientInfo getPatientInfoFK() {
        return patientInfoFK;
    }

    public BaselineDiagnosis patientInfoFK(PatientInfo patientInfo) {
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
        BaselineDiagnosis baselineDiagnosis = (BaselineDiagnosis) o;
        if (baselineDiagnosis.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), baselineDiagnosis.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "BaselineDiagnosis{" +
            "id=" + getId() +
            ", diagnosisDate='" + getDiagnosisDate() + "'" +
            ", age='" + getAge() + "'" +
            ", height='" + getHeight() + "'" +
            ", weight='" + getWeight() + "'" +
            ", heartRate='" + getHeartRate() + "'" +
            ", dbp='" + getDbp() + "'" +
            ", sbp='" + getSbp() + "'" +
            ", historyOfAlcohol='" + getHistoryOfAlcohol() + "'" +
            ", historyOfDiabetes='" + getHistoryOfDiabetes() + "'" +
            ", historyOfHypertension='" + getHistoryOfHypertension() + "'" +
            ", historyOfSmoking='" + getHistoryOfSmoking() + "'" +
            "}";
    }
}
