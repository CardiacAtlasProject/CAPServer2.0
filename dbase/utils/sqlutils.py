import csv
import mysql.connector
import termutils as tu

def read_csv(file):
    with open(file, 'r') as f:
        r = csv.reader(f.read().splitlines())
        header = r.next()
        data = [dict(zip(header,row)) for row in r]
    return data


def get_all_patient_ids(sqlcon):
    # get all existing patient_ids
    cursor = sqlcon.cursor(buffered=True)
    cursor.execute('SELECT patient_id FROM patient_info')
    existing_patients = [r[0] for r in cursor.fetchall()]
    cursor.close()
    return existing_patients


def insert_new_patient_info(sqlcon, data):
    # insert
    add_patient = ("INSERT INTO patient_info "
                   "(patient_id, cohort, ethnicity, gender, primary_diagnosis) "
                   "VALUES (%(patient_id)s, %(cohort)s, %(ethnicity)s, %(gender)s, %(primary_diagnosis)s)")

    try:
        cursor = sqlcon.cursor()
        cursor.execute(add_patient, data)
        q = cursor.statement
        sqlcon.commit()
        cursor.close()
    except mysql.connector.Error as err:
        tu.error(str(err))
        exit()

    return q


def update_patient_info(sqlcon, data):
    # update
    upd_patient = ("UPDATE patient_info "
                   "SET cohort=%(cohort)s, "
                       "ethnicity=%(ethnicity)s, "
                       "gender=%(gender)s, "
                       "primary_diagnosis=%(primary_diagnosis)s "
                   "WHERE patient_id=%(patient_id)s ")

    try:
        cursor = sqlcon.cursor()
        cursor.execute(upd_patient, data)
        q = cursor.statement
        sqlcon.commit()
        cursor.close()
    except mysql.connector.Error as err:
        tu.error(str(err))
        exit()

    return q
