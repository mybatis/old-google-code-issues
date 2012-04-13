--
--    Copyright 2009-2012 The MyBatis Team
--
--    Licensed under the Apache License, Version 2.0 (the "License");
--    you may not use this file except in compliance with the License.
--    You may obtain a copy of the License at
--
--       http://www.apache.org/licenses/LICENSE-2.0
--
--    Unless required by applicable law or agreed to in writing, software
--    distributed under the License is distributed on an "AS IS" BASIS,
--    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
--    See the License for the specific language governing permissions and
--    limitations under the License.
--

drop sequence if exists TestSequence;
drop table if exists table1;
drop table if exists table2;
drop table if exists table3;

create table table1 (
id int generated by default as identity (start with 11) not null,
name varchar(20)
);

create table table2 (
id int generated by default as identity (start with 22) not null,
name varchar(20),
name_fred varchar(25) generated always as (name || '_fred')
);

create sequence TestSequence as integer start with 33;

create table table3 (
id int not null,
name varchar(20)
);
