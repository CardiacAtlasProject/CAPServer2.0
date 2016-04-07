#!/bin/bash

echo 'Initialising CAP databases ....'

# READ NEW ADMIN PASSWORD
read -s -p 'Enter new password for ADMIN user: ' adminpass
echo
read -s -p 'Enter again new password for ADMIN user: ' adminpasschk
echo

if [ $adminpass != $adminpasschk ];
then
    echo 'Passwords do not match. Abort.'
    exit 1
fi

# CREATE CAP & CAPUSERS DATABASES
echo 'Creating CAP database...'
echo 'Please enter root password to mysql'
mysql -uroot -p < create-capdb.mysql

echo 'Creating CAPUSERS database...'
echo 'Please enter root password to mysql'
mysql -uroot -p < create-capusers.mysql

# CREATE ADMIN PASSWORD
echo 'Creating ADMIN password'
echo 'Please enter again root password to mysql'
mysql -uroot -p << EOFSQL
DELETE FROM CAPUSERS.Users WHERE username='ADMIN';

INSERT INTO CAPUSERS.Users
    (username,
     name,
     passwd,
     userdescription,
     lastlogin,
     lastpasswordchange)
VALUES
    ('ADMIN',
     'CAP Administrator',
     PASSWORD("'${adminpass}'"),
     'Super user',
     CURTIME(),
     CURTIME());
EOFSQL
