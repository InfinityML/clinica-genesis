package com.clinica.controlador;

import com.clinica.dao.CitaDAO;
import com.clinica.dao.MedicoDAO;
import com.clinica.dao.PacienteDAO;
import com.clinica.dao.impl.CitaDAOImpl;
import com.clinica.dao.impl.MedicoDAOImpl;
import com.clinica.dao.impl.PacienteDAOImpl;
import com.clinica.modelo.Usuario;
import com.clinica.util.Roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AgendaRecepcionServlet", urlPatterns = {"/AgendaRecepcionServlet"})
public class AgendaRecepcionServlet extends HttpServlet {

    private final CitaDAO citaDAO = new CitaDAOImpl();
    private final MedicoDAO medicoDAO = new MedicoDAOImpl();
    private final PacienteDAO pacienteDAO = new PacienteDAOImpl();

    private boolean esRecepcion(Usuario u) {
        return u != null && u.getIdRol() == Roles.RECEPCIONISTA;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        if (user == null) { response.sendRedirect("index.jsp"); return; }
        if (!esRecepcion(user)) { response.sendRedirect("DashboardServlet"); return; }

        request.setAttribute("pacientes", pacienteDAO.listar());
        request.setAttribute("medicos", medicoDAO.listar());
        request.setAttribute("citas", citaDAO.listarTodas());
        request.getRequestDispatcher("agenda_recepcion.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        if (!esRecepcion(user)) { response.sendRedirect("index.jsp"); return; }

        try {
            int idPaciente = Integer.parseInt(request.getParameter("idPaciente"));
            int idMedico = Integer.parseInt(request.getParameter("idMedico"));
            String fecha = request.getParameter("fecha");
            String hora = request.getParameter("hora");
            String modalidad = request.getParameter("modalidad");
            citaDAO.registrarCita(idPaciente, idMedico, fecha, hora, modalidad);
        } catch (NumberFormatException e) {
            System.err.println("AgendaRecepcion: datos inválidos - " + e.getMessage());
        }
        response.sendRedirect("AgendaRecepcionServlet");
    }
}