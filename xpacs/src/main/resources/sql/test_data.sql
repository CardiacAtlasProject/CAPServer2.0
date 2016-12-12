/* INITIALIZES USERS */
INSERT INTO USER (username, password, role, enabled)
VALUES ('capuser', PASSWORD('capuser'), 'USER', 1), ('capadmin', PASSWORD('capadmin'), 'ADMIN', 1);

/* INITIALIZES PATIENT_INFO */
INSERT INTO PATIENT_INFO(PACS_Patient_Id,Gender,Ethnicity,Cohort,PrimaryDiagnosis) 
VALUES ( 'PAT0001','F','Asian','CHD','Fontan, Coarc'),
	   ( 'PAT0005','M','Caucasian','CHD','Coarc'),
	   ( 'PAT0002','M','Caucasian','CHD','Tricuspid Atresia');
