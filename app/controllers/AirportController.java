package controllers;

import java.sql.SQLException;
import java.util.HashMap;
import javax.persistence.PersistenceException;
import models.Airport;
import play.data.Form;
import play.mvc.*;
import views.html.airport.*;

public class AirportController extends Controller {

    static Form<Airport> airportForm = Form.form(Airport.class);
    static final HashMap<String, String> errorMessages = new HashMap<String, String>() {
        {
            put("ORA-20001", "Cannot delete airport. A route exists for this airport.");
            put("AIRP_PK", "Cannot create airport. This Airport already exists.");
        }
    };

    public static Result index() {
        return ok(airport_index.render(Airport.all()));
    }

    public static Result create() {
        return ok(airport_create.render(airportForm));
    }

    public static Result save() {
        Form<Airport> filledForm = airportForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            return badRequest(airport_create.render(filledForm));
        } else {
            try {
                Airport.create(filledForm.get());
            } catch (PersistenceException e) {
                if (e.getMessage().contains("AIRP_PK")) {
                    flash("error", "Cannot create airport. This Airport already exists.");
                } else {
                    flash("error", "Cannot create airport. A database error occured.");
                }
                return badRequest(airport_create.render(filledForm));
            }
        }
        return ok(airport_index.render(Airport.all()));

    }

    public static Result edit(String id) {
        Airport airport = Airport.find.ref(id);
        return ok(airport_edit.render(airportForm.fill(airport)));
    }

    public static Result update(String id) {
        Form<Airport> filledForm = airportForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form. All fields are required.");
            Airport airport = Airport.find.ref(id);
            return badRequest(airport_edit.render(airportForm.fill(airport)));
        } else {
            Airport.update(filledForm.get().airpt_id, filledForm.get().airpt_name, filledForm.get().country);
        }
        return ok(airport_index.render(Airport.all()));
    }

    public static Result delete(String id) {
        Airport airport = Airport.find.ref(id);
        try {
            airport.remove();
        } catch (SQLException e) {
            flash("error", errorMessages.get(e.getMessage().substring(0, 9)));
        }
        return ok(airport_index.render(Airport.all()));
    }

}
