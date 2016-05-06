Original distribution from Jagir Hussain before any modification.
This note contains documentation & installation instruction.

## Direct deployment on fresh Linux VM

Requirements:
* MySQL server 5.5+
* OpenJDK 7
* JBoss AS Final 7.1
* DCM4CHEE service running on MySQL database

Note that the CAP2.0 service needs to run on top of PACS database. It needs to connect to PACS database through DCM4CHEE (http://dcm4che.org/) service, which can be located on the same machine or remotely.

Installation:

1. Install MySQL server. Follow installation steps from [MySQL reference manual](https://dev.mysql.com/doc/).

2. Install DCM4CHEE service that uses MySQL (see: https://dcm4che.atlassian.net/wiki/display/ee2/Installation).
   Assume that:
   AE title = DCM4CHEE, machine = localhost, port = 11112
   dicom://DCM4CHEE@localhost:11112

   Note that you may skip this step if you want to use existing DCM4CHEE service from other machine.

3. Install JDK 7.
  Note that if you install DCM4CHEE, then you already have JDK.

  Make sure the correct version of java is used. You can check it by
 ```
> java -version
java version "1.7.0_80"
```
 it should give you version 1.7.xxx. If you have multiple java versions, you must modify the `standalone.conf` or `domain.conf` file to refer the correct JDK environment.

4. Install JBoss Application Server 7.1

  You can download the zip file from:
  ```
   wget http://download.jboss.org/jbossas/7.1/jboss-as-7.1.1.Final/jboss-as-7.1.1.Final.zip
   ```

   Some tutorials on JBoss AS 7.1 installation:
   * [Installation on CentOS 6](https://www.opensourcearchitect.co/tutorials/installing-jboss-7-1-on-centos-6).
   * [Installation on CentOS by David Ghelini.](http://www.davidghedini.com/pg/entry/install_jboss_7_on_centos)

5. We need to create two databases in MySQL server:`CAP` and `CAPUSERS` databases.

   **Warning: this will drop all existing CAP and CAPUSERS data.**

   ```
   > ./init-capdb.sh
   ```
