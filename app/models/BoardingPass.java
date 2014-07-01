package models;

import java.util.Date;
import javax.persistence.*;
import play.db.ebean.*;

@Entity
@Table(name = "boardingpass")
public class BoardingPass extends Model {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BoardingPassSeq")
    public Integer bpass_no;
    public String class_id;
    public Date dep_date;
    public String route_id;
    public Integer seg_no;
    
    @ManyToOne
    @JoinColumn(name="ticket_no")
    public Ticket ticket;

    public Integer getBpass_no() {
        return bpass_no;
    }

    public void setBpass_no(Integer bpass_no) {
        this.bpass_no = bpass_no;
    }
        
    public static Finder<String, BoardingPass> find = new Finder(String.class, BoardingPass.class);

}