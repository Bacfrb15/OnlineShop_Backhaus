/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import BL.Cart;
import DB.Database;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author franz
 */
@WebServlet(name = "UpdateAmountServlet", urlPatterns = {"/UpdateAmountServlet"})
public class UpdateAmountServlet extends HttpServlet {
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
        try {
            int customerid = (int) request.getSession().getAttribute("customerid");
            int cartid = Database.getInstance().getCartID(customerid);                
            Gson gson = new Gson();
            AmountUpdate updater = gson.fromJson(new InputStreamReader(request.getInputStream()), AmountUpdate.class);
            updater.setAmount(Database.getInstance().updateAmount(cartid, updater.getAmount(), updater.getArticleid()));
            String jsonAnswer = gson.toJson(updater);
            OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
            osw.write(jsonAnswer);
            osw.flush();
        } catch (SQLException ex) {
            Logger.getLogger(UpdateAmountServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    private class AmountUpdate
    {
        private int articleid;
        private int amount;

        public AmountUpdate(int articleid, int amount) {
            this.articleid = articleid;
            this.amount = amount;
        }

        public int getArticleid() {
            return articleid;
        }

        public int getAmount() {
            return amount;
        }

        public void setArticleid(int articleid) {
            this.articleid = articleid;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
        
        
    }
}
