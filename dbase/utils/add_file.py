"""Add a new file to the xpacsdb.patient_info table.

Usage:
    add_patient.py [options] <patientid> <file>
    add_patient.py -h

Arguments:
    patientid   PatientID to attach with the file.
                Error if there is no such PatientID.
                Use add_patient.py to add new patient to the database.
    file        File to be added to the database

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
import datetime
import magic

def read_file(filename):
    with open(filename, 'rb') as f:
        contents = f.read()
    return contents

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

# check the patient
# This should be unique. Have to check it first.
query = "SELECT * FROM patient_info WHERE patient_id = '" + arguments['<patientid>'] + "'"
if arguments['--debug']:
    tu.debug(query)

cursor = cnx.cursor(buffered=True, named_tuple=True)
cursor.execute(query)
isexist = cursor.rowcount>0
if arguments['--debug']:
    tu.debug("Number of rows = " + str(cursor.rowcount))

# get the row id of the patient
id = cursor.fetchone().id
if arguments['--debug']:
    tu.debug("The id row for " + arguments['<patientid>'] + " is " + str(id))

cursor.close()

if not isexist:
    tu.error("Patient " + arguments['<patientid>'] + " does not exist. Run add_patient.py to add a new patient.")

else:
    # creation_date = today
    dd = datetime.date.today()

    # ask for description
    desc = raw_input('Description [press <enter> to skip]: ')

    # build the query
    add_file = ("INSERT INTO aux_file "
                "(patient_infofk_id, creation_date, description, file, file_content_type) "
                "VALUES (%(pat_id)s, %(date)s, %(desc)s, %(file)s, %(content_type)s)")

    new_file = {
        'pat_id': id,
        'date': dd,
        'desc': desc,
        'file': read_file(arguments['<file>']),
        'content_type': magic.from_file(arguments['<file>'])
    }

    try:
        cursor = cnx.cursor()
        cursor.execute(add_file, new_file)
        cnx.commit()
        cursor.close()

    except mysql.connector.Error as err:
        tu.error(str(err))
        exit()

    tu.ok("File " + arguments['<file>'] + " added to the database")
