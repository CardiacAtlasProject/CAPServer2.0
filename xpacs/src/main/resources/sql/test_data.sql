/* INITIALIZES USERS */
/* DONT FORGET THE MYSQL PASSWORD POLICY !!! */
INSERT INTO USER (username, password, role, enabled)
VALUES
	('capuser', PASSWORD('XPACSc@pus3r'), 'USER', 1),
	('capadmin', PASSWORD('XPACSc@padm1n'), 'ADMIN', 1);

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
INSERT INTO CLINICAL_NOTE (assessment_date, age, notes, diagnosis, height, weight, patient_id)
VALUES
	('2007-07-11', 30.5, 'first scan', NULL, 101.2, 48.0, (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD0005')),
	('2002-09-10', NULL, 'routine follow-up check', NULL, NULL, NULL, (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD00012')),
	('2008-02-23', 30.5, 'second scan', NULL, 101.2, 48.0, (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD0005')),
	('2010-10-06', 20, 'nothing major', 'patient had unexplained symptoms', 98, 77.6, (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='PUB00021')),
	('1998-02-08', 19, 'recruitment', 'this is initial setup', 98.0, 55, (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='PUB00021')),
	('2011-06-20', 30.5, 'follow-up', NULL, NULL, NULL, (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD00011'));

/* ADD AUX_FILE */
INSERT INTO AUX_FILE (created_date, filename, uri, description,  patient_id)
VALUES
	('2015-10-20', 'report1.pdf', '/2015/10/', 'clinician report of a patient', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD0005')),
	('2015-10-21', 'report2.pdf', '/2015/10/', 'clinician report of the same patient', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD0005')),
	('2011-09-11', 'somesheet.csv', '/2011/09/', 'diagnostic table', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD0002')),
	('2014-09-10', 'snapshot.jpg', '/2014/09/', 'image snapshots', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='PUB00021'));

/* ADD CAP_MODEL */
INSERT INTO CAP_MODEL (created_date, name, patient_id)
VALUES
	('2001-11-09', 'cap_chd0001_ao', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD0001')),
	('2001-11-10', 'cap_chd0005_ao', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD0005')),
	('2001-07-11', 'cap_chd0002_ao', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD0002')),
	('2011-11-02', 'cap_pub00100_ao', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='PUB00100')),
	('2001-10-01', 'cap_pub00008_ao', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='PUB00008')),
	('2013-06-25', 'cap_chd00011_ao', (SELECT patient_id FROM PATIENT_INFO WHERE patient_id='CHD00011'));

/* BASELINE_DIAGNOSIS IS INITIALIZED AS EMPTY */
