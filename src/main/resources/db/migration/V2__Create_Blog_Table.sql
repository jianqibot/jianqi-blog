create table blog(
id int primary key auto_increment,
user_id int,
title varchar(100),
description varchar(100),
content text,
created_at datetime,
updated_at datetime)