package com.clinica.controlador;

import com.clinica.dao.CitaDAO;
import com.clinica.util.TeleconsultaUtil;
import com.clinica.dao.impl.CitaDAOImpl;
import com.clinica.modelo.Usuario;
import com.clinica.util.Roles;
import com.clinica.dao.AtencionDAO;
import com.clinica.dao.impl.AtencionDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Map;

@WebServlet(name = "VideollamadaServlet", urlPatterns = {"/VideollamadaServlet"})
public class VideollamadaServlet extends HttpServlet {

    private final CitaDAO citaDAO = new CitaDAOImpl();

    // Sal para que el nombre de la sala no sea adivinable a partir del id.
    private static final String SALT = "ClinicaUPN-2026-Teleconsulta-#7k9";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        if (user == null) { response.sendRedirect("index.jsp"); return; }

        int idCita;
        try { idCita = Integer.parseInt(request.getParameter("idCita")); }
        catch (Exception e) { response.sendRedirect("DashboardServlet"); return; }

        Map<String, Object> cita = citaDAO.buscarCitaParaVideollamada(idCita);
        if (cita == null) { response.sendRedirect("DashboardServlet"); return; }

        // Solo el médico o el paciente dueños de la cita pueden entrar
        boolean esMedico = user.getIdRol() == Roles.DOCTOR
                && user.getIdUsuario() == (int) cita.get("usuario_medico");
        boolean esPaciente = user.getIdRol() == Roles.PACIENTE
                && user.getIdUsuario() == (int) cita.get("usuario_paciente");

        if (!esMedico && !esPaciente) { response.sendRedirect("DashboardServlet"); return; }
        
        // Candado: requiere que el médico haya aceptado; el paciente además dentro de la ventana horaria.
        if ("Teleconsulta".equals(cita.get("modalidad"))) {
            int confirmada = (cita.get("confirmada") == null) ? 0 : (int) cita.get("confirmada");
            if (confirmada != 1) {
                response.sendRedirect(esMedico ? "AtencionServlet" : "CitaServlet?tele=pendiente");
                return;
            }
            if (esPaciente && !TeleconsultaUtil.dentroDeVentana(
                    (java.sql.Date) cita.get("fecha"), (java.sql.Time) cita.get("hora"))) {
                response.sendRedirect("CitaServlet?tele=fuera");
                return;
            }
        }

        // Sala única y difícil de adivinar por cita
        String sala = "ClinicaUPN-Cita-" + idCita + "-"
                + Integer.toHexString((idCita + SALT).hashCode());

        String nombre = esMedico ? ("Dr. " + cita.get("medico")) : String.valueOf(cita.get("paciente"));
        String volver = esMedico ? "AtencionServlet" : "CitaServlet";

        request.setAttribute("sala", sala);
        request.setAttribute("displayName", nombre);
        request.setAttribute("cita", cita);
        request.setAttribute("volverUrl", volver);
        request.setAttribute("esMedico", esMedico);
        if (esMedico) {
            AtencionDAO atencionDAO = new AtencionDAOImpl();
            request.setAttribute("medicamentos", atencionDAO.listarMedicamentos());
        }
        request.getRequestDispatcher("videollamada.jsp").forward(request, response);
    }
}