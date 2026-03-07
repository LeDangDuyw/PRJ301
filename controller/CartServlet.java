/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */

package controller;

import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import Model.CartItem;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author Personal
 */

@WebServlet("/cart")
public class CartServlet extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet CartServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CartServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }
      private void calculateTotal(HttpServletRequest request) {
        List<CartItem> cart = getCart(request);
        double total = 0;
        for (CartItem item : cart) {
            total += item.getSubtotal();
        }
        request.setAttribute("cartTotal", total);
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
         calculateTotal(request);
        request.getRequestDispatcher("/cart.jsp").forward(request, response);
      }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
     @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        switch (action == null ? "" : action) {
            case "addToCart":
                addToCart(request, response);
                break;
            case "updateQuantity":
                updateQuantity(request, response);
                break;
            case "removeItem":
                removeItem(request, response);
                break;
            default:
                response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    @SuppressWarnings("unchecked")
    private List<CartItem> getCart(HttpServletRequest request) {
        HttpSession session = request.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute("cart", cart);
        }
        return cart;
    }
private void addToCart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<CartItem> cart = getCart(request);

        int productId = Integer.parseInt(request.getParameter("productId"));
        String productName = request.getParameter("productName");
        String image = request.getParameter("image");
        double price = Double.parseDouble(request.getParameter("price"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));
        int stock = Integer.parseInt(request.getParameter("stock"));

        boolean found = false;
        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                item.setQuantity(Math.min(item.getQuantity() + quantity, stock));
                found = true;
                break;
            }
        }

        if (!found) {
            cart.add(new CartItem(productId, productName, image, price, quantity, stock));
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }
 private void updateQuantity(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<CartItem> cart = getCart(request);

        int productId = Integer.parseInt(request.getParameter("productId"));
        int quantity = Integer.parseInt(request.getParameter("quantity"));

        for (CartItem item : cart) {
            if (item.getProductId() == productId) {
                if (quantity <= 0) {
                    item.setQuantity(1);
                } else if (quantity > item.getStock()) {
                    item.setQuantity(item.getStock());
                } else {
                    item.setQuantity(quantity);
                }
                break;
            }
        }

        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void removeItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        List<CartItem> cart = getCart(request);
        int productId = Integer.parseInt(request.getParameter("productId"));
        cart.removeIf(item -> item.getProductId() == productId);
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
