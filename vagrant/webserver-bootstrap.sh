#!/usr/bin/env bash

# prepare necessary packages
sudo yum -y update
sudo yum -y install man
sudo yum -y install vim
sudo yum -y install epel-release
sudo yum -y groupinstall "Development tools"

# install JDK 1.8
sudo yum -y install java-1.8.0-openjdk-devel
sudo echo -e "export JAVA_HOME=/etc/alternatives/java_sdk_1.8.0" >  /etc/profile.d/jdk1.8.0.sh

# installing maven 3.3.9
if hash mvn 2>/dev/null; then
    echo "Package mvn has already been installed"
else
    echo "DOWNLOAD & INSTALLING MAVEN"
    curl -s -o /tmp/apache-maven-3.3.9-bin.tar.gz http://www-eu.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz

    sudo tar -C /opt -xzf /tmp/apache-maven-3.3.9-bin.tar.gz
    rm -rf /tmp/apache-maven-3.3.9-bin.tar.gz

    sudo echo -e "export PATH=\$PATH:/opt/apache-maven-3.3.9/bin"  > /etc/profile.d/mvn.sh
fi
