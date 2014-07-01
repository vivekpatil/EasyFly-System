package models;

import java.io.Serializable;
import javax.persistence.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import play.data.validation.Constraints.Required;

@Embeddable
public class RouteSegmentPK implements Serializable {

    @Required
    @GeneratedValue
    public Integer seg_no;
    @Column(name = "route_id")
    public String route_id;

    RouteSegmentPK(String route_id, Integer seg_no) {
        this.route_id = route_id;
        this.seg_no = seg_no;
    }

    public int hashCode() {
        return new HashCodeBuilder(17, 31).append(seg_no).append(route_id).toHashCode();
    }

    public boolean equals(Object that) {
        if (that == null) {
            return false;
        }
        if (that == this) {
            return true;
        }
        if (that.getClass() != this.getClass()) {
            return false;
        }
        RouteSegmentPK pk = (RouteSegmentPK) that;
        return new EqualsBuilder().
                append(route_id, pk.route_id).
                append(seg_no, pk.seg_no).
                isEquals();
    }
}
