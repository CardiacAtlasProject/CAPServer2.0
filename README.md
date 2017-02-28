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

Your VM is setup. To go to the vm do:
```
$ vagrant ssh
```

Both `xpacs` and `dbase` folders are shared to the corresponding VMs. You can modify the source codes using Spring IDE natively, then run the web service from the vm. The port forwarding, and all other configurations, are written in `Vagrantfile` file.

Read [Vagrant documentation](https://www.vagrantup.com/docs/) for more details and commands. Common commands:

* Login to the VM:
```
$ vagrant ssh
[vagrant@localhost ~]$
```

* Stopping the VM:
```
$ vagrant halt
```

* Destroy the VM:
```
$ vagrant destroy
```

## Install MySQL server

This should be done manually. Read [database documentation](dbase/README.md).

## Build xpacs web interface

Please read the [XPACS documentation](xpacs/README.md).
