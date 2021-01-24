CREATE TABLE user (
  id INT NOT NULL AUTO_INCREMENT primary key,
  uid VARCHAR(40) NOT NULL ,
  name VARCHAR(100) ,
  email VARCHAR(254) ,
  password varchar(64) ,
  company VARCHAR(1000) NULL,
  last_login TIMESTAMP NULL,
  created TIMESTAMP NOT NULL default CURRENT_TIMESTAMP,
  updated TIMESTAMP NULL,
  created_by varchar(40) references user(uid),
  updated_by varchar(40) references user(uid),
  login_attempts INT NULL,
  status SMALLINT NOT NULL default 1,
  profile_uid varchar(40) ,
  parent_user_uid varchar(40) references user(uid)
 );

