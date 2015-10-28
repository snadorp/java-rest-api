# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table transaction (
  id                        bigint not null,
  amount                    double,
  type                      varchar(255),
  parent_id                 bigint,
  constraint pk_transaction primary key (id))
;

create sequence transaction_seq;




# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists transaction;

SET REFERENTIAL_INTEGRITY TRUE;

drop sequence if exists transaction_seq;

