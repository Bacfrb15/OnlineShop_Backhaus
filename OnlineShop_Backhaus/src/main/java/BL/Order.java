package BL;

import java.sql.Date;

/**
 *
 * @author franz
 */
public class Order {
    private int orderid;
    private int customerid;
    private Date date;

    public Order(int orderid, int customerid, Date date) {
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
