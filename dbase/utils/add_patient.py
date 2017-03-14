"""Add a new patient to the xpacsdb.patient_info table

Usage:
    add_patient.py [options]
    add_patient.py -h

Options:
    -h, --help              Show this screen
    -d, --debug             Show some debug information
    -s <host>               The MySQL server host IP address.
    -p <port>               MySQL port. Default is 3306.
    -u <user>               User name.
    --password=<password>   User password.
    --db=<database>         Database name. Default is xpacs.
    -f <csv_file>           Read list of patients from a CSV file (see below)

Batch addition of patients
^^^^^^^^^^^^^^^^^^^^^^^^^^
If -f option is given, then patients are added in from a CSV file in a batch
mode. This file should have the following headers:
'patient_id', 'gender', 'cohort', 'ethnicity', 'primary_diagnosis

If there are existing patient_ids in the database, then the existing rows
will be updated.

Author: Avan Suinesiaputra - University of Auckland (2017)
"""

# Docopt is a library for parsing command line arguments
import docopt
import getpass
import mysql.connector
import termutils as tu
import sqlutils as su


if __name__ == '__main__':

    try:
        # Parse arguments, use file docstring as a parameter definition
        arguments = docopt.docopt(__doc__)

        # Default values
        if not arguments['-s']:
            arguments['-s'] = '127.0.0.1'

        if not arguments['-p']:
            arguments['-p'] = 3306

        if not arguments['--db']:
            arguments['--db'] = 'xpacs'

        # Check user & password
        if not arguments['-u']:
            arguments['-u'] = raw_input('Username: ')

        if arguments['--password'] is None:
            arguments['--password'] = getpass.getpass('Password: ')

        # print arguments for debug
        if arguments['--debug']:
            tu.debug(str(arguments))

    # Handle invalid options
    except docopt.DocoptExit as e:
        tu.error(e.message)
        exit()

# connecting
print 'Connecting to mysql://' + arguments['-s'] + ':' + str(arguments['-p']) + ' ...'

try:
    cnx = mysql.connector.connect(user=arguments['-u'],
                                  host=arguments['-s'],
                                  port=arguments['-p'],
                                  password=arguments['--password'],
                                  database=arguments['--db'])

except mysql.connector.Error as err:
    print(err)
    exit()

existing_patients = su.get_all_patient_ids(cnx)


# it's either by CSV file or interactive
if arguments['-f'] is None:

    # First question: who is the patient?
    patientID = raw_input('Patient ID: ')
    if patientID in existing_patients:
        tu.error("Patient " + patientID + " already exists.")
        exit()

    # Remaining questions
    cohort = raw_input('Cohort [press <enter> to skip]: ')
    ethnicity = raw_input('Ethnicity [press <enter> to skip]: ')
    gender = raw_input('Cohort [M/F/U=unknown (default)]: ')
    if str.lower(gender) == 'f':
        gender = 'female'
    elif str.lower(gender) == 'm':
        gender = 'male'
    else:
        gender = 'unknown'
    primary_diagnosis = raw_input('Primary diagnosis [press <enter> to skip]: ')

    query = su.insert_new_patient_info(cnx, {
        'patient_id': patientID,
        'cohort': cohort,
        'ethnicity': ethnicity,
        'gender': gender,
        'primary_diagnosis': primary_diagnosis
    })

    if arguments['--debug']:
        tu.debug(query)
    tu.ok("Patient " + patientID + " added to the database")

    # don't forget to close the connection
    cnx.close()

else:

    try:
        for row in su.read_csv(arguments['-f']):
            # fix gender
            g = str.lower(row['gender'])
            if g == 'male' or g == 'm':
                row['gender'] = 'male'
            elif g == 'female' or g == 'f':
                row['gender'] = 'female'
            else:
                row['gender'] = 'unknown'

            # update or insert
            if row['patient_id'] in existing_patients:
                if arguments['--debug']:
                    tu.warn('Updating ' + row['patient_id'])
                query = su.update_patient_info(cnx, row)

            else:
                if arguments['--debug']:
                    tu.debug('Inserting ' + row['patient_id'])
                query = su.insert_new_patient_info(cnx, row)

            if arguments['--debug']:
                print query


    except Exception, e:
        tu.error(str(e))
        exit()

    tu.ok("SUCCESS")
