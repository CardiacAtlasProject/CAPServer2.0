Original distribution from Jagir Hussain before any modification.
This note contains documentation & installation instruction.

## Direct deployment on fresh Linux VM

Requirements:
* MySQL server 5.5+
* OpenJDK 7
* JBoss AS Final 7.1

Installation:

1. Install MySQL server. Follow installation steps from [MySQL reference manual](https://dev.mysql.com/doc/).

2. We need to create three databases in MySQL server: `pacsdb`, `CAP` and `CAPUSERS` databases. The `pacsdb` database follows standard open PACS table relationship, which we will use [`dcm4chee`](https://dcm4che.atlassian.net/wiki/display/ee2/MySQL) script to create it.

  Create pacsdb database:
  ```
> mysql -u root -p
mysql> create database pacsdb;
mysql> grant all on pacsdb.* to 'pacs'@'localhost' identified by 'pacs';
mysql> \q
```
  Run `pacsdb` database creation setup script from `deploy/sql` directory.
  ```{bash}
  > cd deploy/sql
  > mysql -upacs -ppacs < create-pacsdb.mysql
  ```
  This will create a database called `pacsdb` and a user `pacs` who has access and privileges to do anything only if it accesses the database from localhost. The password for user `pacs` is `pacs`.

3. Initialise `CAP` and `CAPUSERS` databases.

   **Warning: this will drop all existing CAP and CAPUSERS data.**

   ```
   > ./init-capdb.sh
   ```

4. Install OpenJDK 7.

   Make sure the correct version of java is used. You can check it by
  ```
> java -version
java version "1.7.0_99"
```
  it should give you version 1.7.xxx. If you have multiple java versions, you must modify the `standalone.conf` or `domain.conf` file to refer the correct JDK environment.

5. Install JBoss Application Server 7.1

   You can download the zip file from:
   ```
    wget http://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.zip
    ```

    Some tutorials on JBoss AS 7.1 installation:
    * [Installation on CentOS 6](https://www.opensourcearchitect.co/tutorials/installing-jboss-7-1-on-centos-6).
