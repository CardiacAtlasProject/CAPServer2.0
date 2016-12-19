/* INITIALIZES USERS */
INSERT INTO USER (username, password, role, enabled)
VALUES
	('capuser', PASSWORD('capuser'), 'USER', 1),
	('capadmin', PASSWORD('capadmin'), 'ADMIN', 1);

/* INITIALIZES PATIENT_INFO */
INSERT INTO PATIENT_INFO (cohort, ethnicity, gender, patient_id, primary_diagnosis)
VALUES
	('CHD', 'Asian', 'F', 'CHD0001', 'Fontan, Coarc'),
	('CHD', 'Caucasian', 'M', 'CHD0005', 'Coarc'),
	('CHD', 'Caucasian', 'M', 'CHD0002', 'Tricuspid Atresia'),
	('PUBLIC', 'Unknown', 'F', 'PUB00100', 'Healthy'),
	('PUBLIC', 'Asian', 'M', 'PUB00008', 'Healthy'),
	('CHD', 'Unknown', 'M', 'CHD00011', 'TOF'),
	('PUBLIC', 'Asian', 'M', 'PUB00001', 'Healthy'),
	('CHD', 'Asian', 'F', 'CHD00012', 'Single ventricle'),
	('CHD', 'Asian', 'F', 'CHD00003', 'TOF, Fontan'),
	('PUBLIC', 'Caucasian', 'F', 'PUB00021', 'Infarction');

/* ADD CLINICAL_NOTE */
