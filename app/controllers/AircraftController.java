package controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import javax.persistence.PersistenceException;
import models.Aircraft;
import models.AircraftType;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.*;
import views.html.aircraft.*;


public class AircraftController extends Controller {

    static Form<Aircraft> aircraftForm = Form.form(Aircraft.class);
    static final HashMap<String, String> errorMessages = new HashMap<String, String>() {
        {
            put("ORA-20005", "Cannot delete aircraft. A flight exists for this aircraft.");
            put("AIRC_PK", "Cannot create aircraft. This aircraft already exists.");
        }
    };

    public static Result index() {
        return ok(aircraft_index.render(Aircraft.all()));
    }

    public static Result create() {
        return ok(aircraft_create.render(aircraftForm, AircraftType.all()));
    }

    public static Result save() {
        Form<Aircraft> filledForm = aircraftForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            return badRequest(aircraft_create.render(filledForm, AircraftType.all()));
        }
        Aircraft aircraft = filledForm.get();
        if (aircraft.seatsInvalid()) {
            ArrayList<ValidationError> errorList = new ArrayList<ValidationError>();
            errorList.add(new ValidationError("", "Total seats must be less than the maximum seats allowed for this aircraft type."));
            filledForm.errors().put("seats_qty_B", errorList);            
            flash("error", "There were errors in the form:");
            return badRequest(aircraft_create.render(filledForm, AircraftType.all()));
        }

        try {
            Aircraft.create(filledForm.get());
        } catch (PersistenceException e) {
            if (e.getMessage().contains("AIRC_PK")) {
                flash("error", "Cannot create aircraft. This aircraft already exists.");
            } else {
                flash("error", "Cannot create airline. A database error occured.");
            }
            return badRequest(aircraft_create.render(filledForm, AircraftType.all()));
        }
        return ok(aircraft_index.render(Aircraft.all()));
    }

    public static Result edit(String id) {
        Aircraft aircraft = Aircraft.find.ref(id);
        return ok(aircraft_edit.render(aircraft, aircraftForm.fill(aircraft), AircraftType.all()));
    }

    public static Result update(String id) {
        Form<Aircraft> filledForm = aircraftForm.bindFromRequest();
        Aircraft aircraft = Aircraft.find.ref(id);
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            return badRequest(aircraft_edit.render(aircraft, filledForm, AircraftType.all()));
        }
        Aircraft newAircraft = filledForm.get();
        if (newAircraft.seatsInvalid()) {
            ArrayList<ValidationError> errorList = new ArrayList<ValidationError>();
            errorList.add(new ValidationError("", "Total seats must be less than the maximum seats allowed for this aircraft type."));
            filledForm.errors().put("seats_qty_B", errorList);            
            flash("error", "There were errors in the form:");
            return badRequest(aircraft_edit.render(aircraft, filledForm, AircraftType.all()));
        }

        Aircraft.update(filledForm.get().aircraft_id, filledForm.get().aircraft_name, filledForm.get().aircr_type_id, filledForm.get().seats_qty_F, filledForm.get().seats_qty_B, filledForm.get().seats_qty_E);
        return ok(aircraft_index.render(Aircraft.all()));
    }
    
    public static Result delete(String id) {
        try {
            Aircraft.deleteAircraft(id);
        } catch (SQLException e) {
            flash("error", errorMessages.get(e.getMessage().substring(0, 9)));
        }
        return ok(aircraft_index.render(Aircraft.all()));
    }

}
