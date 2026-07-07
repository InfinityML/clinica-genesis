package com.clinica.controlador;

import com.clinica.dao.DashboardDAO;
import com.clinica.dao.impl.DashboardDAOImpl;
import com.clinica.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "DashboardServlet", urlPatterns = {"/DashboardServlet"})
public class DashboardServlet extends HttpServlet {

    private DashboardDAO dashboardDAO = new DashboardDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        
        if (user == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // Cargamos las estadísticas solo si es Administrador (Rol 1 o 5)
        if (user.getIdRol() == 1 || user.getIdRol() == 5) {
            request.setAttribute("stats", dashboardDAO.obtenerEstadisticas());
        }
        
        request.getRequestDispatcher("dashboard.jsp").forward(request, response);
    }
}