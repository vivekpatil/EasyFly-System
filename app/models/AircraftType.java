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
public class AircraftType extends Model{
    
    @Id
    @Required(message = "Aircraft type ID is required.")
    @MaxLength(value = 6, message = "Airline Id cannot be more than 6 characters")
    public String aircr_type_id;
    @Required(message = "Aircraft type is required.")
    @MaxLength(value = 30, message = "Aircraft type name cannot be more than 30 characters")
    public String aircraft_type;
    @Required(message = "Departure time is required.")
    @Max(value=999, message = "Maximum number of seats cannot be more than 999.")
    public Integer maximum_seats;
    
    public static Finder<String, AircraftType> find = new Finder(String.class, AircraftType.class);

    public AircraftType(String aircr_type_id, String aircraft_type, Integer maximum_seats) {
        this.aircr_type_id = aircr_type_id;
        this.aircraft_type = aircraft_type;
        this.maximum_seats = maximum_seats;
    }
    
    public static List<AircraftType> all() {
        return find.all();
    }
    
    public static void create(AircraftType aircraftType) throws PersistenceException {
        aircraftType.save();
    }

    public static void update(String aircr_type_id, String aircraft_type, Integer maximum_seats) {
        AircraftType aircraftType = find.ref(aircr_type_id);
        aircraftType.aircraft_type = aircraft_type;
        aircraftType.maximum_seats = maximum_seats;
        aircraftType.update();
    }
    
    public static void deleteAircraftType(String aircr_type_id) throws SQLException {
        Connection connection = null;
        CallableStatement callableStatement = null;
        connection = play.db.DB.getConnection();
        String storedProc = "{call SP_DELETE_AIRCRAFT_TYPE(?)}";
        callableStatement = connection.prepareCall(storedProc);
        callableStatement.setString(1, aircr_type_id);
        callableStatement.executeUpdate();
    }
}
