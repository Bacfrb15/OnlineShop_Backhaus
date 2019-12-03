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
    
    /**
     * Singleton implementation of the database (private constructor)
     * @return Database instance
     * @throws SQLException 
     */
    
    public synchronized static Database getInstance() throws SQLException
    {
        if(theInstance == null)
        {
            theInstance = new Database();
        }
        return theInstance;
    }
    
    /**
     * Returns the customerid that matches the current user
     * @param username username of the current user
     * @return customerid of the current user
     * @throws SQLException 
     */
    
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
    
    /**
     * Returns the password of the current user
     * @param username username of the current user
     * @return password of the current user
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    
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
    
    /**
     * Counts all the carts from the current customer, 
     * if the current customer already owns a cart it returns the cartid,
     * if not a new cartid is created for the current customer
     * @param customerid of the current user
     * @return the cartid of the current/new cart
     * @throws SQLException 
     */
    
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

    /**
     * Counts the amount of cartpositions,
     * if one already exists in the database, either a article is added or removed
     * if not a new cartposition gets created 
     * @param cartid of the current cartposition
     * @param amount already existing amount of articles in the cart
     * @param articleid id of the articles in the cart
     * @return the amount of articles in the cartposition
     * @throws SQLException 
     */
    
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
    
    /**
     * Shows the details of the selected order
     * @param orderid of the selected order
     * @return an ArrayList of all the order details
     * @throws SQLException 
     */
    
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
    
    /**
     * Shows all the orders of the current user
     * @param customerid of the current user
     * @return an ArrayList of all the Orders of the user
     * @throws SQLException 
     */
    
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
    
    /**
     * When buying the selected articles a new order with the order details is created
     * @param customerid of the current user
     * @throws SQLException 
     */
    
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
    
    /**
     * Sets the amount of articles in the cart
     * @param cartid of the cart that is currently used by the user 
     * @param articles all articles that exist
     * @return an ArrayList with the correct amount of articles
     * @throws SQLException 
     */
    
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

    /**
     * List of all articles from the database
     * @return an ArrayList of all articles
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    
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
