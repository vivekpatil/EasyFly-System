CREATE  TABLE aircraft(  
aircraft_id  VARCHAR2(6) NOT  NULL,
aircraft_name VARCHAR2(30) NOT  NULL,
aircr_type_ID VARCHAR2(6) NOT  NULL,
seats_qty_F  NUMBER(3,0) NOT  NULL,
seats_qty_B  NUMBER(3,0) NOT  NULL,
seats_qty_E  NUMBER(3,0) NOT  NULL
);

CREATE  TABLE aircraft_seat(  
aircraft_id  VARCHAR2(6) NOT  NULL,
seat_id   VARCHAR2(4) NOT  NULL,
class_id  VARCHAR2(2) NOT  NULL,
seat_type  VARCHAR2(3) NOT  NULL,
seat_pos  VARCHAR2(1) NOT  NULL
);

CREATE  TABLE  aircraft_type(  
aircr_type_id VARCHAR2(6) NOT  NULL,
aircraft_type VARCHAR2(30) NOT  NULL,
maximum_seats NUMBER(3,0) NOT  NULL
);

CREATE  TABLE  airline(  
airln_id VARCHAR2(2) NOT  NULL,
airln_name VARCHAR2(30) NOT  NULL
);  

CREATE  TABLE  airport(  
airpt_id VARCHAR2(3) NOT  NULL,
airpt_name VARCHAR2(30) NOT  NULL,
country VARCHAR2(30) NOT  NULL
);  

CREATE  TABLE  ticket(  
ticket_no NUMBER(8,0) NOT  NULL,
fare  NUMBER(7,2) NOT  NULL,
issue_date DATE NOT  NULL,
cust_id  VARCHAR2(6) NOT  NULL,
pass_surname VARCHAR2(30) NOT  NULL,
pass_inits VARCHAR2(3) NULL,
pass_title VARCHAR2(10) NULL,
pass_phone VARCHAR2(15) NULL,
staff_ind VARCHAR2(1) NULL
);   

CREATE  TABLE  BOARDINGPASS  (  
ticket_no NUMBER(8,0) NOT  NULL,
BPASS_NO NUMBER(3,0) NOT   NULL,
class_id VARCHAR2(2) NOT  NULL,
dep_date DATE NOT  NULL,
route_id VARCHAR2(5) NOT  NULL,
seg_no NUMBER(2,0) NOT  NULL,
seat_id VARCHAR2(4) NULL,
aircraft_id VARCHAR2(6) NULL,
check_in_time NUMBER(4,0) NULL,
status VARCHAR2(5) NULL,
confirmed_ind VARCHAR2(1) NULL
);  

CREATE  TABLE  customer(  
cust_id VARCHAR2(6) NOT  NULL,
cust_surname VARCHAR2(30) NOT  NULL,
cust_credlim NUMBER(9,2) NOT  NULL,
cust_addr1 VARCHAR2(30) NOT  NULL,
cust_addr2 VARCHAR2(30) NULL,
cust_addr3 VARCHAR2(30) NULL,
cust_inits VARCHAR2(3) NULL,
cust_title VARCHAR2(10) NULL,
cust_phone VARCHAR2(15) NULL
);  

CREATE  TABLE  flight(  
route_id VARCHAR2(5) NOT  NULL,
dep_date DATE NOT  NULL,
arr_time NUMBER(4,0) NOT  NULL,
dep_time NUMBER(4,0) NOT  NULL,
aircraft_id VARCHAR2(6) NULL,
aircr_type_id VARCHAR2(6) NULL
);  

CREATE  TABLE  flight_seg(  
route_id VARCHAR2(5) NOT  NULL,
dep_date DATE NOT  NULL,
seg_no NUMBER(2,0) NOT  NULL,
arr_time NUMBER(4,0) NOT  NULL,
dep_time NUMBER(4,0) NOT  NULL
);  

CREATE  TABLE  route(  
route_id VARCHAR2(5) NOT  NULL,
arr_time NUMBER(4,0) NOT  NULL,
airpt_ID_to VARCHAR2(3) NOT  NULL,
dep_time NUMBER(4,0) NOT  NULL,
overbook_f NUMBER(3,0) NOT  NULL,
overbook_i NUMBER(3,0) NOT  NULL,
airpt_id_from VARCHAR2(3) NOT  NULL,
airln_id VARCHAR2(2) NOT  NULL,
day_no NUMBER(1,0) NOT  NULL
);  

ALTER  TABLE  ROUTE  ADD  (
CONSTRAINT  ROUT_PK
PRIMARY  KEY  (ROUTE_ID));

ALTER  TABLE  ROUTE  ADD  (
CONSTRAINT  ROUT_OPERATED_BY
FOREIGN  KEY  (AIRLN_ID)
REFERENCES    AIRLINE  (  AIRLN_ID));

ALTER  TABLE  ROUTE  ADD  (
CONSTRAINT  ROUT_FROM
FOREIGN  KEY  (AIRPT_ID_FROM)
REFERENCES    AIRPORT  (  AIRPT_ID));

ALTER  TABLE  ROUTE  ADD  (
CONSTRAINT  ROUT_TO
FOREIGN  KEY  (AIRPT_ID_TO)
REFERENCES    AIRPORT  (AIRPT_ID));
  
CREATE  TABLE  route_seg(  
route_id VARCHAR2(5) NOT  NULL,
seg_no NUMBER(2,0) NOT  NULL,
arr_time NUMBER(4,0) NOT  NULL,
dep_time NUMBER(4,0) NOT  NULL,
airpt_ID_to VARCHAR2(3) NOT  NULL,
airpt_ID_from VARCHAR2(3) NOT  NULL,
refr_type VARCHAR2(1) NULL
);  

ALTER TABLE ROUTE_SEG ADD (
CONSTRAINT  RTSG_PK
PRIMARY  KEY  (ROUTE_ID,  SEG_NO));

ALTER  TABLE  ROUTE_SEG  ADD  (
CONSTRAINT  RTSG_IN
FOREIGN  KEY  (ROUTE_ID)
REFERENCES    ROUTE  (  ROUTE_ID));

ALTER  TABLE  ROUTE_SEG  ADD  (
CONSTRAINT  RTSG_FROM
FOREIGN  KEY  (AIRPT_ID_FROM)
REFERENCES    AIRPORT  (  AIRPT_ID));

ALTER  TABLE  ROUTE_SEG  ADD  (
CONSTRAINT  RTSG_TO
FOREIGN  KEY  (AIRPT_ID_TO)
REFERENCES    AIRPORT  (  AIRPT_ID));

CREATE  TABLE  seats_avail(  
route_id VARCHAR2(5) NOT  NULL,
dep_date DATE NOT  NULL,
seg_no NUMBER(2,0) NOT  NULL,
class_id VARCHAR2(2) NOT  NULL,
seats_avail NUMBER(4,0) NOT  NULL,
seats_booked NUMBER(4,0) NOT  NULL
);  

CREATE  TABLE  seat_config(  
aircr_type_id VARCHAR2(6) NOT  NULL,
seat_id  VARCHAR2(4) NOT  NULL,
class_id VARCHAR2(2) NOT  NULL,
seat_type VARCHAR2(3) NOT  NULL,
seat_pos VARCHAR2(1) NOT  NULL
);   

ALTER TABLE AIRCRAFT ADD (
CONSTRAINT  AIRC_PK
PRIMARY  KEY  (AIRCRAFT_ID));

ALTER TABLE AIRCRAFT_SEAT ADD ( CONSTRAINT SEAT_PK
PRIMARY  KEY  (AIRCRAFT_ID,  SEAT_ID));

ALTER TABLE AIRCRAFT_TYPE ADD (
CONSTRAINT  AIRT_PK
PRIMARY  KEY  (AIRCR_TYPE_ID));

ALTER TABLE AIRLINE ADD (
CONSTRAINT AIRL_PK PRIMARY KEY (AIRLN_ID));

ALTER TABLE AIRPORT ADD (
CONSTRAINT AIRP_PK PRIMARY KEY (AIRPT_ID));

ALTER TABLE BOARDINGPASS ADD (
CONSTRAINT  BPASS_PK
PRIMARY  KEY  (TICKET_NO,    BPASS_NO));

ALTER TABLE CUSTOMER ADD (
CONSTRAINT CUST_PK PRIMARY KEY (CUST_ID));

ALTER TABLE FLIGHT ADD (
CONSTRAINT  FLIT_PK
PRIMARY  KEY  (ROUTE_ID,  DEP_DATE));

ALTER TABLE FLIGHT_SEG ADD (
CONSTRAINT FLSG_PK PRIMARY KEY (ROUTE_ID,
DEP_DATE,  SEG_NO));

ALTER TABLE SEATS_AVAIL ADD (
CONSTRAINT  STAV_PK
PRIMARY  KEY  (ROUTE_ID,  DEP_DATE,  SEG_NO,  CLASS_ID));

ALTER TABLE SEAT_CONFIG ADD (
CONSTRAINT  STCN_PK
PRIMARY  KEY  (AIRCR_TYPE_ID,SEAT_ID));

ALTER TABLE TICKET ADD (
CONSTRAINT TICK_PK PRIMARY KEY (TICKET_NO));

ALTER TABLE AIRCRAFT ADD (
CONSTRAINT  AIRC_OF
FOREIGN  KEY  (AIRCR_TYPE_ID)
REFERENCES    AIRCRAFT_TYPE  (  AIRCR_TYPE_ID));

ALTER TABLE AIRCRAFT_SEAT ADD (
CONSTRAINT SEAT_IN FOREIGN KEY (AIRCRAFT_ID)
REFERENCES    AIRCRAFT  (  AIRCRAFT_ID));

ALTER TABLE BOARDINGPASS ADD (
CONSTRAINT  BPASS_FOR
FOREIGN  KEY  (ROUTE_ID,  DEP_DATE,  SEG_NO)
REFERENCES    FLIGHT_SEG  (  ROUTE_ID,  DEP_DATE,  SEG_NO));

ALTER TABLE BOARDINGPASS ADD (
CONSTRAINT  BPASS_ALLOCATED
FOREIGN  KEY  (AIRCRAFT_ID,  SEAT_ID)
REFERENCES      AIRCRAFT_SEAT  (  AIRCRAFT_ID,  SEAT_ID));

ALTER TABLE BOARDINGPASS ADD (
CONSTRAINT BPASS_IN FOREIGN KEY (TICKET_NO)
REFERENCES    TICKET  (  TICKET_NO));

ALTER  TABLE  FLIGHT  ADD  (
CONSTRAINT  FLIT_FOR
FOREIGN  KEY  (ROUTE_ID)
REFERENCES    ROUTE  (  ROUTE_ID));

ALTER  TABLE  FLIGHT  ADD  (
CONSTRAINT  FLIT_FLOWN_BY
FOREIGN  KEY  (AIRCRAFT_ID)
REFERENCES    AIRCRAFT  (  AIRCRAFT_ID));

ALTER TABLE FLIGHT_SEG ADD ( CONSTRAINT FLSG_FOR
FOREIGN  KEY  (ROUTE_ID,  SEG_NO)
REFERENCES    ROUTE_SEG  (  ROUTE_ID,  SEG_NO));
 
ALTER  TABLE  FLIGHT_SEG  ADD  (
CONSTRAINT  FLSG_IN
FOREIGN KEY (ROUTE_ID, DEP_DATE) REFERENCES FLIGHT ( ROUTE_ID, DEP_DATE));

ALTER TABLE SEATS_AVAIL ADD ( CONSTRAINT STAV_FOR
FOREIGN  KEY  (ROUTE_ID,  DEP_DATE,  SEG_NO)
REFERENCES    FLIGHT_SEG  (  ROUTE_ID,  DEP_DATE,  SEG_NO));
 
ALTER  TABLE  SEAT_CONFIG  ADD  (
CONSTRAINT  STCN_IN
FOREIGN  KEY  (AIRCR_TYPE_ID)
REFERENCES    AIRCRAFT_TYPE  (  AIRCR_TYPE_ID));

ALTER  TABLE  TICKET  ADD  (
CONSTRAINT  TICK_FOR
FOREIGN  KEY  (CUST_ID)
REFERENCES    CUSTOMER  (  CUST_ID));
 

-----------------------------------------------------------------------------------------
-- START changes made by us to the case study schema
-----------------------------------------------------------------------------------------

----- setting default values for columns unused by us

alter table route modify (overbook_f default 0);
alter table route modify (overbook_i default 0);
alter table route modify (arr_time default 0);
alter table route modify (dep_time default 0);
alter table route modify (day_no default 0);

ALTER TABLE ROUTE_SEG DROP CONSTRAINT RTSG_IN;

/

ALTER TABLE ROUTE_SEG ADD CONSTRAINT RTSG_IN FOREIGN KEY (ROUTE_ID) REFERENCES ROUTE (ROUTE_ID) ON DELETE CASCADE;

----- dropping FKs which are referring to composite PKs, so that we can drop composite PKs and create surrogate PKs

--dropping BPASS_FOR
alter table boardingpass drop constraint BPASS_FOR;
--dropping STAV_FOR
alter table seats_avail drop constraint STAV_FOR;
--dropping FLSG_IN
alter table flight_seg drop constraint FLSG_IN;
--dropping FLSG_PK
alter table flight_seg drop constraint FLSG_PK;
-- dropping FLSG_FOR
alter table flight_seg drop constraint FLSG_FOR;
--dropping FLIT_PK
alter table flight drop constraint FLIT_PK;
-- dropping RTSG_PK
alter table route_seg drop constraint RTSG_PK;
-- dropping BPASS_PK
alter table boardingpass drop constraint BPASS_PK;

----- creating surrogate PKs and replacing the deleted FKs with new PKs

--making new PK for flight
alter table flight add (constraint FLIT_PK primary key(flight_id));
--making new PK for flight_seg
alter table flight_seg add (constraint FLSG_PK primary key(seg_no));
--making new FK for flight_seg
alter table flight_seg add flight_id number(2,0) not null;
alter table flight_seg add (constraint FLSG_IN foreign key(flight_id) references flight ON DELETE CASCADE);
--making new FK for seats_avail
alter table seats_avail add (constraint STAV_FOR foreign key(seg_no) references flight_seg);
--making new FK for boardingpass
alter table boardingpass add (constraint bpass_FOR foreign key(seg_no) references flight_seg);
-- making new PK for route_seg
alter table route_seg add (constraint RTSG_PK primary key(seg_no));
-- making new PK for boardingpass
alter table boardingpass add (constraint BPASS_PK primary key(bpass_no));
-- increasing the capacity of bpass_no
Alter table boardingpass MODIFY bpass_no number(4,0);

-- add route_seg_no everywhere
alter table flight_seg add route_seg_no NUMBER(2,0) NOT  NULL;
alter table boardingpass add route_seg_no NUMBER(2,0) NOT  NULL;

-----------------------------------------------------------------------------------------
-- END changes made by us to the case study schema
-----------------------------------------------------------------------------------------