package controllers;

import java.sql.SQLException;
import java.util.HashMap;
import javax.persistence.PersistenceException;
import models.Airline;
import play.data.Form;
import play.mvc.*;
import views.html.airline.*;

public class AirlineController extends Controller {

    static Form<Airline> airlineForm = Form.form(Airline.class);
    static final HashMap<String, String> errorMessages = new HashMap<String, String>() {
        {
            put("ORA-20002", "Cannot delete airline. A route exists for this airline.");
            put("AIRL_PK", "Cannot create airline. This airline already exists.");
        }
    };

    public static Result index() {
        return ok(airline_index.render(Airline.all()));
    }

    public static Result create() {
        return ok(airline_create.render(airlineForm));
    }
    
    public static Result save() {
        Form<Airline> filledForm = airlineForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            return badRequest(airline_create.render(filledForm));
        } else {
            try {
                Airline.create(filledForm.get());
            } catch(PersistenceException e) {
                if (e.getMessage().contains("AIRL_PK")) {
                    flash("error", "Cannot create airline. This airline already exists.");
                } else {
                    flash("error", "Cannot create airline. A database error occured.");
                }
                return badRequest(airline_create.render(filledForm));
            }
        }
        return ok(airline_index.render(Airline.all()));
    }
    
    public static Result edit(String id) {
        Airline airline = Airline.find.ref(id);
        return ok(airline_edit.render(airline, airlineForm.fill(airline)));
    }

    public static Result update(String id) {
        Form<Airline> filledForm = airlineForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            Airline airline = Airline.find.ref(id);
            return badRequest(airline_edit.render(airline, filledForm));
        } else {
            Airline.update(filledForm.get().airln_id, filledForm.get().airln_name);
        }
        return ok(airline_index.render(Airline.all()));
    }

    public static Result delete(String id) {
        Airline airline = Airline.find.ref(id);
        try {
            airline.remove();
        } catch (SQLException e) {
            flash("error", errorMessages.get(e.getMessage().substring(0, 9)));
        }
        return ok(airline_index.render(Airline.all()));
    }

}
