-- Remove init root password for easy development
ALTER USER 'root'@'localhost' IDENTIFIED BY 'xpacsdbadmin';

-- Create cap user
CREATE USER 'cap'@'%' IDENTIFIED BY '*CapUser*' PASSWORD EXPIRE NEVER;

-- Create XPACS
CREATE DATABASE xpacs;
GRANT ALL ON xpacs.* TO 'cap'@'%' IDENTIFIED BY '*CapUser*';
