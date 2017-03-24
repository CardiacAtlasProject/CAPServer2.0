## About

This folder contains scripts to interact directly to the XPACS database using command line interpreter.
Scripts are written in python with some required modules:

1. Install [MySQL Python connector](https://dev.mysql.com/downloads/connector/python/)

   ```bash
   $ pip install mysql-connector
   ```

2. Install [docopt](http://docopt.org/) module:

   ```bash
   $ pip install docopt
   ```

3. Install [magic](https://github.com/ahupp/python-magic) module to determine file's content type:

   ```bash
   $ pip install python-magic
   ```

   Note you need to install the dependencies for running this module.

## Pre-requisites

1. Make sure you have created the XPACS schema.

2. Make sure you have access to the MySQL database with the necessary privilege to the `xpacsdb` schema.

See more information on the [XPACS installation setup](..).
