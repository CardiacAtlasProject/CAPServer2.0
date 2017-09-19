# CAPServer2.0

## Setting up the development environment

Requirements for development:
* [Oracle VM VirtualBox](https://www.virtualbox.org/)
* [Vagrant](https://www.vagrantup.com/)
* [Spring suite IDE](http://spring.io/) (can be Eclipse with STS plugin)

Running up:
```
$ cd CAPServer2.0
$ vagrant plugin install vagrant-vbguest
$ vagrant up
```

Your VM is ready. To go to the vm do:
```
$ vagrant ssh
[vagrant@localhost ~]$
```

Both `xpacs` and `dbase` folders are shared to the corresponding VMs. You can modify the source codes using Spring IDE natively, then run the web service from the vm. The port forwarding, and all other configurations, are written in `Vagrantfile` file.

Read [Vagrant documentation](https://www.vagrantup.com/docs/) for more details and commands. Common commands:

* Stopping the VM:
```
$ vagrant halt
```

* Destroy the VM:
```
$ vagrant destroy
```

## Install DCM4CHEE-ARC

The `xpacs` service needs [dcm4chee-arc](https://github.com/dcm4che/dcm4chee-arc-light/wiki) to work. Follow [installation notes for dcm4chee-arc with MySQL server on the same vagrant VM above](https://github.com/CardiacAtlasProject/dcm4chee-arc-notes) to install the dcm4chee server. Note that you do not have to setup vagrant again.

## Install XPACS schema

[Read this guideline](dbase)

## Build xpacs web interface

[Read the XPACS documentation](xpacs).
