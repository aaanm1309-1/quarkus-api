create table USERS (
	id bigserial  not null primary key,
	name varchar(100) not null,
	age integer not null
)


create table POSTS (
	id bigserial  not null primary key,
	post_text varchar(1000) not null,
	dateTime timestamp not null,
	userId bigint not null references USERS(id)
)

