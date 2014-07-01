package models;

import java.sql.Date;

public class TicketInfo {
    public Date dep_date;
    public String airpt_id_from;
    public String airpt_id_to;
    public String route_id;
    public Integer dep_time;
    public Integer arr_time;
    public String class_id;
    public Integer bpass_no;

    public TicketInfo(Date dep_date, String airpt_id_from, String airpt_id_to, String route_id, Integer dep_time, Integer arr_time, String class_id, Integer bpass_no) {
        this.dep_date = dep_date;
        this.airpt_id_from = airpt_id_from;
        this.airpt_id_to = airpt_id_to;
        this.route_id = route_id;
        this.dep_time = dep_time;
        this.arr_time = arr_time;
        this.class_id = class_id;
        this.bpass_no = bpass_no;
    }

}