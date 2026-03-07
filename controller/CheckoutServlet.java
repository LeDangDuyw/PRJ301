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
import java.sql.PreparedStatement;
import dao.OrderDAO;
import dao.OrderDetailDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import Model.CartItem;
import Model.Order;
import com.sun.jdi.connect.spi.Connection;
import Model.OrderDetail;
import java.util.List;
import utils.DBContext;
/**
 *
 * @author Personal
 */
@WebServlet(name="CheckoutServlet", urlPatterns={"/CheckoutServlet"})
public class CheckoutServlet extends HttpServlet {
 private final OrderDAO orderDAO = new OrderDAO();
    private final OrderDetailDAO orderDetailDAO = new OrderDetailDAO();
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
            out.println("<title>Servlet CheckoutServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet CheckoutServlet at " + request.getContextPath () + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
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
         request.getRequestDispatcher("/public/checkout.jsp").forward(request, response);
  
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
   @Override
    @SuppressWarnings("unchecked")
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        List<CartItem> cart = (List<CartItem>) session.getAttribute("cart");

        if (cart == null || cart.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        int userId = (Integer) userIdObj;
        String receiverName = request.getParameter("receiverName");
        String phoneReceiver = request.getParameter("phoneReceiver");
        String shippingAddress = request.getParameter("shippingAddress");
        String paymentMethod = request.getParameter("paymentMethod");

        double totalAmount = 0;
        for (CartItem item : cart) {
            totalAmount += item.getSubtotal();
        }

        Order order = new Order();
        order.setUserId(userId);
        order.setReceiverName(receiverName);
        order.setPhoneReceiver(phoneReceiver);
        order.setShippingAddress(shippingAddress);
        order.setPaymentMethod(paymentMethod);
        order.setTotalAmount(totalAmount);
        order.setStatus("Pending");

        int orderId = orderDAO.insertOrder(order);

        if (orderId > 0) {
            boolean allInserted = true;

            for (CartItem item : cart) {
                OrderDetail detail = new OrderDetail();
                detail.setOrderId(orderId);
                detail.setProductId(item.getProductId());
                detail.setQuantity(item.getQuantity());
                detail.setPrice(item.getPrice());

                boolean ok = orderDetailDAO.insertOrderDetail(detail);
                if (!ok) {
                    allInserted = false;
                    break;
                }

                updateProductStock(item.getProductId(), item.getQuantity());
            }

            if (allInserted) {
                session.removeAttribute("cart");
                session.setAttribute("success", "Đặt hàng thành công!");
                response.sendRedirect(request.getContextPath() + "/orders");
                return;
            }
        }

        request.setAttribute("error", "Checkout thất bại!");
        request.getRequestDispatcher("/public/checkout.jsp").forward(request, response);
    }
  private void updateProductStock(int productId, int quantity) {
        String sql = "UPDATE Products SET stock = stock - ?, sold = sold + ? WHERE id = ? AND stock >= ?";
        try (java.sql.Connection conn = DBContext.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, quantity);
            ps.setInt(2, quantity);
            ps.setInt(3, productId);
            ps.setInt(4, quantity);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
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
