create table agenda (
        hairdresser_id int4 not null,
        primary key (hairdresser_id)
    );
create table app_user (
        role varchar(31) not null,
        id  serial not null,
        name varchar(255) not null,
        password varchar(255) not null,
        phone varchar(255) not null,
        status int4 not null,
        username varchar(255) not null,
        distance varchar(255),
        primary key (id)
    );
create table appointment (
        id  serial not null,
        date timestamp not null,
        duration int8 not null,
        notes varchar(255),
        status int4 not null,
        user_id int4 not null,
        primary key (id)
    );
create table appointment_work (
        Appointment_id int4 not null,
        works_id int4 not null,
        primary key (Appointment_id, works_id)
    );
create table block (
        id  serial not null,
        length int8 not null,
        start time not null,
        appointment_id int4,
        workingDay_id int4 not null,
        primary key (id)
    );
create table non_working_days (
        Agenda_hairdresser_id int4 not null,
        nonWorkingDays date
    );
create table stretch (
        id  serial not null,
        endTime time not null,
        startTime time not null,
        stretches_id int4,
        primary key (id)
    );
create table timetable (
        id  serial not null,
        endDay date not null,
        startDay date not null,
        timetables_hairdresser_id int4,
        primary key (id)
    );
create table work (
        id  serial not null,
        duration int8,
        kind varchar(255),
        name varchar(255),
        primary key (id)
    );
create table working_day (
        id  serial not null,
        date date not null,
        agenda_hairdresser_id int4 not null,
        primary key (id)
    );
alter table app_user 
        add constraint username_uk unique (username);
alter table agenda 
        add constraint agenda_hairdresser_fk 
        foreign key (hairdresser_id) 
        references app_user;
alter table appointment 
        add constraint appointment_user_fk 
        foreign key (user_id) 
        references app_user;
alter table appointment_work 
        add constraint appointment_work_fk 
        foreign key (works_id) 
        references work;
alter table appointment_work 
        add constraint work_appointment_fk 
        foreign key (Appointment_id) 
        references appointment;
alter table block 
        add constraint block_appointment_fk 
        foreign key (appointment_id) 
        references appointment;
alter table block 
        add constraint block_working_day_fk 
        foreign key (workingDay_id) 
        references working_day;
alter table non_working_days 
        add constraint non_working_days_agenda_fk 
        foreign key (Agenda_hairdresser_id) 
        references agenda;
alter table stretch 
        add constraint stretch_timetable_fk 
        foreign key (stretches_id) 
        references timetable;
alter table timetable 
        add constraint agenda_timetable_fk 
        foreign key (timetables_hairdresser_id) 
        references agenda;
alter table working_day 
        add constraint working_day_agenda_fk 
        foreign key (agenda_hairdresser_id) 
        references agenda;