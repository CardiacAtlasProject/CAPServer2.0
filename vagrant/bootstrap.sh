#!/usr/bin/env bash

# prepare necessary packages
sudo yum -y update
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
    curl -sS -o /tmp/apache-maven-3.3.9-bin.tar.gz http://www-eu.apache.org/dist/maven/maven-3/3.3.9/binaries/apache-maven-3.3.9-bin.tar.gz

    sudo tar -C /opt -xzf /tmp/apache-maven-3.3.9-bin.tar.gz
    rm -rf /tmp/apache-maven-3.3.9-bin.tar.gz

    sudo echo -e "export PATH=\$PATH:/opt/apache-maven-3.3.9/bin"  > /etc/profile.d/mvn.sh
fi

# installing node.js
if hash npm 2>/dev/null; then
    echo "Package node.js has already been installed"
else
    echo "DOWNLOAD & INSTALLING Node.js"
    curl -sS -o /tmp/node-v6.10.0-linux-x64.tar.xz https://nodejs.org/dist/v6.10.0/node-v6.10.0-linux-x64.tar.xz

    sudo tar -C /opt -xvf /tmp/node-v6.10.0-linux-x64.tar.xz
    rm -rf /tmp/node-v6.10.0-linux-x64.tar.xz

    sudo echo -e "export PATH=\$PATH:/opt/node-v6.10.0-linux-x64/bin"  > /etc/profile.d/node.sh
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


# FINISH
echo "VM is ready."
echo "You can login by '$ vagrant ssh'"
echo "Starting/stopping vm by '$ vagrant up' and '$ vagrant halt' commands"
echo "Destroying the vm by '$ vagrant destroy' command"
echo "NOTE: The MySQL database has not been installed."
echo "      Read instruction from ~/dbase/README.md file."