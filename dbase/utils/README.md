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

## Pre-requisites

1. Make sure you have created the XPACS schema.

2. Make sure you have access to the MySQL database with the necessary privilege to the `xpacsdb` schema.

See more information on the [XPACS installation setup](..).
