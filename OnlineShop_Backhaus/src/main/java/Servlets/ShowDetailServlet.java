/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import DB.Database;
import com.google.gson.Gson;
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

/**
 *
 * @author franz
 */
@WebServlet(name = "ShowDetailServlet", urlPatterns = {"/ShowDetailServlet"})
public class ShowDetailServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            Gson gson = new Gson();
            int orderid = gson.fromJson(new InputStreamReader(request.getInputStream()), Id.class).getOrderid();
             
            String jsonAnswer = gson.toJson(Database.getInstance().showDetails(orderid));
            OutputStreamWriter osw = new OutputStreamWriter(response.getOutputStream());
            osw.write(jsonAnswer);
            osw.flush();     
        } catch (SQLException ex) {
            Logger.getLogger(ShowDetailServlet.class.getName()).log(Level.SEVERE, null, ex);
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
    
    private class Id
    {
        private int orderid;

        public Id(int orderid) {
            this.orderid = orderid;
        }

        public int getOrderid() {
            return orderid;
        }

        public void setOrderid(int orderid) {
            this.orderid = orderid;
        }
        
        
    }
}
