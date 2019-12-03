package DB;

import BL.Article;
import BL.Cart;
import BL.Order;
import BL.OrderDetail;
import BL.Position;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author franz
 */
public class Database {
    private static Database theInstance;
    private static Connection conn;
    
    
    private Database() throws SQLException
    {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
        conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/onlineshopdb", "postgres", "postgres");
        theInstance = this;
    }
    public synchronized static Database getInstance() throws SQLException
    {
        if(theInstance == null)
        {
            theInstance = new Database();
        }
        return theInstance;
    }
    public int getCustomerID(String username) throws SQLException
    {
        PreparedStatement ps = conn.prepareStatement("SELECT customerid "
                + "FROM customer "
                + "WHERE username = ? ");
        ps.setString(1, username);
        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getInt("customerid");
    }
    
    public String getPassword(String username) throws SQLException, ClassNotFoundException
    {
        PreparedStatement ps = conn.prepareStatement("SELECT password "
                + "FROM customer "
                + "WHERE username = ? ");
        ps.setString(1, username);

        ResultSet rs = ps.executeQuery();
        rs.next();
        return rs.getString("password");
    }
    
    public int getCartID(int customerid) throws SQLException
    {
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) "
                                                    +"FROM cart "
                                                    +"WHERE customerid = ? ");
        ps.setInt(1, customerid);
        ResultSet rs = ps.executeQuery();
        rs.next();
        if(rs.getInt("count")== 1)
        {
            int cartid = -1;
            
            ps = conn.prepareStatement("SELECT cartid "
                                     + "FROM cart "
                                     + "WHERE customerid = ? ");
            ps.setInt(1, customerid);
            rs = ps.executeQuery();
            while (rs.next()) {
                cartid = rs.getInt("cartid");
            }
            return cartid;
        }
        else
        {
            ps = conn.prepareStatement("INSERT INTO cart(customerid) values(?)");
            ps.setInt(1, customerid);
            ps.execute();
            return getCartID(customerid);
        }
    }
    
    public void updateCart(Cart article, int customerid) throws SQLException
    {
        int articleid = article.getArticleid();
        int amount = article.getAmount();
        updateAmount(customerid, amount, articleid);
    }
    
    public int updateAmount(int cartid, int amount, int articleid) throws SQLException {
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(*) "
                                                    + "FROM cartposition "
                                                    + "WHERE cartid = ? AND articleid = ? ");
        ps.setInt(1, cartid);
        ps.setInt(2, articleid);
        
        ResultSet rs = ps.executeQuery();
        rs.next();
        
        if(rs.getInt("count")== 1 )
        {
            ps = conn.prepareStatement("SELECT amount "
                                     + "FROM cartposition "
                                     + "WHERE cartid = ? AND articleid = ? ");
            ps.setInt(1, cartid);
            ps.setInt(2, articleid);
            rs = ps.executeQuery();
            rs.next();
            
            int newamount = rs.getInt("amount");
            
            newamount += amount;
            if(newamount >= 0)
            {
                ps = conn.prepareStatement("UPDATE cartposition SET amount=? WHERE cartid=? AND articleid=? ");
                ps.setInt(1, newamount);
                ps.setInt(2, cartid);
                ps.setInt(3, articleid);
                ps.execute();
                return newamount;
            }
            return 0;
        }
        else
        {
            ps = conn.prepareStatement("INSERT INTO cartposition(cartid,articleid,amount) VALUES(?,?,?) ");
            ps.setInt(1, cartid);
            ps.setInt(2, articleid);
            ps.setInt(3, amount);
            ps.execute();
            return amount;
        }
    }
    public ArrayList<OrderDetail> showDetails(int orderid) throws SQLException
    {
        ArrayList<OrderDetail> details = new ArrayList<>();
        
        PreparedStatement ps = conn.prepareStatement("SELECT art.artname, pos.amount, art.price "
                                                   + "FROM public.position pos INNER JOIN article art ON art.articleid = pos.articleid "
                                                   + "WHERE pos.orderid = ?");
        ps.setInt(1, orderid);
        ResultSet rs = ps.executeQuery();
        
        while(rs.next())
        {
            details.add(new OrderDetail(rs.getString("artname"), rs.getInt("amount"), rs.getDouble("price")));
        }
        
        return details;
    }
    
    public ArrayList<Order> showOrders(int customerid) throws SQLException
    {
        ArrayList<Order> orders = new ArrayList<>();
        
        PreparedStatement ps = conn.prepareStatement("SELECT * "
                                                   + "FROM public.order "
                                                   + "WHERE customerid = ? ");
        ps.setInt(1, customerid);
        ResultSet rs = ps.executeQuery();
        
        while(rs.next())
        {
            orders.add(new Order(rs.getInt("orderid"), customerid, rs.getTimestamp("date")));
        }
        return orders;
    }
    
    public void newOrder(int customerid) throws SQLException
    {
        PreparedStatement ps = conn.prepareStatement("INSERT INTO public.order(customerid) VALUES(?) ");
        ps.setInt(1,customerid);
        ps.execute();
        
        ps = conn.prepareStatement("SELECT MAX(orderid) "
                                 + "FROM public.order "
                                 + "WHERE customerid = ? ");
        ps.setInt(1, customerid);
        ResultSet rs = ps.executeQuery();
        rs.next();
        
        int orderid = rs.getInt("max");
        int cartid = getCartID(customerid);
        ps = conn.prepareStatement("SELECT articleid, amount "
                                 + "FROM cartposition "
                                 + "WHERE cartid = ? ");
        ps.setInt(1, cartid);
        rs = ps.executeQuery();
        
        while(rs.next())
        {
            ps = conn.prepareStatement("INSERT INTO position(articleid, orderid, amount) VALUES(?,?,?) ");
            ps.setInt(1, rs.getInt("articleid"));
            ps.setInt(2, orderid);
            ps.setInt(3, rs.getInt("amount"));
            ps.execute();
        }
        
        ps = conn.prepareStatement("DELETE FROM cartposition WHERE cartid = ? ");
        ps.setInt(1, cartid);
        ps.execute();
    }
    
    
    public ArrayList<Article> setArticleAmount(int cartid, ArrayList<Article> articles) throws SQLException
    {
        PreparedStatement ps;
        ResultSet rs;
        
        for(Article a : articles)
        {
            ps = conn.prepareStatement("SELECT COALESCE(c.amount,0) amount "
                                     + "FROM article a LEFT OUTER JOIN cartposition c ON a.articleid = c.articleid "
                                     + "WHERE a.articleid =  ? ");
            ps.setInt(1, a.getArticleid());
            rs = ps.executeQuery();
            rs.next();
            a.setAmount(rs.getInt("amount"));
        }
        return articles;
    }

    
    public ArrayList<Article> getArticles() throws SQLException, ClassNotFoundException
    {
        String sql = "SELECT *"
                   + "FROM article";
        
        PreparedStatement ps = conn.prepareStatement(sql);
        
        ResultSet rs = ps.executeQuery();
        
        ArrayList<Article> articles = new ArrayList<>();
        
        while(rs.next())
        {
            articles.add(new Article(rs.getInt("articleid"), rs.getString("artname"), rs.getDouble("price"), 0));
        }
        
        return articles;
    }
}
