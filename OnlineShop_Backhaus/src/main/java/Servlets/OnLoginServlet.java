/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Servlets;

import DB.Database;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
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
@WebServlet(name = "OnLoginServlet", urlPatterns = {"/OnLoginServlet"})
public class OnLoginServlet extends HttpServlet {

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
        String redirect = "";
        String username = request.getParameter("username");
        try {
            boolean password = PBKDF2.PBKDF2Hasher.validatePassword(request.getParameter("pass"), Database.getInstance().getPassword(username));
            
            if(password)
            {
                HttpSession http = request.getSession();
                http.setAttribute("customerid", Database.getInstance().getCustomerID(username));
                http.setMaxInactiveInterval(60*30);
                redirect = "/shop.jsp";
            }
            else
            {
                System.out.println("Fehler");
                redirect = "/login.jsp";
            }
        } catch (SQLException ex) {
            Logger.getLogger(OnLoginServlet.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Fehler");
            redirect = "/login.jsp";
        } catch (ClassNotFoundException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            Logger.getLogger(OnLoginServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        RequestDispatcher rd = getServletContext().getRequestDispatcher(redirect);
        rd.forward(request, response);
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

}
