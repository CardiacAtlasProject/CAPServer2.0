# Installing MySQL 5.7

You need to install and setup MySQL 5.7 manually because CentOS 7 prefers MariaDB
when you install by default.

1. Install MySQL 5.7 package
   ```
   $ sudo rpm -ivh ~/xpacs-db/mysql/mysql57-community-release-el7-9.noarch.rpm
   ```

2. Install the server
   ```
   $ sudo yum -y install mysql-server
   ```

   Replace the configuration file
   ```
   $ sudo cp ~/xpacs-db/mysql/my.cnf /etc/my.cnf
   ```
   
3. Start MySQL server

   ```
   $ sudo systemctl start mysqld
   ```

   To check whether it has been started properly:
   ```
   $ sudo systemctl status mysqld
   ```

   You should see status containing `Active: active (running)`.

4. Configuring MySQL

   Run the init script
   ```
   $ cd ~/xpacs-db/mysql
   $ ./mysql-init.sh
   ```
