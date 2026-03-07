package controller;

import dao.UserDAO;
import model.User;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet("/change-password")
public class ChangePasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect("login");
            return;
        }

        User user = (User) session.getAttribute("user");

        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "Mật khẩu xác nhận không khớp");
            request.getRequestDispatcher("profile.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();

        boolean success = dao.changePassword(user.getId(), oldPassword, newPassword);

        if (success) {
            request.setAttribute("message", "Đổi mật khẩu thành công");
        } else {
            request.setAttribute("error", "Mật khẩu cũ không đúng");
        }

        request.getRequestDispatcher("profile.jsp").forward(request, response);
    }
}