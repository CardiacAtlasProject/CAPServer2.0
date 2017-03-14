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

Author: Avan Suinesiaputra - University of Auckland (2017)
"""

# Docopt is a library for parsing command line arguments
import docopt
import getpass
import mysql.connector
import termutils as tu

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

# First question: who is the patient?
patientID = raw_input('Patient ID: ')

# This should be unique. Have to check it first.
query = "SELECT * FROM patient_info WHERE patient_id = '" + patientID + "'"
if arguments['--debug']:
    tu.debug(query)

cursor = cnx.cursor(buffered=True)
cursor.execute(query)
isexist = cursor.rowcount>0
if arguments['--debug']:
    tu.debug("Number of rows = " + str(cursor.rowcount))
cursor.close()

if isexist:
    tu.error("Patient " + patientID + " already exists.")

else:
    # Remaining questions
    cohort = raw_input('Cohort [press <enter> to skip]: ')
    ethnicity = raw_input('Ethnicity [press <enter> to skip]: ')
    gender = raw_input('Cohort [M/F/U=unknown (default)]: ')
    if str.lower(gender)=='f':
        gender = 'female'
    elif str.lower(gender)=='m':
        gender = 'male'
    else:
        gender = 'unknown'
    primary_diagnosis = raw_input('Primary diagnosis [press <enter> to skip]: ')

    # insert
    add_patient = ("INSERT INTO patient_info "
                   "(patient_id, cohort, ethnicity, gender, primary_diagnosis) "
                   "VALUES (%s, %s, %s, %s, %s)")

    try:
        cursor = cnx.cursor()
        cursor.execute(add_patient, (patientID, cohort, ethnicity, gender, primary_diagnosis))
        cnx.commit()
        cursor.close()
    except mysql.connector.Error as err:
        tu.error(str(err))
        exit()

    tu.ok("Patient " + patientID + " added to the database")

# don't forget to close the connection
cnx.close()
