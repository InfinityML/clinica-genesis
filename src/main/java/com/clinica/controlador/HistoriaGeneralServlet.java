package com.clinica.controlador;

import com.clinica.dao.ConsultasDAO;
import com.clinica.dao.impl.ConsultasDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "HistoriaGeneralServlet", urlPatterns = {"/HistoriaGeneralServlet"})
public class HistoriaGeneralServlet extends HttpServlet {
    private ConsultasDAO consultasDAO = new ConsultasDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.getRequestDispatcher("historia_general.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String dni = request.getParameter("dni");
        request.setAttribute("historialDNI", consultasDAO.buscarHistorialPorDNI(dni));
        request.setAttribute("dniBuscado", dni);
        request.getRequestDispatcher("historia_general.jsp").forward(request, response);
    }
}