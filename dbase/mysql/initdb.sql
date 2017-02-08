-- Create cap user
CREATE USER 'cap'@'%' IDENTIFIED BY '*CapUser*1234' PASSWORD EXPIRE NEVER;

-- Create XPACS
CREATE DATABASE xpacs;
GRANT ALL ON xpacs.* TO 'cap'@'%' IDENTIFIED BY '*CapUser*1234';
