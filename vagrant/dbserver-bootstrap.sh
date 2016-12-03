#!/usr/bin/env bash

# prepare necessary packages
sudo yum -y update
sudo yum -y install man
sudo yum -y install vim
sudo yum -y install epel-release
#sudo yum -y groupinstall "Development tools"

# fix vbox guest addition
#sudo yum -y install dkms
#sudo /etc/init.d/vboxadd setup

#sudo yum -y install java-1.8.0-openjdk-devel
