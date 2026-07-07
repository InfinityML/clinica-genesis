package com.clinica.controlador;

import com.clinica.dao.HistorialDAO;
import com.clinica.dao.impl.HistorialDAOImpl;
import com.clinica.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "HistorialServlet", urlPatterns = {"/HistorialServlet"})
public class HistorialServlet extends HttpServlet {

    private HistorialDAO historialDAO = new HistorialDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        
        // Validación de seguridad: Solo Pacientes (Rol 4)
        if (user == null || user.getIdRol() != 4) {
            response.sendRedirect("DashboardServlet");
            return;
        }

        // Cargamos el historial y lo enviamos a la vista
        request.setAttribute("historial", historialDAO.obtenerMiHistorial(user.getIdUsuario()));
        request.getRequestDispatcher("historial_paciente.jsp").forward(request, response);
    }
}