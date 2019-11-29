package DB;

import BL.Article;
import BL.Cart;
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
        PreparedStatement ps = conn.prepareStatement("SELECT COUNT(customerid)"
                                                    + "FROM cart"
                                                    + "WHERE customerid = ?");
        ps.setInt(1, customerid);
        
        ResultSet rs = ps.executeQuery();
        rs.next();
        if(rs.getInt("count")== 1)
        {
            int cartid = -1;
            
            ps = conn.prepareStatement("SELECT cartid"
                                     + "FROM cart"
                                     + "WHERE customerid = ?");
            ps.setInt(1, cartid);
            rs=ps.executeQuery();
            rs.next();
            cartid = rs.getInt("cartid");
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
    
    public void updateAmount(int customerid, int amount, int articlenr) throws SQLException {
        PreparedStatement ps1 = conn.prepareStatement("SELECT cartid "
                                                    + "FROM cart INNER JOIN customer ON customerid = ?");
        ps1.setInt(1,customerid);
        ResultSet rs1 = ps1.executeQuery();
        rs1.next();
        PreparedStatement ps2 = conn.prepareStatement("UPDATE cartposition "
                                                    + "SET amount = ? "
                                                    + "WHERE cartid = ? AND articlenr = ?");
        ps2.setInt(1,amount);
        ps2.setInt(2,rs1.getInt("cartid"));
        ps2.setInt(3,articlenr);
        ps2.execute();
    }
    public ArrayList<Article> getArticles() throws SQLException, ClassNotFoundException
    {
        String sql = "SELECT *"
                   + "FROM article";
        
        PreparedStatement state = conn.prepareStatement(sql);
        
        ResultSet rs = state.executeQuery();
        
        ArrayList<Article> articles = new ArrayList<>();
        
        while(rs.next())
        {
            articles.add(new Article(rs.getInt("articleid"), rs.getString("artname"), rs.getDouble("price")));
        }
        
        return articles;
    }
}
