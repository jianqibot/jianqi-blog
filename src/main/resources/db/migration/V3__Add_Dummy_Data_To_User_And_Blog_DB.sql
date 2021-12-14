insert into user(id, username, encrypted_password, avatar, created_at, updated_at)
values(1, "Bill", "password123", "avatar123", now(), now());

insert into user(id, username, encrypted_password, avatar, created_at, updated_at)
values(2, "David", "password456", "avatar456", now(), now());

insert into blog(id, user_id, title, description, content, created_at, updated_at)
values(1, 1, "title 111", "description 111", "content 111", now(), now());

insert into blog(id, user_id, title, description, content, created_at, updated_at)
values(2, 1, "title 222", "description 222", "content 222", now(), now());

insert into blog(id, user_id, title, description, content, created_at, updated_at)
values(3, 1, "title 333", "description 333", "content 333", now(), now());

insert into blog(id, user_id, title, description, content, created_at, updated_at)
values(4, 2, "title 444", "description 444", "content 444", now(), now());

insert into blog(id, user_id, title, description, content, created_at, updated_at)
values(5, 2, "title 555", "description 555", "content 555", now(), now());

insert into blog(id, user_id, title, description, content, created_at, updated_at)
values(6, 2, "title 666", "description 666", "content 666", now(), now());