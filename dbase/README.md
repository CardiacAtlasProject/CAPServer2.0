# Installing MySQL 5.7

You need to install and setup MySQL 5.7 manually because CentOS 7 prefers MariaDB
when you install by default.

1. Install MySQL 5.7 package
   ```
   $ sudo rpm -ivh ~/dbase/mysql/mysql57-community-release-el7-9.noarch.rpm
   ```

2. Install the server
   ```
   $ sudo yum -y install mysql-server
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

4. Initializing root password

   During the installation process, a temporary password is generated for the MySQL root user. Locate it in the `mysqld.log` with this command:
   ```
   $ sudo grep 'temporary password' /var/log/mysqld.log
   ```

   Make a note of that password before running MySQL secure initialization for the first time:
   ```
   $ sudo mysql_secure_installation
   ```

   Enter a new 12-character password that contains at least one uppercase letter, one lowercase letter, one number and one special character. Re-enter it when prompted.

 5. Initialize the XPACS database

    ```
    $ mysql -uroot -p < ~/dbase/mysql/initdb.sql
    ```
