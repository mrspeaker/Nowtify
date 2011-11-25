# --- !Ups
create table user (
  email                     varchar(255) not null primary key,
  password                  varchar(255) not null
);

create table story (
  id                        bigint not null primary key,
  name                      varchar(255) not null,
  rank                      bigint,
  rate                      bigint,
  added                     timestamp,
  updated                   timestamp
);

create sequence story_seq start with 1000;
    
# --- !Downs
drop table if exists story;
drop sequence if exists story_seq;
drop table if exists user;
