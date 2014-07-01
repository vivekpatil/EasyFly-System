package models;

import java.sql.Date;

public class FlightInfo {
    public Integer flight_id;
    public String route_id;
    public Date dep_date;
    public Integer dep_time;
    public Integer arr_time;
    public Integer seats_avail;

    public FlightInfo(Integer flight_id, String route_id, Date dep_date, Integer dep_time, Integer arr_time, Integer seats_avail) {
        this.flight_id = flight_id;
        this.route_id = route_id;
        this.dep_date = dep_date;
        this.dep_time = dep_time;
        this.arr_time = arr_time;
        this.seats_avail = seats_avail;
    }
    
}