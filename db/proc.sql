create or replace procedure sp_delete_airline(pairlnid varchar2) as
child_exists  exception;
pragma exception_init (child_exists , -02292);
begin
  delete from airline where airln_id = pairlnid;
exception
   when child_exists  then
      raise_application_error(-20002, ': Cannot delete airline. A route exists for this airline.'); 
end;

/

create or replace procedure sp_delete_airport(pairptid varchar2) as
child_exists  exception;
pragma exception_init (child_exists , -02292);
begin
  delete from airport where airpt_id = pairptid;
exception
   when child_exists then
      raise_application_error(-20001, ': Cannot delete airport. A route exists for this airport.'); 
end;

/

CREATE OR REPLACE TRIGGER trg_Update_Segment_Details
AFTER UPDATE ON ROUTE
FOR EACH ROW
BEGIN
UPDATE ROUTE_SEG SET
ARR_TIME = :new.ARR_TIME,
DEP_TIME = :new.DEP_TIME,
AIRPT_ID_TO = :new.AIRPT_ID_TO,
AIRPT_ID_FROM = :new.AIRPT_ID_FROM
WHERE
ROUTE_ID = :old.ROUTE_ID; 
END;

/

CREATE OR REPLACE PROCEDURE SP_DELETE_AIRCRAFT_TYPE(pAircraft_type_id VARCHAR2) AS
child_exists exception;
pragma exception_init (child_exists , -02292);
begin
  delete from aircraft_type where aircr_type_id = pAircraft_type_id;
exception
   when child_exists  then
      raise_application_error(-20004, ': Cannot delete aircraft type. A aircraft exists for this aircraft type.'); 
end;

/

CREATE OR REPLACE PROCEDURE SP_DELETE_AIRCRAFT(pAircraft_id VARCHAR2) AS
child_exists exception;
pragma exception_init (child_exists , -02292);
begin
  delete from aircraft where aircraft_id = pAircraft_id;
exception
   when child_exists  then
      raise_application_error(-20005, ': Cannot delete aircraft. A flight exists for this aircraft .'); 
end;

/

CREATE OR REPLACE PROCEDURE SP_DELETE_FLIGHT(pFlight_id VARCHAR2) AS
child_exists exception;
pragma exception_init (child_exists , -02292);
begin
  delete from Flight where flight_id = pFlight_id;
exception
   when child_exists  then
      raise_application_error(-20006, ': Cannot delete flight. A dependency exists for this flight .'); 
end;

/

CREATE OR REPLACE PACKAGE pkg_EasyFly_Maintenance AS
procedure sp_delete_airline(pairlnid varchar2);
procedure sp_delete_airport(pairptid varchar2);
PROCEDURE SP_DELETE_AIRCRAFT_TYPE(pAircraft_type_id VARCHAR2);
END pkg_EasyFly_Maintenance;

/

create or replace 
PROCEDURE sp_ADD_FLIGHT(pRoute_ID VARCHAR2,pDep_Date VARCHAR2,pArr_Time NUMBER,
pDep_Time NUMBER,pAircraft_ID VARCHAR2,pFlight_ID NUMBER) IS
vCons_Name VARCHAR(100);
vAircraft_type_id varchar2(5);
BEGIN
  select aircr_type_id into vAircraft_type_id from aircraft where aircraft_id = pAircraft_ID;
	INSERT INTO flight values(pRoute_ID,pDep_Date,pArr_Time,pDep_Time,pAircraft_ID,vAircraft_type_id,pFlight_ID);
	INSERT INTO flight_seg SELECT route_id,pDep_Date,FlightSegmentSeq.nextval,arr_time,dep_time,pFlight_ID, seg_no FROM route_seg WHERE route_id=pRoute_ID;
	COMMIT;
EXCEPTION 
	WHEN OTHERS THEN
        ROLLBACK;
	raise_application_error(-20007, SQLERRM);
END;

/

CREATE OR REPLACE FUNCTION strip_constraint_name(errmsg VARCHAR2)
RETURN VARCHAR2 AS
rp_pos NUMBER;
dot_pos NUMBER;
-- The constraint name lies between the first '.' and the first ')'
BEGIN
dot_pos := INSTR(errmsg, '.');
rp_pos := INSTR(errmsg, ')');
IF (dot_pos = 0 OR rp_pos = 0 ) THEN
RETURN(NULL);
ELSE
RETURN(UPPER(SUBSTR(errmsg, dot_pos+1,
rp_pos-dot_pos-1)));
END IF;
END;

create sequence RouteSegmentSeq start with 1;
create sequence FlightSegmentSeq start with 1;
create sequence TicketSeq start with 1;
create sequence BoardingPassSeq start with 1;

/

create or replace 
PROCEDURE sp_Update_FLIGHT_Details(pRoute_ID VARCHAR2,pDep_Date VARCHAR2,pArr_Time NUMBER,
pDep_Time NUMBER,pAircraft_ID VARCHAR2,pFlight_ID NUMBER) IS
vRoute_ID route.route_id%type;
vAircraft_type_id varchar2(5);
BEGIN

	SELECT route_id INTO vRoute_ID FROM flight WHERE Flight_ID = pFlight_ID;
	select aircr_type_id into vAircraft_type_id from aircraft where aircraft_id = pAircraft_ID;
	UPDATE flight SET
		route_ID = pRoute_ID,
		dep_date = pDep_Date,
		arr_time = pArr_Time,
		dep_time = pDep_Time,
		aircraft_id = pAircraft_ID,
		aircr_type_id = vAircraft_type_id
		WHERE Flight_ID = pFlight_ID;
	
	DELETE FROM flight_seg WHERE Flight_ID = pFlight_ID;
	INSERT INTO flight_seg SELECT route_id,pDep_Date,FlightSegmentSeq.nextval,arr_time,dep_time,pFlight_ID, seg_no FROM route_seg WHERE route_id=pRoute_ID;
	COMMIT;
EXCEPTION 
	WHEN OTHERS THEN
        ROLLBACK;
	raise_application_error(-20008, SQLERRM);
END;

/

CREATE OR REPLACE TRIGGER trg_Insert_Seats_avail
AFTER INSERT ON FLIGHT_SEG
FOR EACH ROW
DECLARE
vFlight_id flight.flight_id%type;
vEconomySeats number;
vBusinessSeats number;
vFirstClassSeats number;
BEGIN
vFlight_id := :new.flight_id;
select seats_qty_f, seats_qty_b, seats_qty_e into vFirstClassSeats, vBusinessSeats, vEconomySeats from aircraft 
where aircraft_id = (select aircraft_id from FLIGHT where flight_id = vFlight_id);

INSERT into SEATS_AVAIL values(:new.route_id, :new.dep_date, :new.seg_no, 'E', vEconomySeats, 0);
INSERT into SEATS_AVAIL values(:new.route_id, :new.dep_date, :new.seg_no, 'B', vBusinessSeats, 0);
INSERT into SEATS_AVAIL values(:new.route_id, :new.dep_date, :new.seg_no, 'F', vFirstClassSeats, 0);
END;

/

create or replace trigger trg_delete_seats_avail
after delete on flight_seg
for each row
Begin
delete from seats_avail where seg_no = :old.seg_no;
end;

/
create or replace
PROCEDURE sp_insert_boarding_pass 
    (p_ticket_no number, p_class_id varchar2, p_seg_no number, p_route_seg_no number) as
    
v_dep_date flight_seg.dep_date%type;
v_route_id flight_seg.route_id%type;
BEGIN
    select dep_date, route_id into v_dep_date, v_route_id from flight_seg where seg_no = p_seg_no;
    insert into boardingpass (route_seg_no, ticket_no, bpass_no, class_id, dep_date, route_id, seg_no, seat_id, aircraft_id, check_in_time, status, confirmed_ind) 
    values(p_route_seg_no, p_ticket_no, boardingPassSeq.nextval, p_class_id, v_dep_date, v_route_id, p_seg_no, null, null, null, null, null);
    update seats_avail set seats_booked = seats_booked + 1 where class_id = p_class_id and seg_no = p_seg_no;
EXCEPTION 
	WHEN OTHERS THEN
	raise_application_error(-20009, SQLERRM);
END;

/

create or replace 
PROCEDURE SP_DELETE_CUSTOMER(pCustomer_id VARCHAR2) AS
child_exists exception;
vBoardingPassCount NUMBER;
pragma exception_init (child_exists , -02292);
begin
--Check if the customer has any boarding pass issued
  SELECT COUNT(BP.BPASS_NO) INTO vBoardingPassCount
  FROM boardingpass BP 
  INNER JOIN ticket T ON bp.ticket_no=t.ticket_no 
  WHERE t.cust_id = pCustomer_id AND bp.dep_date > sysdate;
  IF vBoardingPassCount > 0 THEN
    raise child_exists;
  Else  
    delete from CUSTOMER where cust_id = pCustomer_id;
  END IF;
exception
   when child_exists  then
      raise_application_error(-20010, ': Cannot delete customer. Boarding pass issued'); 
end;

/

create or replace
procedure sp_delete_ticket(pTicket_no number) as
cursor boardingpass is select seg_no,class_id from boardingpass where ticket_no = pTicket_no;
begin
  FOR eachpass in boardingpass
    LOOP
      dbms_output.put_line(eachpass.seg_no);
      update seats_avail set seats_booked = seats_booked - 1 where seg_no =  eachpass.seg_no and class_id = eachpass.class_id;
    END LOOP;
  delete from boardingpass where ticket_no = pTicket_no;
  delete from ticket where ticket_no = pTicket_no;
exception 
  WHEN OTHERS THEN
	raise_application_error(-20013, SQLERRM);
END;

/