/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import BL.Article;
import DB.Database;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author franz
 */
@WebServlet(name = "OnShowArticles", urlPatterns = {"/OnShowArticles"})
public class OnShowArticles extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            
            int customerid = (int) request.getSession().getAttribute("customerid");
            int cartid = Database.getInstance().getCartID(customerid);
            
            
            
            ArrayList<Article> articles = Database.getInstance().setArticleAmount(cartid, Database.getInstance().getArticles());
            
            Gson gson = new Gson();
            String jsonEntries = gson.toJson(articles);
            
            response.setContentType("application/json");
            OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
            osw.write(jsonEntries);
            osw.flush();
            
        } catch (SQLException ex) {
            Logger.getLogger(OnShowArticles.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(OnShowArticles.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private class ArticleAmount
    {
        private int articleid;
        private double price;
        private int amount;

        public ArticleAmount(int articleid, double price, int amount) {
            this.articleid = articleid;
            this.price = price;
            this.amount = amount;
        }

        public int getArticleid() {
            return articleid;
        }

        public double getPrice() {
            return price;
        }

        public int getAmount() {
            return amount;
        }
    }
}
