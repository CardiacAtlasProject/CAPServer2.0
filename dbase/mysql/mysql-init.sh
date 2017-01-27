#!/usr/bin/env bash
set -x

sudo systemctl status mysqld

# remove initial root password
initpwd="$(sudo grep 'temporary password' /var/log/mysqld.log | sed s/^.*root@localhost:\ *//)"
mysql -uroot -p"${initpwd}" --connect-expired-password < initdb.sql
echo "MySQL root password is: xpacsdbadmin"
echo "CAP user is: cap, password: *CapUser*"

# Initiate the XPACS schema
echo "Create XPACS schema"
mysql -uroot -pxpacsdbadmin < ~/xpacs-db/schema/xpacs-create-schema.sql
