package controllers;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import models.Airport;
import models.Customer;
import models.Flight;
import models.FlightInfo;
import models.FlightSegment;
import models.Ticket;
import models.TicketInfo;
import play.Logger;
import play.data.DynamicForm;
import play.mvc.*;
import views.html.booking.*;

public class BookingController extends Controller {

    public static Result index() {
        return ok(booking_index.render(Ticket.all()));
    }

    public static Result create() {
        return ok(booking_create.render(Airport.all(), Customer.all()));
    }

    public static Result save() {
        
        DynamicForm form = DynamicForm.form().bindFromRequest();
        Customer customer = Customer.find.ref(form.get("customer"));
        Ticket ticket = new Ticket(1000, new java.util.Date(), customer.cust_id, customer.cust_surname, 
                customer.cust_inits, customer.cust_title, customer.cust_phone);
        Flight flight = Flight.find.ref(Integer.parseInt(form.get("flight_id")));
        // don't allow booking if seats are not available
        
//        Ebean.beginTransaction();
        Ticket.create(ticket);
        for (FlightSegment seg : flight.segments) {
            try {
                Ticket.createBoardingPass(ticket.ticket_no, form.get("class_id"), seg.seg_no, seg.route_seg_no);
            } catch (SQLException ex) {
//                Ebean.rollbackTransaction();
                flash("error", "Cannot create booking. A database error occurred: " + ex.getMessage());
                return badRequest(booking_index.render(Ticket.all()));
            }
        }
//        Ebean.commitTransaction();
        return ok();
    }
    
    public static Result flights() {
        DynamicForm form = DynamicForm.form().bindFromRequest();
        List<SqlRow> rows = Ebean.createSqlQuery("select unique f.flight_id, f.route_id, f.dep_date, f.dep_time, f.arr_time, s.seats_avail - s.seats_booked as seats from route r, flight f,  flight_seg seg, seats_avail s where r.route_id = f.route_id and f.flight_id = seg.flight_id and seg.seg_no = s.seg_no and r.route_id = s.route_id and r.airpt_id_from = :origin and r.airpt_id_to = :destination and class_id = :class")
                            .setParameter("origin", form.get("origin")).setParameter("destination", form.get("destination"))
                            .setParameter("class", form.get("class_id")).findList();
        if (rows.isEmpty()) {
            return ok(no_flights.render());
        }
        List<FlightInfo> flightInfos = new ArrayList<FlightInfo>();
        for (SqlRow row : rows) {
            flightInfos.add(new FlightInfo(row.getInteger("flight_id"), row.getString("route_id"), 
                    row.getDate("dep_date"), row.getInteger("dep_time"), row.getInteger("arr_time"), 
                    row.getInteger("seats")));
        }
        return ok(flights.render(flightInfos));
    }
    
    public static Result view(Integer ticket_no) {
        List<SqlRow> rows = Ticket.findFullTicket(ticket_no);
        List<TicketInfo> ticketInfos = new ArrayList<TicketInfo>();
        for (SqlRow row : rows) {
            ticketInfos.add(new TicketInfo(row.getDate("dep_date"), row.getString("airpt_id_from"), 
                    row.getString("airpt_id_to"), row.getString("route_id"), row.getInteger("dep_time"), 
                    row.getInteger("arr_time"), row.getString("class_id"), row.getInteger("bpass_no")));
        }
        return ok(booking_view.render(Ticket.find.ref(ticket_no), ticketInfos));
    }
    
    public static Result delete(Integer id) {
        try {
            Ticket.deleteTicket(id);
        } catch (SQLException e) {
            flash("error", "An unexpected error occured: " + e.getMessage());
        }
        return ok(booking_index.render(Ticket.all()));
    }

}

