package models;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Airport extends Model{

    @Id
    @Required(message = "Airport Id is required.")
    @MaxLength(value = 3, message = "Airport Id cannot be more than 3 characters")
    public String airpt_id;
    @MaxLength(value = 30, message = "Airport name cannot be more than 30 characters")
    @Required(message = "Airport name is required.")
    public String airpt_name;
    @Required(message = "Country is required.")
    @MaxLength(value = 30, message = "Country cannot be more than 30 characters")
    public String country;
    public static Model.Finder<String, Airport> find = new Model.Finder(String.class, Airport.class);

    public Airport(String airpt_id, String airpt_name, String country) {
        this.airpt_id = airpt_id;
        this.airpt_name = airpt_name;
        this.country = country;
    }

    public static List<Airport> all() {
        return find.all();
    }

    public static void create(Airport airport) throws PersistenceException {
        airport.save();
    }

    public static void update(String airpt_id, String airpt_name, String country) {
        Airport airport = find.ref(airpt_id);
        airport.airpt_name = airpt_name;
        airport.country = country;
        airport.update();
    }

    public void remove() throws SQLException {
        Connection connection = null;
        CallableStatement callableStatement = null;
        connection = play.db.DB.getConnection();
        callableStatement = connection.prepareCall("{call sp_delete_airport(?)}");
        callableStatement.setString(1, airpt_id);
        callableStatement.executeUpdate();
    }
}
