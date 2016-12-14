create table AUX_FILE (id bigint not null auto_increment, Descriptor varchar(255), Filename varchar(255) not null, URI varchar(255) not null, PATIENT_HISTORY_Id bigint, primary key (id))
create table BASELINE_DIAGNOSIS (id bigint not null auto_increment, Age float, DBP varchar(255), HeartRate varchar(255), Height varchar(255), HistoryOfAlcohol varchar(255), HistoryOfDiabetes varchar(255), HistoryOfHypertension varchar(255), HistoryOfSmoking varchar(255), SBP varchar(255), Weight varchar(255), PATIENT_HISTORY_Id bigint, primary key (id))
create table CAP_MODEL (id bigint not null auto_increment, Comment varchar(255), ModelData longblob, Name varchar(255), Type varchar(255), XMLData longblob, PATIENT_HISTORY_Id bigint, primary key (id))
create table CLINICAL_NOTE (id bigint not null auto_increment, Age float, Diagnosis varchar(255), Height varchar(255), Notes varchar(255), Weight varchar(255), PATIENT_HISTORY_Id bigint, primary key (id))
create table PATIENT_HISTORY (id bigint not null auto_increment, EventDate date, PACS_Patient_Id varchar(255) not null, PACS_Study_Iuid varchar(255), primary key (id))
create table PATIENT_INFO (id bigint not null auto_increment, Cohort varchar(255) not null, Ethnicity varchar(255), Gender varchar(255) not null, PACS_Patient_Id varchar(255) not null, PrimaryDiagnosis varchar(255), primary key (id))
create table USER (id bigint not null auto_increment, enabled bit, password varchar(255) not null, role varchar(255) not null, username varchar(255) not null, primary key (id))
alter table PATIENT_INFO add constraint UK_s4xsebfddnaet4xsw3pchasxh unique (PACS_Patient_Id)
alter table AUX_FILE add constraint FK_AUX_FILE_PATIENT_HISTORY_Id foreign key (PATIENT_HISTORY_Id) references PATIENT_HISTORY (id)
alter table BASELINE_DIAGNOSIS add constraint FK_BASELINE_DIAGNOSIS_PATIENT_HISTORY_Id foreign key (PATIENT_HISTORY_Id) references PATIENT_HISTORY (id)
alter table CAP_MODEL add constraint FK_CAP_MODEL_PATIENT_HISTORY_Id foreign key (PATIENT_HISTORY_Id) references PATIENT_HISTORY (id)
alter table CLINICAL_NOTE add constraint FK_CLINICAL_NOTE_PATIENT_HISTORY_Id foreign key (PATIENT_HISTORY_Id) references PATIENT_HISTORY (id)