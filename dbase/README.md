The new CAP server consists of two separate databases:

* PACS (Picture Archiving and Communication System) to store DICOM images based on open source [DCM4CHEE](http://www.dcm4che.org/) framework.
* XPACS (auXiliarry PACS) database that stores non-DICOM image data.

# Installing MySQL 5.7

Installation is automatic through `vagrant up` command.

1. Initializing root password (*if you have not done it*)

   During the installation process, a temporary password is generated for the MySQL root user. Locate it in the `mysqld.log` with this command:
   ```
   $ sudo grep 'temporary password' /var/log/mysqld.log
   ```

   Make a note of that password before running MySQL secure initialization for the first time:
   ```
   $ sudo mysql_secure_installation
   ```

   Enter a new 12-character password that contains at least one uppercase letter, one lowercase letter, one number and one special character. Re-enter it when prompted.

2. Create **XPACS** database

    ```
    $ mysql -uroot -p
    mysql> CREATE DATABASE xpacs;
    mysql> CREATE DATABASE pacs;
    ```

3. Create user **`cap`**

   This user will be used in the code to connect to both XPACS and PACS database. It is recommended that you use the same password and privileges for both database.

   Use the same username and password, e.g.
   ```
   mysql> CREATE USER 'cap'@'%' IDENTIFIED BY 'SOME_PASSWORD' PASSWORD EXPIRE NEVER;
   mysql> GRANT ALL ON xpacs.* TO 'cap'@'%' IDENTIFIED BY 'SOME_PASSWORD';
   mysql> GRANT ALL ON pacs.* TO 'cap'@'%' IDENTIFIED BY 'SOME_PASSWORD';
   ```
