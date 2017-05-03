"""Test connection to a MySQL server

Usage:
    test_connect.py [options] <host> <user>
    test_connect.py -h

Arguments:
    host    MySQL server IP address
    user    Username to connect with

Options:
    -h, --help              Show this screen
    -d, --debug             Show some debug information
    -p, --port              MySQL port. Default is 3306.
    --password=<password>   User password.

Author: Avan Suinesiaputra - University of Auckland (2017)
"""

# Docopt is a library for parsing command line arguments
import docopt
import getpass
import mysql.connector

if __name__ == '__main__':

    try:
        # Parse arguments, use file docstring as a parameter definition
        arguments = docopt.docopt(__doc__)

        # Default values
        if not arguments['--port']:
            arguments['--port'] = 3306

        # Check password
        if arguments['--password'] is None:
            arguments['--password'] = getpass.getpass('Password: ')

        # print arguments for debug
        if arguments['--debug']:
            print arguments

    # Handle invalid options
    except docopt.DocoptExit as e:
        print e.message
        exit()

# everything goes fine
# let's go!
print 'Connecting mysql://' + arguments['<host>'] + ':' + str(arguments['--port']) + ' ...'

try:
    cnx = mysql.connector.connect(user=arguments['<user>'],
                                  host=arguments['<host>'],
                                  port=arguments['--port'],
                                  password=arguments['--password'])

except mysql.connector.Error as err:
    print(err)

else:
    print "SUCCESS"
    cnx.close()
