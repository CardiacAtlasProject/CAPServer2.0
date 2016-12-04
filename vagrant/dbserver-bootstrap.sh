#!/usr/bin/env bash

# prepare necessary packages
sudo yum -y update
sudo yum -y install man
sudo yum -y install vim
sudo yum -y install epel-release
sudo yum -y groupinstall "Development tools"

# fix vbox guest addition
#sudo yum -y install dkms
#sudo /etc/init.d/vboxadd setup

#sudo yum -y install java-1.8.0-openjdk-devel

# Install MySQL server
if hash mysql 2>/dev/null; then
    echo "Package MySQL has already been installed"
else
    echo "INSTALLING MYSQL 5.7"
    curl -s -o /tmp/mysql57-community-release-el6-9.noarch.rpm http://repo.mysql.com/mysql57-community-release-el6-9.noarch.rpm
    sudo yum -y localinstall /tmp/mysql57-community-release-el6-9.noarch.rpm
    rm -rf /tmp/mysql57-community-release-el6-9.noarch.rpm

    sudo yum -y install mysql-community-server

    # change some global variables
    sudo cp xpacs-db/vagrant-init/my.cnf /etc/my.cnf

    echo "Starting MySQL 5.7"
    sudo service mysqld start

    # remove initial root password
    initpwd="$(sudo grep 'temporary password' /var/log/mysqld.log | sed s/^.*root@localhost:\ *//)"
    mysql -uroot -p"${initpwd}" --connect-expired-password < ./xpacs-db/vagrant-init/initdb.sql
    echo "MySQL root password is: xpacsdbadmin"
    echo "CAP user is: cap, password: *CapUser*"

fi
