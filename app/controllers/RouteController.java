package controllers;

import com.avaje.ebean.Ebean;
import java.util.ArrayList;
import java.util.HashMap;
import javax.persistence.PersistenceException;
import models.Airline;
import models.Airport;
import models.Route;
import models.RouteSegment;
import play.data.Form;
import play.data.validation.ValidationError;
import play.mvc.*;
import views.html.route.*;

public class RouteController extends Controller {

    static Form<Route> routeForm = Form.form(Route.class);
    static final HashMap<String, String> errorMessages = new HashMap<String, String>() {
        {
            put("ROUT_PK", "Cannot save route. This route already exists.");
            put("ROUT_FROM", "Cannot save route. The source airport does not exist.");
            put("ROUT_TO", "Cannot save route. The destination airport does not exist.");
            put("ROUT_OPERATED_BY", "Cannot save route. The airline does not exist.");
            put("RTSG_TO", "Cannot save route segments. One of the destination airports does not exist.");
            put("RTSG_FROM", "Cannot save route segments. One of the source airports does not exist.");
            put("FLIT_FOR", "Cannot delete this route. A flight has already been scheduled for it.");
        }
    };

    public static Result index() {
        return ok(route_index.render(Route.all()));
    }

    public static Result create() {
        return ok(route_create.render(routeForm, Airline.all(), Airport.all()));
    }

    public static Result edit(String id) {
        Route route = Route.findInSegmentOrder(id);
        return ok(route_edit.render(route, routeForm.fill(route), Airline.all(), Airport.all()));
    }

    public static Result update(String id) {
        Form<Route> filledForm = routeForm.bindFromRequest();
        Route route = Route.find.ref(id);
        Route newRoute = filledForm.get();
        
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            return badRequest(route_edit.render(route, filledForm, Airline.all(), Airport.all()));
        } 
        if (newRoute.areAirportsSame()) {
            return redirectUpdateError(route, filledForm, "airpt_id_from", "Source and destination airports should be different.");
        } 
        if (!newRoute.airpt_id_from.equals(newRoute.segments.get(0).airpt_id_from)){
            return redirectUpdateError(route, filledForm, "airpt_id_from", "Source airport of the route must match the source airport of the first segment.");
        }
        if (!newRoute.airpt_id_to.equals(newRoute.segments.get(route.segments.size() - 1).airpt_id_to)){
            return redirectUpdateError(route, filledForm, "airpt_id_to", "Destination airport of the route must match the destination airport of the last segment.");
        }
        
        try {
            Route.update(route.route_id, newRoute.arr_time, newRoute.airpt_id_to,
                    newRoute.dep_time, newRoute.airpt_id_from, newRoute.airln_id, newRoute.day_no);
            for (RouteSegment segment : newRoute.segments) {
                RouteSegment.update(segment.seg_no, segment.arr_time, segment.dep_time, segment.airpt_id_to, segment.airpt_id_from);
            }
        } catch (PersistenceException e) {
            String[] temp = e.getMessage().split("S1784498.");
            if (temp.length > 1) {
                temp = temp[1].split("\\) violated");
                String constraintName = temp[0];
                flash("error", errorMessages.get(constraintName));
            } else {
                flash("error", "Cannot create route. A database error occurred: " + e.getMessage());
            }
            return badRequest(route_edit.render(newRoute, filledForm, Airline.all(), Airport.all()));
        }
        
        return ok(route_index.render(Route.all()));
    }

    public static Result delete(String id) {
        try {
            Route.find.ref(id).delete();
        } catch (Exception e){
            String[] temp = e.getMessage().split("S1784498.");
            if (temp.length > 1) {
                temp = temp[1].split("\\) violated");
                String constraintName = temp[0];
                flash("error", errorMessages.get(constraintName));
            } else {
                flash("error", "Cannot delete route. A database error occurred: " + e.getMessage());
            }
            return badRequest(route_index.render(Route.all()));
        }
        return ok(route_index.render(Route.all()));
    }

    public static Result save() {
        Form<Route> filledForm = routeForm.bindFromRequest();
        Route route = filledForm.get();
        
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            return badRequest(route_create.render(filledForm, Airline.all(), Airport.all()));
        } 
        if (route.areAirportsSame()) {
            return redirectSaveError(filledForm, "airpt_id_from", "Source and destination airports should be different.");
        }
        if (!route.airpt_id_from.equals(route.segments.get(0).airpt_id_from)){
            return redirectSaveError(filledForm, "airpt_id_from", "Source airport of the route must match the source airport of the first segment.");
        }
        if (!route.airpt_id_to.equals(route.segments.get(route.segments.size() - 1).airpt_id_to)){
            return redirectSaveError(filledForm, "airpt_id_to", "Destination airport of the route must match the destination airport of the last segment.");
        }
        
        Ebean.beginTransaction();
        try {
            Route.create(route);
            for (RouteSegment segment : route.segments) {
                RouteSegment.create(segment.arr_time, segment.dep_time, segment.airpt_id_to, segment.airpt_id_from, route);
            }
            Ebean.commitTransaction();
        } catch (PersistenceException e) {
            flash("error", "Cannot create route. A database error occurred: " + e.getMessage());
            String[] temp = e.getMessage().split("S1784498.");
            if (temp.length > 1) {
                temp = temp[1].split("\\) violated");
                String constraintName = temp[0];
                flash("error", errorMessages.get(constraintName));
            }
            Ebean.rollbackTransaction();
            return badRequest(route_create.render(filledForm, Airline.all(), Airport.all()));
        } finally {
            Ebean.endTransaction();  
        }
        
        return ok(route_index.render(Route.all()));
    }

    private static Result redirectSaveError(Form<Route> filledForm, String field, String error) {
        ArrayList<ValidationError> errorList = new ArrayList<ValidationError>();
        errorList.add(new ValidationError("", error));
        filledForm.errors().put(field, errorList);
        flash("error", "There were errors in the form:");
        return badRequest(route_create.render(filledForm, Airline.all(), Airport.all()));
    }
    
    private static Result redirectUpdateError(Route route, Form<Route> filledForm, String field, String error) {
        ArrayList<ValidationError> errorList = new ArrayList<ValidationError>();
        errorList.add(new ValidationError("", error));
        filledForm.errors().put(field, errorList);
        flash("error", "There were errors in the form:");
        return badRequest(route_edit.render(route, filledForm, Airline.all(), Airport.all()));
    }
}
