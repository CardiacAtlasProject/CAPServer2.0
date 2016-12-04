# CAPServer2.0

## XPACS

XPACS is an auXiliarry PACS database support for non-DICOM data. The source is written under the `xpacs` folder.

Requirement for development:
* [Oracle VM VirtualBox](https://www.virtualbox.org/)
* [Vagrant](https://www.vagrantup.com/)
* [Spring suite IDE](http://spring.io/) (can be Eclipse with STS plugin)

After vagrant installation, add vbguest plugin:
```
$ vagrant plugin install vagrant-vbguest
```

Running up:
```
$ cd CAPServer2.0
$ vagrant up
```

That's it. You have two virtual machines: web and db.
To go to each vm:
```
$ vagrant ssh web
$ vagrant ssh db
```

Read [Vagrant documentation](https://www.vagrantup.com/docs/) for more details and commands.

Both `xpacs` and `dbase` folders are shared to the corresponding VMs. You can modify the source codes using Spring IDE natively, then run the web service from the `web` vm. The port forwarding, and all other configurations, are written in `Vagrantfile` file.

Starting up the service:
```
$ vagrant ssh web
[vagrant@localhost ~]$ cd xpacs
[vagrant@localhost xpacs]$ mvn spring-boot:run
```

Fire up your web browser and open http://localhost:8585/xpacs-web/

## Database

The new CAP server consists of two separate databases:

* PACS (Picture Archiving and Communication System) to store DICOM images based on open source [DCM4CHE] framework.
* XPACS (auXiliarry PACS) database that stores non-DICOM image data.

Both databases are implemented in MySQL database server.

**PACS schema (ver 2.18)**
![pacs](dbase/schema/2.18.x Database Scheme.jpg)

**XPACS schema (ver 0.3)**
![xpacs](dbase/schema/xpacs-schema.png)

This schema is centralised by the `PATIENT_HISTORY` table, which holds events of patients. An event is either imaging session exam, GP visit, cath lab, etc. All are identified by CAP's patient_id. The column PACS_study_iuid links to an imaging session in the STUDY table of the PACS schema (see above). If an event is not an imaging exam, this column will be NULL.

A list of patients are stored in the table `PATIENT_INFO` that holds persistence data of a single patient. These values are constant at each event.

Four tables are linked to `PATIENT_HISTORY` :
* `CLINICAL_NOTE`
* `AUX_FILE`, which contains links to [URI] of files stored externally.
* `CAP_MODEL` that defines a heart model.
* `BASELINE_DIAGNOSIS`


<!-- URLs -->
[DCM4CHE]: http://dcm4che.org/
[URI]: https://en.wikipedia.org/wiki/Uniform_Resource_Identifier
