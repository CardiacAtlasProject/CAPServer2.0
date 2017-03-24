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
 * A BaselineDiagnosis.
 */
@Entity
@Table(name = "baseline_diagnosis")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@Document(indexName = "baselinediagnosis")
public class BaselineDiagnosis implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "diagnosis_date", nullable = false)
    private LocalDate diagnosis_date;

    @DecimalMin(value = "0")
    @Column(name = "age")
    private Float age;

    @Column(name = "height")
    private String height;

    @Column(name = "weight")
    private String weight;

    @Column(name = "heart_rate")
    private String heart_rate;

    @Column(name = "dbp")
    private String dbp;

    @Column(name = "sbp")
    private String sbp;

    @Column(name = "history_of_alcohol")
    private String history_of_alcohol;

    @Column(name = "history_of_diabetes")
    private String history_of_diabetes;

    @Column(name = "history_of_hypertension")
    private String history_of_hypertension;

    @Column(name = "history_of_smoking")
    private String history_of_smoking;

    @ManyToOne(optional = false)
    @NotNull
    private PatientInfo patientInfoFK;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDiagnosis_date() {
        return diagnosis_date;
    }

    public BaselineDiagnosis diagnosis_date(LocalDate diagnosis_date) {
        this.diagnosis_date = diagnosis_date;
        return this;
    }

    public void setDiagnosis_date(LocalDate diagnosis_date) {
        this.diagnosis_date = diagnosis_date;
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

    public String getHeart_rate() {
        return heart_rate;
    }

    public BaselineDiagnosis heart_rate(String heart_rate) {
        this.heart_rate = heart_rate;
        return this;
    }

    public void setHeart_rate(String heart_rate) {
        this.heart_rate = heart_rate;
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

    public String getHistory_of_alcohol() {
        return history_of_alcohol;
    }

    public BaselineDiagnosis history_of_alcohol(String history_of_alcohol) {
        this.history_of_alcohol = history_of_alcohol;
        return this;
    }

    public void setHistory_of_alcohol(String history_of_alcohol) {
        this.history_of_alcohol = history_of_alcohol;
    }

    public String getHistory_of_diabetes() {
        return history_of_diabetes;
    }

    public BaselineDiagnosis history_of_diabetes(String history_of_diabetes) {
        this.history_of_diabetes = history_of_diabetes;
        return this;
    }

    public void setHistory_of_diabetes(String history_of_diabetes) {
        this.history_of_diabetes = history_of_diabetes;
    }

    public String getHistory_of_hypertension() {
        return history_of_hypertension;
    }

    public BaselineDiagnosis history_of_hypertension(String history_of_hypertension) {
        this.history_of_hypertension = history_of_hypertension;
        return this;
    }

    public void setHistory_of_hypertension(String history_of_hypertension) {
        this.history_of_hypertension = history_of_hypertension;
    }

    public String getHistory_of_smoking() {
        return history_of_smoking;
    }

    public BaselineDiagnosis history_of_smoking(String history_of_smoking) {
        this.history_of_smoking = history_of_smoking;
        return this;
    }

    public void setHistory_of_smoking(String history_of_smoking) {
        this.history_of_smoking = history_of_smoking;
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
        if (baselineDiagnosis.id == null || id == null) {
            return false;
        }
        return Objects.equals(id, baselineDiagnosis.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String toString() {
        return "BaselineDiagnosis{" +
            "id=" + id +
            ", diagnosis_date='" + diagnosis_date + "'" +
            ", age='" + age + "'" +
            ", height='" + height + "'" +
            ", weight='" + weight + "'" +
            ", heart_rate='" + heart_rate + "'" +
            ", dbp='" + dbp + "'" +
            ", sbp='" + sbp + "'" +
            ", history_of_alcohol='" + history_of_alcohol + "'" +
            ", history_of_diabetes='" + history_of_diabetes + "'" +
            ", history_of_hypertension='" + history_of_hypertension + "'" +
            ", history_of_smoking='" + history_of_smoking + "'" +
            '}';
    }
}
