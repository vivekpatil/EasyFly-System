package models;

import java.util.*;
import javax.persistence.*;
import play.Logger;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Min;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

@Entity
@Table(name = "route_seg")
@SequenceGenerator(name = "RouteSegmentSeq", sequenceName="RouteSegmentSeq", allocationSize = 1)
public class RouteSegment extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RouteSegmentSeq")
    public Integer seg_no;
    
    @Required(message = "Arrival time is required.")
    @Max(value = 2400, message = "Arrival time cannot be more than 2400.")
    @Min(value = 0, message = "Arrival time cannot be less then 0.")
    public Integer arr_time;
    @Required(message = "Departure time is required.")
    @Max(value = 2400, message = "Departure time cannot be more than 2400.")
    @Min(value = 0, message = "Departure time cannot be less then 0.")
    public Integer dep_time;
    @Required(message = "Destination airport is required.")
    @MaxLength(value = 3, message = "Destination airport cannot be more than 3 characters.")
    public String airpt_id_to;
    @Required(message = "Source airport is required.")
    @MaxLength(value = 3, message = "Source airport cannot be more than 3 characters.")
    public String airpt_id_from;
    
    @ManyToOne
    @JoinColumn(name="route_id")
    public Route route;
    
    public static Finder<Integer, RouteSegment> find = new Finder(Integer.class, RouteSegment.class);

    public static List<RouteSegment> all() {
        return find.all();
    }

    private RouteSegment(Integer arr_time, Integer dep_time, String airpt_id_to, String airpt_id_from, Route route) {
        this.arr_time = arr_time;
        this.dep_time = dep_time;
        this.airpt_id_from = airpt_id_from;
        this.airpt_id_to = airpt_id_to;
        this.route = route;
    }

    public Integer getSeg_no() {
        return seg_no;
    }

    public void setSeg_no(Integer seg_no) {
        this.seg_no = seg_no;
    }
    
    public static void create(Integer arr_time, Integer dep_time, String airpt_id_to, String airpt_id_from, Route route) {
        RouteSegment segment = new RouteSegment(arr_time, dep_time, airpt_id_to, airpt_id_from, route);
        segment.save();
    }
    
    public static void update(Integer seg_no, Integer arr_time, Integer dep_time, String airpt_id_to, String airpt_id_from) {
        RouteSegment segment = find.ref(seg_no);
        segment.arr_time = arr_time;
        segment.dep_time = dep_time;
        segment.airpt_id_to = airpt_id_to;
        segment.airpt_id_from = airpt_id_from;
        segment.update();
    }
}