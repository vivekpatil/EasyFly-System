package controllers;

import java.sql.SQLException;
import java.util.HashMap;
import javax.persistence.PersistenceException;
import models.Customer;
import play.data.Form;
import play.mvc.*;
import views.html.customer.*;


public class CustomerController extends Controller{
    
    static Form<Customer> customerForm = Form.form(Customer.class);
    
    static final HashMap<String, String> errorMessages = new HashMap<String, String>() {
        {
            put("CUST_PK", "Cannot create customer. This customer ID already exists.");
            put("ORA-20010", "Cannot delete customer. A boarding pass has been issued for this customer.");
        }
    };
    
    public static Result index() {
        return ok(customer_index.render(Customer.all()));
    }
    
    public static Result create() {
        return ok(customer_create.render(customerForm));
    }
    
    public static Result save() {
        Form<Customer> filledForm = customerForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            return badRequest(customer_create.render(filledForm));
        } else {
            try {
                Customer.create(filledForm.get());
            } catch (PersistenceException e) {
                if (e.getMessage().contains("CUST_PK")) {
                    flash("error", "Cannot create customer. This customer ID already exists.");
                } else {
                    flash("error", "Cannot create customer. A database error occured.");
                }
                return badRequest(customer_create.render(filledForm));
            }
        }
        return ok(customer_index.render(Customer.all()));
    }
    
    
    public static Result edit(String id) {
        Customer cust = Customer.find.ref(id);
        return ok(customer_edit.render(cust, customerForm.fill(cust)));
    }

    public static Result update(String id) {
        Form<Customer> filledForm = customerForm.bindFromRequest();
        if (filledForm.hasErrors()) {
            flash("error", "There were errors in the form:");
            Customer cust = Customer.find.ref(id);
            return badRequest(customer_edit.render(cust, filledForm));
        } else {
            Customer.update(filledForm.get().cust_id, filledForm.get().cust_surname, filledForm.get().cust_credlim,
                    filledForm.get().cust_addr1, filledForm.get().cust_addr2,filledForm.get().cust_addr3,
                    filledForm.get().cust_inits,filledForm.get().cust_title,filledForm.get().cust_phone);
        }
        return ok(customer_index.render(Customer.all()));
    }

    public static Result delete(String id) {
        try {
            Customer.deleteCustomer(id);  
        } catch (SQLException e) {
            flash("error", errorMessages.get(e.getMessage().substring(0, 9)));
        }
        return ok(customer_index.render(Customer.all()));
    }
}
