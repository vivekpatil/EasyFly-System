package controllers;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import javax.persistence.PersistenceException;
import models.AircraftType;
import play.data.Form;
import play.mvc.*;
import views.html.aircraftType.*;

public class AircraftTypeController extends Controller {

    static Form<AircraftType> aircraftTypeForm = Form.form(AircraftType.class);
    static final HashMap<String, String> errorMessages = new HashMap<String, String>() {
        {
            put("ORA-20004", "Cannot delete aircraft type. A aircraft exists for this aircraft type.");
            put("AIRT_PK", "Cannot create aircraft type. This aircraft type already exists.");
        }
    };

    public static Result index() {
        return ok(aircraftType_index.render(AircraftType.all()));
    }

    public static Result create() {
        return ok(aircraftType_create.render(aircraftTypeForm));
    }

    public static Result save() {
        Form<AircraftType> filledForm = aircraftTypeForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            return badRequest(aircraftType_create.render(filledForm));
        } else {
            try {
                AircraftType.create(filledForm.get());
            } catch (PersistenceException e) {
                if (e.getMessage().contains("AIRT_PK")) {
                    flash("error", "Cannot create aircraft type. This aircraft type already exists.");
                } else {
                    flash("error", "Cannot create airline. A database error occured.");
                }
                return badRequest(aircraftType_create.render(filledForm));
            }
        }
        return ok(aircraftType_index.render(AircraftType.all()));
    }

    public static Result edit(String id) {
        AircraftType aircraftType = AircraftType.find.ref(id);
        return ok(aircraftType_edit.render(aircraftType, aircraftTypeForm.fill(aircraftType)));
    }

    public static Result update(String id) {
        Form<AircraftType> filledForm = aircraftTypeForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            AircraftType aircraftType = AircraftType.find.ref(id);
            return badRequest(aircraftType_edit.render(aircraftType, filledForm));
        } else {
            AircraftType.update(filledForm.get().aircr_type_id, filledForm.get().aircraft_type, filledForm.get().maximum_seats);
        }
        return ok(aircraftType_index.render(AircraftType.all()));
    }

    public static Result delete(String id) {
        try {
            AircraftType.deleteAircraftType(id);
        } catch (SQLException e) {
            flash("error", errorMessages.get(e.getMessage().substring(0, 9)));
        }
        return ok(aircraftType_index.render(AircraftType.all()));
    }
}
