package com.clinica.controlador;

import com.clinica.dao.ConsultasDAO;
import com.clinica.dao.impl.ConsultasDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(name = "ReportesServlet", urlPatterns = {"/ReportesServlet"})
public class ReportesServlet extends HttpServlet {
    private ConsultasDAO consultasDAO = new ConsultasDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("reporte", consultasDAO.obtenerReporteEspecialidades());
        request.getRequestDispatcher("reportes.jsp").forward(request, response);
    }
}