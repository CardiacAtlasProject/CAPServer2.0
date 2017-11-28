# CAPServer2.0

Requirements for development:
* [Oracle VM VirtualBox](https://www.virtualbox.org/)
* [Vagrant](https://www.vagrantup.com/)
* [Spring suite IDE](http://spring.io/) (can be Eclipse with STS plugin)


## Quick start up

Fresh installation using vagrant:
```
$ cd CAPServer2.0
$ vagrant plugin install vagrant-vbguest
$ vagrant up
$ vagrant ssh
[vagrant@localhost ~]$ cd xpacs
[vagrant@localhost ~]$ mvn -N io.takari:maven:wrapper
[vagrant@localhost ~]$ ./mvnw
```

Open http://localhost:8080, you should see the XPACSWEB interface:

![xpacs-interface](.img/xpacsweb-interface.png)

Read [Vagrant documentation](https://www.vagrantup.com/docs/) for more details on configurations and custom commands. You can modify the `Vagrantfile` configuration file to customize your development environment. Examples are creating a scratch folder for your testing, or setting port forwarding on some services.

## Quick DCM4CHEE-ARC database setup

The XPACS-WEB application needs to connect to a PACS database to retrieve patients' image information. You need to install DCM4CHEE-ARC separately on your vagrant VM.

There are more information about installing dcm4chee-arc from [this site](https://github.com/CardiacAtlasProject/dcm4chee-arc-notes/tree/master/centos7-mysql), but for quick setup, we're going to install dcm4chee-arc with MySQL server on Centos7 without secured-ui:

### Initialise the MySQL server

Make a note on the default root password:
```bash
[vagrant@localhost ~]$ sudo grep 'temporary password' /var/log/mysqld.log
```

Change the default root password:
```bash
[vagrant@localhost ~]$ sudo mysql_secure_installation
```

### Setting up OpenLDAP server

Change the OpenLDAP root password
```bash
[vagrant@localhost ~]$ $ sudo slappasswd
New password:
Re-enter new password:
SECRET_KEY
```

Make notes on the root OpenLDAP password and the `SECRET_KEY`. They will be needed later for accessing the directory from dcm4chee-arc.

Let's make references about these as:
* **`$ROOT_OPENLDAP_PASSWORD`** for the new root password
* **`$SECRET_KEY_OPENLDAP`** for the `SECRET_KEY` generated above

### Installing dcm4chee-server

The installation process is pretty complicated. You can see the full details from the [dcm4chee-arc-light installation page](https://github.com/dcm4che/dcm4chee-arc-light/wiki/Installation). I have made a python script to automate the installation process.

First, we need to download a couple of things.

1. Download the `dcm4chee-arc` binary distribution
```
$ wget https://sourceforge.net/projects/dcm4che/files/dcm4chee-arc-light5/5.10.5/dcm4chee-arc-5.10.5-mysql.zip
$ unzip dcm4chee-arc-5.10.5-mysql.zip
```

2. Download the `wildfly` application server
```
$ wget -qO- http://download.jboss.org/wildfly/10.1.0.Final/wildfly-10.1.0.Final.tar.gz | tar xvz
```

3. Download the mySQL JDBC connector
```
$ wget -qO- https://dev.mysql.com/get/Downloads/Connector-J/mysql-connector-java-5.1.44.tar.gz | tar xvz
```

Create `dcm4chee-arc-config.json` file below to set user settings and permissions to the MySQL and OpenLDAP servers. Change the values according to your setup.
```json
{
  "dcm4cheeDir": "/home/vagrant/dcm4chee-arc-5.10.5-mysql",
  "wildflyHome": "/home/vagrant/wildfly-10.1.0.Final",
  "mysql": {
    "host": "localhost",
    "port": 3306,
    "rootPasswd": "<ROOT_PASSWORD_TO_MYSQL_FROM_PREVIOUS_STEP>",
    "dbName": "<DCM4CHEE_PACS_DATABASE_NAME>",
    "userName": "<NEW_MYSQL_USER_TO_CONNECT_FROM_DCM4CHEE>",
    "userPasswd": "<SET_MYSQL_USER_PASSWORD>"
  },
  "ldap": {
    "rootPasswd": "<$ROOT_OPENLDAP_PASSWD>",
    "olcRootPW": "<$SECRET_KEY_OPENLDAP>",
    "domainName": "your-domain.org"
  },
  "jdbcJarFile" : "/home/vagrant/mysql-connector-java-5.1.44/mysql-connector-java-5.1.44-bin.jar"
}
```

Run the installation script:
```bash
$ wget https://raw.githubusercontent.com/avansp/dcm4chee-arc-notes/master/centos7-mysql/install-dcm4chee-arc-mysql.py
$ python install-dcm4chee-arc-mysql.py dcm4chee-arc-config.json
```

### Configure the WildFly

In the next step, you must run the dcm4chee-arc and apply the configuration steps below from another shell.

1. Start the `WildFly` with `dcm4chee-arc.xml` setting
   ```
   $ ~/wildfly-10.1.0.Final/bin/standalone.sh -b 0.0.0.0 -c dcm4chee-arc.xml
   ```
   Note the binding address `0.0.0.0` in order to accept webpage request from all sources.

2. Open a shell in another terminal.

   *You can open another shell by calling `vagrant ssh` again from your guest machine*

3. Configure the wildfly using another script:
   ```
   $ wget https://raw.githubusercontent.com/avansp/dcm4chee-arc-notes/master/centos7-mysql/configure-dcm4chee-arc.py
   $ python configure-dcm4chee-arc.py dcm4chee-arc-config.json
   ```  

4. Open the UI at http://localhost:8080/dcm4chee-arc/ui2


### Change the port number

In order to avoid clashing with XPACS, you need to change the port from 8080 to e.g. 8585.

Open `$WILDFLY_HOME/standalone/configuration/dcm4chee-arc.xml` and adjust port numbers in:
```xml
  <socket-binding-group name="standard-sockets" default-interface="public" port-offset="${jboss.socket.binding.port-offset:0}">
    <socket-binding name="management-http" interface="management" port="${jboss.management.http.port:9990}"/>
    <socket-binding name="management-https" interface="management" port="${jboss.management.https.port:9993}"/>
    <socket-binding name="ajp" port="${jboss.ajp.port:8009}"/>
    <socket-binding name="http" port="${jboss.http.port:8080}"/>
    <socket-binding name="https" port="${jboss.https.port:8443}"/>
    <socket-binding name="iiop" interface="unsecure" port="3528"/>
    <socket-binding name="iiop-ssl" interface="unsecure" port="3529"/>
    <socket-binding name="txn-recovery-environment" port="4712"/>
    <socket-binding name="txn-status-manager" port="4713"/>
    <outbound-socket-binding name="mail-smtp">
        <remote-destination host="localhost" port="25"/>
    </outbound-socket-binding>
  </socket-binding-group>
```


### Disable SSL connection

1. Open `$WILDFLY_HOME/standalone/configuration/dcm4chee-arc.xml` file

2. In the **`connection-url`** element, replace `jdbc:mysql://localhost:3306/<DB_NAME>` with
```
   jdbc:mysql://localhost:3306/<DB_NAME>?autoReconnect=true&amp;verifyServerCertificate=false&amp;useSSL=true&amp;requireSSL=true
```

3. Restart dcm4chee-arc server.

### Change the default DICOM object storage location

The default storage location is at `$WILDFLY_HOME/standalone/data/fs1/`, which is not what you want.

1. Create a folder for the new location, e.g. `/opt/ARCHIVE`.

2. Assume that your domain name is `domain.org`. Create a file called `storage.ldif` that contains:

   ```
   version: 1
   dn: dcmStorageID=fs1,dicomDeviceName=dcm4chee-arc,cn=Devices,cn=DICOM Configuration,dc=domain,dc=org
   changetype: modify
   replace: dcmURI
   dcmURI: file:/opt/ARCHIVE/
   ```

3. From command line:
```bash
$ sudo ldapmodify -x -D "cn=admin,dc=domain,dc=org" -H ldap:/// -W -f storage.ldif
Enter LDAP password:
```

4. You can check the storage location setting by using this command:
```{bash}
sudo ldapsearch -x -W -D "cn=admin,dc=domain,dc=org" -H ldap:/// -b "dcmStorageID=fs1,dicomDeviceName=dcm4chee-arc,cn=Devices,cn=DICOM Configuration,dc=domain,dc=org"
```

5. Restart the dcm4chee server

### Test DICOM connection

You can send DICOM C-ECHO message using [dcm4che tool's storescu](https://github.com/dcm4che/dcm4che) command line. Assume that your `AET=DCM4CHEE` and your host is `localhost`, then
```
$ storescu -c DCM4CHEE@localhost:11112
```

To upload images, see [using HOROS for upload/retrieve](https://github.com/CardiacAtlasProject/dcm4chee-arc-notes/blob/master/using-horos.md) or [using dcm4che toolkit](https://github.com/CardiacAtlasProject/dcm4chee-arc-notes/blob/master/post-installation.md#upload-a-study).

### XPACS access to PACS database

Open file `/home/vagrant/xpacs/src/main/resource/config/application.yml` and edit `application` section:
```
application:
    pacsdb: # DCM4CHEE server configuration
        init-start: false
        url: http://localhost:8585
        AET: DCM4CHEE
        jdbc-url: jdbc:mysql://localhost:3306/<DCM4CHEE_PACS_DATABASE_NAME>?useUnicode=true&characterEncoding=utf8&useSSL=false
        jdbc-username: <MYSQL_USERNAME>
        jdbc-password: <MYSQL_USER_PASSWORD>
        jdbc-driver: com.mysql.jdbc.Driver
```

Replace:
* `<DCM4CHEE_PACS_DATABASE_NAME>` with pacs database name
* `<MYSQL_USERNAME>` with user name
* `<MYSQL_USER_PASSWORD>` with user password

See `dcm4chee-arc-config.json` configuration file above.


## Production mode

For the production mode, you need to setup a database called `xpacsweb`.
