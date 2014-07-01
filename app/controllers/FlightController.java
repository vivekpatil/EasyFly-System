package controllers;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import models.Aircraft;
import models.AircraftType;
import models.Flight;
import models.Route;
import play.data.Form;
import play.mvc.*;
import views.html.flight.*;


public class FlightController extends Controller {

    static Form<Flight> flightForm = Form.form(Flight.class);
    static final HashMap<String, String> errorMessages = new HashMap<String, String>() {
        {
            put("FLIT_PK", "Cannot save flight. This flight already exists.");
            put("FLIT_FOR", "Cannot save flight. Route ID does not exist.");
            put("FLSG_PK", "Cannot save flight. Please reset the Flight Segment sequence.");
            put("ORA-20006", "Cannot delete this flight. One or more tickets have already been purchased.");
        }
    };

    public static Result index() {
        return ok(flight_index.render(Flight.all()));
    }

    public static Result create() {
        return ok(flight_create.render(flightForm, Route.all(), AircraftType.all(), Aircraft.all()));
    }

    public static Result save() throws SQLException {
        Form<Flight> filledForm = flightForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            return badRequest(flight_create.render(filledForm, Route.all(), AircraftType.all(), Aircraft.all()));
        } 
        Flight flight = filledForm.get();
        if (flight.dep_date.before(new Date())) {
            flash("error", "The departure date of the flight must be after the current date");
            return badRequest(flight_create.render(filledForm, Route.all(), AircraftType.all(), Aircraft.all()));
        }
        try {
            Date dep_date = new SimpleDateFormat("M/d/y", Locale.ENGLISH).parse(filledForm.data().get("dep_date"));
            SimpleDateFormat format = new SimpleDateFormat("u");
            Route route = Route.find.ref(flight.route_id);
            if (!format.format(dep_date).equals(route.day_no.toString())) {
                flash("error", "The day of the week of the flight should match the day of the route.");
                return badRequest(flight_create.render(filledForm, Route.all(), AircraftType.all(), Aircraft.all()));
            }
        } catch (ParseException ex) {
            flash("error", "The date format is incorrect.");
            return badRequest(flight_create.render(filledForm, Route.all(), AircraftType.all(), Aircraft.all()));
        }
        try {
            Flight.create(flight);
        } catch (SQLException e) {
            String[] temp = e.getMessage().split("S1784498.");
            if (temp.length > 1) {
                temp = temp[1].split("\\) violated");
                String constraintName = temp[0];
                flash("error", errorMessages.get(constraintName));
            } else {
                flash("error", "Cannot create flight. A database error occurred: " + e.getMessage());
            }
            return badRequest(flight_create.render(filledForm, Route.all(), AircraftType.all(), Aircraft.all()));
        }
        return ok(flight_index.render(Flight.all()));
    }

    public static Result edit(Integer id) {
        Flight flight = Flight.find.ref(id);
        return ok(flight_edit.render(flight, flightForm.fill(flight), AircraftType.all(), Aircraft.all(), Route.all()));
    }

    public static Result update(Integer id){
        Form<Flight> filledForm = flightForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            Flight flight = Flight.find.ref(id);
            return badRequest(flight_edit.render(flight, filledForm, AircraftType.all(), Aircraft.all(), Route.all()));
        } else {
            try {
                Flight.update(filledForm.get().flight_id, filledForm.get().route_id, filledForm.get().dep_date, filledForm.get().arr_time, filledForm.get().dep_time, filledForm.get().aircraft_id);
            } catch (SQLException e) {
                String[] temp = e.getMessage().split("S1784498.");
                if (temp.length > 1) {
                    temp = temp[1].split("\\) violated");
                    String constraintName = temp[0];
                    flash("error", errorMessages.get(constraintName));
                } else {
                    flash("error", "Cannot save flight. A database error occurred: " + e.getMessage());
                }
            }
        }
        return ok(flight_index.render(Flight.all()));
    }
    
    public static Result delete(Integer id) {
        try {
            Flight.deleteFlight(id);
        } catch (SQLException e) {
            flash("error", errorMessages.get(e.getMessage().substring(0, 9)));
        }
        return ok(flight_index.render(Flight.all()));
    }

}
