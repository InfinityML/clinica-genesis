package com.clinica.controlador;

import com.clinica.dao.CitaDAO;
import com.clinica.dao.MedicoDAO;
import com.clinica.util.TeleconsultaUtil;
import java.util.List;
import java.util.Map;
import com.clinica.dao.impl.CitaDAOImpl;
import com.clinica.dao.impl.MedicoDAOImpl;
import com.clinica.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "CitaServlet", urlPatterns = {"/CitaServlet"})
public class CitaServlet extends HttpServlet {

    private CitaDAO citaDAO = new CitaDAOImpl();
    private MedicoDAO medicoDAO = new MedicoDAOImpl(); // Reutilizamos tu DAO de médicos

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        
        // Solo los pacientes (Rol 4) pueden entrar aquí
        if (user == null || user.getIdRol() != 4) {
            response.sendRedirect("DashboardServlet");
            return;
        }

        // Enviamos la lista de médicos para el formulario y el historial del paciente
        request.setAttribute("medicos", medicoDAO.listar());
        
        List<Map<String, Object>> misCitas = citaDAO.listarMisCitas(user.getIdUsuario());
        for (Map<String, Object> c : misCitas) {
            int conf = (c.get("confirmada") == null) ? 0 : (Integer) c.get("confirmada");
            String est = TeleconsultaUtil.estadoUnion(
                    (String) c.get("modalidad"), (String) c.get("estado"), conf,
                    (java.sql.Date) c.get("fecha"), (java.sql.Time) c.get("hora"));
            c.put("estadoUnion", est);
        }
        request.setAttribute("misCitas", misCitas);
        
        request.getRequestDispatcher("solicitar_cita.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        
        // 1. Obtenemos el ID real del paciente a partir de su cuenta de usuario
        int idPaciente = citaDAO.obtenerIdPacientePorUsuario(user.getIdUsuario());
        
        // 2. Capturamos los datos del formulario
        int idMedico = Integer.parseInt(request.getParameter("idMedico"));
        String fecha = request.getParameter("fecha");
        String hora = request.getParameter("hora");
        String modalidad = request.getParameter("modalidad");
        
        // 3. Registramos la cita
        if (idPaciente > 0) {
            citaDAO.registrarCita(idPaciente, idMedico, fecha, hora, modalidad);
        }
        
        // Recargamos la página para ver la cita nueva en la tabla
        response.sendRedirect("CitaServlet");
    }
}