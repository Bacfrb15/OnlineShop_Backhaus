package BL;

import java.sql.Date;
import java.sql.Timestamp;

/**
 *
 * @author franz
 */
public class Order {
    private int orderid;
    private int customerid;
    private Timestamp date;

    public Order(int orderid, int customerid, Timestamp date) {
        this.orderid = orderid;
        this.customerid = customerid;
        this.date = date;
    }

    public int getOrderid() {
        return orderid;
    }

    public void setOrderid(int orderid) {
        this.orderid = orderid;
    }

    public int getCustomerid() {
        return customerid;
    }

    public void setCustomerid(int customerid) {
        this.customerid = customerid;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }
}
