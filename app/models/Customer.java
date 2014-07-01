
package models;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PersistenceException;
import play.data.validation.Constraints.Max;
import play.data.validation.Constraints.MaxLength;
import play.data.validation.Constraints.Required;
import play.db.ebean.Model;

@Entity
public class Customer extends Model{
    
    @Id
    @Required(message = "Customer ID is required.")
    @MaxLength(value = 6, message = "Customer ID cannot be more than 6 characters")
    public String cust_id;
    @Required(message = "Customer surname is required.")
    @MaxLength(value = 30, message = "Customer surname cannot be more than 30 characters")
    public String cust_surname;
    @Required(message = "Credit limit is required.")
    @Max(value=999999999, message = "Credit limit cannot be more than 999999999.99")
    public Integer cust_credlim;
    @Required(message = "Customer address is required.")
    @MaxLength(value = 30, message = "Customer address cannot be more than 30 characters")
    public String cust_addr1;
    @MaxLength(value = 30, message = "Customer address cannot be more than 30 characters")
    public String cust_addr2;
    @MaxLength(value = 30, message = "Customer address cannot be more than 30 characters")
    public String cust_addr3;
    @MaxLength(value = 3, message = "Customer initials cannot be more than 3 characters")
    public String cust_inits;
    @MaxLength(value = 3, message = "Customer title cannot be more than 10 characters")
    public String cust_title;
    @MaxLength(value = 15, message = "Customer phone number cannot be more than 15 characters")
    public String cust_phone;
    
    public static Finder<String, Customer> find = new Finder(String.class, Customer.class);

    public Customer(String cust_Id, String cust_Surname, Integer cust_Credlimit, String cust_Addr1, String cust_Addr2, 
                    String cust_Addr3, String cust_Initials, String cust_Title, String cust_Phone) {
        this.cust_id = cust_Id;
        this.cust_surname= cust_Surname;
        this.cust_credlim = cust_Credlimit;
        this.cust_addr1 = cust_Addr1;
        this.cust_addr2 = cust_Addr2;
        this.cust_addr3 = cust_Addr3;
        this.cust_inits = cust_Initials;
        this.cust_title = cust_Title;
        this.cust_phone = cust_Phone;
    }

    public static List<Customer> all() {
        return find.all();
    }
    
    public static void create(Customer cust) throws PersistenceException {
        cust.save();
    }
    
    public static void update(String cust_Id, String cust_Surname, Integer cust_Credlimit, String cust_Addr1, String cust_Addr2, 
                    String cust_Addr3, String cust_Initials, String cust_Title, String cust_Phone) {
        Customer cust = find.ref(cust_Id);
        cust.cust_surname= cust_Surname;
        cust.cust_credlim = cust_Credlimit;
        cust.cust_addr1 = cust_Addr1;
        cust.cust_addr2 = cust_Addr2;
        cust.cust_addr3 = cust_Addr3;
        cust.cust_inits = cust_Initials;
        cust.cust_title = cust_Title;
        cust.cust_phone = cust_Phone;
        cust.update();
    }
    
    public static void deleteCustomer(String cust_Id) throws SQLException {
        Connection connection = null;
        CallableStatement callableStatement = null;
        connection = play.db.DB.getConnection();
        String storedProc = "{call SP_DELETE_CUSTOMER(?)}";
        callableStatement = connection.prepareCall(storedProc);
        callableStatement.setString(1, cust_Id);
        callableStatement.executeUpdate();
    }
}
