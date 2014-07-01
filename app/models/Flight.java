package models;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.Min;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Flight extends Model {

    @Id
    @Required(message = "Flight ID is required.")
    @Max(value = 99, message = "Flight ID cannot be more than 99")
    @Min(value = 1, message = "Flight ID cannot be less than 1")
    public Integer flight_id;
    @Required(message = "Route ID is required.")
    @MaxLength(value = 5, message = "Route ID cannot be more than 5 characters")
    public String route_id;
    @Required(message = "Departure date is required.")
    public Date dep_date;
    @Required(message = "Arrival time is required.")
    @Max(value = 2400, message = "Arrival time cannot be more than 2400.")
    @Min(value = 0, message = "Arrival time cannot be less then 0.")
    public Integer arr_time;
    @Required(message = "Departure time is required.")
    @Max(value = 2400, message = "Departure time cannot be more than 2400.")
    @Min(value = 0, message = "Departure time cannot be less then 0.")
    public Integer dep_time;
    @Required(message = "Aircraft ID is required.")
    @MaxLength(value = 6, message = "Aircraft ID cannot be more than 6 characters")
    public String aircraft_id;
    public String aircr_type_id;
    
    @OneToMany(mappedBy = "flight")
    public List<FlightSegment> segments;
    
    public static Model.Finder<Integer, Flight> find = new Model.Finder(Integer.class, Flight.class);
            
    public static Connection connection = play.db.DB.getConnection();
    public static CallableStatement callableStatement = null;

    public Flight(Integer flight_id, String route_id, Date dep_date, Integer arr_time, Integer dep_time, String aircraft_id, String aircr_type_id) {
        this.flight_id = flight_id;
        this.route_id = route_id;
        this.dep_date = dep_date;
        this.arr_time = arr_time;
        this.dep_time = dep_time;
        this.aircraft_id = aircraft_id;
        this.aircr_type_id = aircr_type_id;
    }

    public static List<Flight> all() {
        return find.all();
    }

    public static void create(Flight flight) throws SQLException {
        String storedProc = "{call sp_ADD_FLIGHT(?,?,?,?,?,?)}";
        callableStatement = connection.prepareCall(storedProc);
        callableStatement.setString(1, flight.route_id);
        callableStatement.setDate(2, new java.sql.Date(flight.dep_date.getTime()));
        callableStatement.setInt(3, flight.arr_time);
        callableStatement.setInt(4, flight.dep_time);
        callableStatement.setString(5, flight.aircraft_id);
        callableStatement.setInt(6, flight.flight_id);
        callableStatement.executeUpdate();
    }

    public static void update(Integer flight_id, String route_id, Date dep_date, Integer arr_time, Integer dep_time, String aircraft_id) throws SQLException {
        String storedProc = "{call sp_Update_FLIGHT_Details(?,?,?,?,?,?)}";
        callableStatement = connection.prepareCall(storedProc);
        callableStatement.setString(1, route_id);
        callableStatement.setDate(2, new java.sql.Date(dep_date.getTime()));
        callableStatement.setInt(3, arr_time);
        callableStatement.setInt(4, dep_time);
        callableStatement.setString(5, aircraft_id);
        callableStatement.setInt(6, flight_id);
        callableStatement.executeUpdate();
    }

    public static void deleteFlight(Integer flight_id) throws SQLException {
        String storedProc = "{call SP_DELETE_FLIGHT(?)}";
        callableStatement = connection.prepareCall(storedProc);
        callableStatement.setInt(1, flight_id);
        callableStatement.executeUpdate();
    }
}
