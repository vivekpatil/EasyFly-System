package models;

import com.avaje.ebean.Ebean;
import com.avaje.ebean.SqlRow;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import javax.persistence.*;
import play.db.ebean.*;

@Entity
public class Ticket extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "TicketSeq")
    public Integer ticket_no;
    public Integer fare;
    public Date issue_date;
    public String cust_id;
    public String pass_surname;
    public String pass_inits;
    public String pass_title;
    public String pass_phone;
    public String staff_ind;
    
    @OneToMany(mappedBy = "ticket")
    public List<BoardingPass> boardingPasses;
    
    public static Finder<Integer, Ticket> find = new Finder(Integer.class, Ticket.class);
    public static Connection connection = play.db.DB.getConnection();
    public static CallableStatement callableStatement = null;

    public Integer getTicket_no() {
        return ticket_no;
    }

    public void setTicket_no(Integer ticket_no) {
        this.ticket_no = ticket_no;
    }

    public Ticket(Integer fare, Date issue_date, String cust_id, String pass_surname, String pass_inits, String pass_title, String pass_phone) {
        this.fare = fare;
        this.issue_date = issue_date;
        this.cust_id = cust_id;
        this.pass_surname = pass_surname;
        this.pass_inits = pass_inits;
        this.pass_title = pass_title;
        this.pass_phone = pass_phone;
        this.staff_ind = "N";
    }
    
    public static List<Ticket> all() {
        return find.all();
    }

    public static void create(Ticket ticket) throws PersistenceException {
        ticket.save();
    }
    
    public static void createBoardingPass(Integer ticket_no, String class_id, Integer seg_no, Integer route_seg_no) throws SQLException {
        String storedProc = "{call sp_insert_boarding_pass(?,?,?,?)}";
        callableStatement = connection.prepareCall(storedProc);
        callableStatement.setInt(1, ticket_no);
        callableStatement.setString(2, class_id);
        callableStatement.setInt(3, seg_no);
        callableStatement.setInt(4, route_seg_no);
        callableStatement.executeUpdate();        
    }
    
    public static List<SqlRow> findFullTicket(Integer ticket_no) {
        return Ebean.createSqlQuery("select dep_date, airpt_id_from, airpt_id_to, b.route_id, dep_time, arr_time, class_id, bpass_no from boardingpass b inner join route_seg r on r.seg_no = b.route_seg_no where ticket_no=:no")
                            .setParameter("no", ticket_no).findList();
    }

    public static void deleteTicket(Integer id) throws SQLException {
        String storedProc = "{call sp_delete_ticket(?)}";
        callableStatement = connection.prepareCall(storedProc);
        callableStatement.setInt(1, id);
        callableStatement.executeUpdate();
    }
}