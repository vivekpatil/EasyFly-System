package models;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;
import javax.persistence.*;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.*;

@Entity
public class Airline extends Model {

    @Id
    @Required(message = "Airline ID is required.")
    @MaxLength(value = 2, message = "Airline Id cannot be more than 2 characters")
    public String airln_id;
    @Required(message = "Airline name is required.")
    @MaxLength(value = 30, message = "Airline name cannot be more than 30 characters")
    public String airln_name;
    public static Finder<String, Airline> find = new Finder(String.class, Airline.class);

    public Airline(String airln_id, String airln_name) {
        this.airln_id = airln_id;
        this.airln_name = airln_name;
    }

    public static List<Airline> all() {
        return find.all();
    }

    public static void create(Airline airline) throws PersistenceException {
        airline.save();
    }

    public static void update(String airln_id, String airln_name) {
        Airline airline = find.ref(airln_id);
        airline.airln_name = airln_name;
        airline.update();
    }
    
    public void remove() throws SQLException {
        Connection connection = null;
        CallableStatement callableStatement = null;
        connection = play.db.DB.getConnection();
        String storedProc = "{call sp_delete_airline(?)}";
        callableStatement = connection.prepareCall(storedProc);
        callableStatement.setString(1, airln_id);
        callableStatement.executeUpdate();
    }
}