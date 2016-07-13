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

   **Important notes**
   * Since port 8080 has been used by DCM4CHEE, you must change this port to, e.g. 8181. Edit `$JBOSS_HOME/standalone/configuration/standalone.xml` and replace
   ```
   <socket-binding name="http" port="8080"/>
   ```
   to
   ```
   <socket-binding name="http" port="8181"/>
   ```

   * By default, JBoss web service is only accessible on local host (127.0.0.1). To allow it accessible through the internet, edit the same `standalone.xml` and replace the IP address
   ```
   <interface name="public">
      <inet-address value="${jboss.bind.address:127.0.0.1}"/>
   </interface>
   ```
   to your public IP address, or using the following to use any IP address:
   ```
   <interface name="public">
       <any-ipv4-address/>
   </interface>   
   ```

5. Install additional modules to JBoss:

   Extract `abi-jboss-modules.com.tar.gz` and `mysql-jboss-modules-com.tar.gz` from `libs/jboss-as-7` folder to `$JBOSS_HOME/modules/com` folder.

6. We need to create two databases in MySQL server:`CAP` and `CAPUSERS` databases.

   **Warning: this will drop all existing CAP and CAPUSERS data.**

   ```
   > ./init-capdb.sh
   ```

7. Set JNDI connection in the `standalone.xml` file.

   Find `<datasource jndi-name="java:jboss/datasources/ExampleDS"> ... </datasource>` block and replace it with:
   ```
   <xa-datasource jndi-name="java:jboss/xa-datasources/CAPAccess" pool-name="CAPAccess" enabled="true" use-java-context="true">
        <xa-datasource-property name="URL">
            jdbc:mysql://127.0.0.1:3306/CAPUSERS?zeroDateTimeBehavior=convertToNull&autoReconnect=true&characterEncoding=UTF-8&characterSetResults=UTF-8
        </xa-datasource-property>
        <xa-datasource-property name="User">
            cap2.0
        </xa-datasource-property>
        <xa-datasource-property name="Password">
            cap2.0
        </xa-datasource-property>
        <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
        <driver>com.mysql</driver>
    </xa-datasource>

    <xa-datasource jndi-name="java:jboss/xa-datasources/CAPDS" pool-name="CAPDS" enabled="true" use-java-context="true">
        <xa-datasource-property name="URL">
            jdbc:mysql://127.0.0.1:3306/CAP?zeroDateTimeBehavior=convertToNull&autoReconnect=true&characterEncoding=UTF-8&characterSetResults=UTF-8
        </xa-datasource-property>
        <xa-datasource-property name="User">
            cap2.0
        </xa-datasource-property>
        <xa-datasource-property name="Password">
            cap2.0
        </xa-datasource-property>
        <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
        <driver>com.mysql</driver>
        </xa-datasource>
    ```

    Find `<driver name="h2" module="com.h2database.h2">` and replace it with:
    ```
    <driver name="com.mysql" module="com.mysql">
        <xa-datasource-class>com.mysql.jdbc.jdbc2.optional.MysqlXADataSource</xa-datasource-class>
    </driver>
    ```
