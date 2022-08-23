create database cloudmc;
create database cloudmc_audit;
create database cloudmc_content;
create database test_cloudmc;
create database test_cloudmc_audit;
create database test_cloudmc_content;
create user 'cloudmc'@'%' identified by 'cloudmc';
create user 'cloudmc'@'localhost' identified by 'cloudmc';
set password for 'cloudmc'@'%'='cloudmc';
set password for 'cloudmc'@'localhost'='cloudmc';
flush privileges;
grant all privileges on *.* to 'cloudmc'@'%' with Grant option;
grant all privileges on *.* to 'cloudmc'@'localhost' with Grant option;
flush privileges;
set global sql_mode='NO_ENGINE_SUBSTITUTION';