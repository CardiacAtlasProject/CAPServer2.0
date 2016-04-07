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

read -s -p 'Please enter root password for mysql: ' rootpass
echo

# CREATE CAP & CAPUSERS DATABASES
echo 'Creating CAP database...'
echo 'IGNORE THE WARNING'
mysql -uroot -p$rootpass < create-capdb.mysql

echo 'Creating CAPUSERS database...'
echo 'IGNORE THE WARNING'
mysql -uroot -p$rootpass < create-capusers.mysql

# CREATE ADMIN PASSWORD
echo 'Creating ADMIN password'
echo 'IGNORE THE WARNING'
mysql -uroot -p$rootpass << EOFSQL
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
