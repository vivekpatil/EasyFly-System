package models;

import java.util.*;
import javax.persistence.*;
import play.db.ebean.*;

@Entity
@Table(name = "flight_seg")
public class FlightSegment extends Model {
    @Id
    public Integer seg_no;
    public String route_id;
    public Date dep_date;
    public Integer arr_time;
    public Integer dep_time;
    public Integer route_seg_no;
    
    @ManyToOne
    @JoinColumn(name="flight_id")
    public Flight flight;
    
    public static Finder<Integer, FlightSegment> find = new Finder(Integer.class, FlightSegment.class);

    public static List<FlightSegment> all() {
        return find.all();
    }
      
}