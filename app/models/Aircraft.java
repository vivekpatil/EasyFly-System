package models;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Aircraft extends Model{
    
    @Id
    @Required(message = "Aircraft ID is required.")
    @MaxLength(value = 6, message = "Aircraft ID cannot be more than 6 characters")
    public String aircraft_id;
    @Required(message = "Aircraft name is required.")
    @MaxLength(value = 30, message = "Aircraft name cannot be more than 30 characters")
    public String aircraft_name;
    @Required(message = "Aircraft type ID is required.")
    @MaxLength(value = 6, message = "Aircraft type ID cannot be more than 30 characters")
    public String aircr_type_id;
    @Required(message = "Number of First class seats is required.")
    @Max(value=999, message = "Number of First class seats cannot be more than 999.")
    public Integer seats_qty_F;
    @Required(message = "Number of Business class seats is required.")
    @Max(value=999, message = "Number of Business class seats cannot be more than 999.")
    public Integer seats_qty_B;
    @Required(message = "Number of Economy class seats is required.")
    @Max(value=999, message = "Number of Economy class seats cannot be more than 999.")
    public Integer seats_qty_E;
    
    public static Model.Finder<String, Aircraft> find = new Model.Finder(String.class, Aircraft.class);

    public Aircraft(String aircraft_id, String aircraft_name, String aircr_type_ID, Integer seats_qty_F, Integer seats_qty_B, Integer seats_qty_E) {
        this.aircraft_id = aircraft_id;
        this.aircraft_name = aircraft_name;
        this.aircr_type_id = aircr_type_id;
        this.seats_qty_F = seats_qty_F;
        this.seats_qty_B = seats_qty_B;
        this.seats_qty_E = seats_qty_E;
    }

    public static List<Aircraft> all() {
        return find.all();
    }
    
    public static void create(Aircraft aircraft) throws PersistenceException {
        aircraft.save();
    }

    public static void update(String aircraft_id, String aircraft_name, String aircr_type_id, Integer seats_qty_F, Integer seats_qty_B, Integer seats_qty_E) {
        Aircraft aircraft = find.ref(aircraft_id);
        aircraft.aircraft_name = aircraft_name;
        aircraft.aircr_type_id = aircr_type_id;
        aircraft.seats_qty_F = seats_qty_F;
        aircraft.seats_qty_B = seats_qty_B;
        aircraft.seats_qty_E = seats_qty_E;
        aircraft.update();
    }
 
    public static void deleteAircraft(String id) throws SQLException {
        Connection connection = null;
        CallableStatement callableStatement = null;
        connection = play.db.DB.getConnection();
        String storedProc = "{call SP_DELETE_AIRCRAFT(?)}";
        callableStatement = connection.prepareCall(storedProc);
        callableStatement.setString(1, id);
        callableStatement.executeUpdate();
    }

    public boolean seatsInvalid() {
        AircraftType type = AircraftType.find.byId(aircr_type_id);
        return type.maximum_seats < (seats_qty_B + seats_qty_E + seats_qty_F);
    }
}
