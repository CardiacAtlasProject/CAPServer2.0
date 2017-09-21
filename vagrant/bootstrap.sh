#!/usr/bin/env bash

# prepare necessary packages
sudo yum -y install epel-release vim wget
sudo yum -y update
sudo yum -y groupinstall "Development tools"

# installing maven
sudo wget http://repos.fedorapeople.org/repos/dchen/apache-maven/epel-apache-maven.repo -O /etc/yum.repos.d/epel-apache-maven.repo
sudo sed -i s/\$releasever/7/g /etc/yum.repos.d/epel-apache-maven.repo
sudo yum install -y apache-maven

# installing the latest version of node.js
curl --silent --location https://rpm.nodesource.com/setup_8.x | sudo bash -
sudo yum -y install nodejs


# install pip & necessary modules
if hash pip 2>/dev/null; then
   echo "Package pip has already been installed"
else

  sudo yum -y install python-pip python-wheel
  sudo yum upgrade python-setuptools
  sudo pip install --upgrade pip
  sudo pip install docopt

  wget https://dev.mysql.com/get/Downloads/Connector-Python/mysql-connector-python-2.1.7-1.el7.x86_64.rpm
  sudo rpm -ivh mysql-connector-python-2.1.7-1.el7.x86_64.rpm

fi

# installing yarn
if hash yarn 2>/dev/null; then
   echo "Package yarn has already been installed"
else

   echo "DOWNLOAD & INSTALLING yarn"
   sudo wget https://dl.yarnpkg.com/rpm/yarn.repo -O /etc/yum.repos.d/yarn.repo
   sudo yum -y install yarn

   sudo echo -e "export PATH=\$PATH:`yarn global bin`:\$HOME/.config/yarn/global/node_modules/.bin" > /etc/profile.d/yarn.sh

   yarn global add yo
   yarn global add bower
   yarn global add gulp-cli
   yarn global add generator-jhipster
fi

# installing mysql server 5.7
if hash mysql 2>/dev/null; then
  echo "Package mysql has already been installed"
else

  echo "INSTALLING MYSQL 5.7"
  wget https://dev.mysql.com/get/mysql57-community-release-el7-11.noarch.rpm
  sudo rpm -ivh mysql57-community-release-el7-11.noarch.rpm
  sudo yum -y install mysql-server
  sudo systemctl start mysqld
  sudo systemctl status mysqld

fi

# installing openldap server
if hash slapd 2>/dev/null; then
  echo "Package openldap has already been installed"
else

  echo "INSTALLING OPENLDAP SERVER"

  sudo yum -y install openldap compat-openldap openldap-clients openldap-servers openldap-servers-sql openldap-devel
  sudo systemctl start slapd.service
  sudo systemctl enable slapd.service
  sudo systemctl status slapd.service

fi

# FINISH
echo "VM is ready."
echo "You can login by '$ vagrant ssh'"
echo "Starting/stopping vm by '$ vagrant up' and '$ vagrant halt' commands"
echo "Destroying the vm by '$ vagrant destroy' command"
echo "NOTE: You need to setup root password for MySQL and OpenLDAP servers."
echo "$ sudo grep 'temporary password' /var/log/mysqld.log"
echo "$ sudo mysql_secure_installation"
echo "$ sudo slappasswd"
