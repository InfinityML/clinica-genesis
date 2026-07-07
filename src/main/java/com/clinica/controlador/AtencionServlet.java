package com.clinica.controlador;

import com.clinica.dao.AtencionDAO;
import com.clinica.dao.CitaDAO;
import com.clinica.dao.impl.CitaDAOImpl;
import com.clinica.dao.impl.AtencionDAOImpl;
import com.clinica.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "AtencionServlet", urlPatterns = {"/AtencionServlet"})
public class AtencionServlet extends HttpServlet {

    private AtencionDAO atencionDAO = new AtencionDAOImpl();
    private CitaDAO citaDAO = new CitaDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        
        if (user == null || user.getIdRol() != 2) {
            // Rol 2 = Doctor
            response.sendRedirect("dashboard.jsp");
            return;
        }
        
        // El médico acepta una teleconsulta -> habilita al paciente
        if ("confirmar".equals(request.getParameter("accion"))) {
            try { citaDAO.confirmarCita(Integer.parseInt(request.getParameter("idCita"))); }
            catch (Exception ignored) {}
            response.sendRedirect("AtencionServlet");
            return;
        }

        // Cargamos la agenda de este doctor y el catálogo de medicinas
        request.setAttribute("agenda", atencionDAO.listarAgendaMedico(user.getIdUsuario()));
        request.setAttribute("medicamentos", atencionDAO.listarMedicamentos());
        
        request.getRequestDispatcher("agenda_medico.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        
        // Obtenemos los datos ocultos
        int idCita = Integer.parseInt(request.getParameter("idCita"));
        int idPaciente = Integer.parseInt(request.getParameter("idPaciente"));
        int idHistoria = Integer.parseInt(request.getParameter("idHistoria"));
        int idMedico = Integer.parseInt(request.getParameter("idMedico"));
        
        // Si no tiene historia (era 0), la creamos en el acto
        if (idHistoria == 0) {
            idHistoria = atencionDAO.crearHistoriaClinica(idPaciente);
        }

        // Obtenemos los datos médicos
        String motivo = request.getParameter("motivo");
        String diagnostico = request.getParameter("diagnostico");
        
        // Obtenemos la receta (opcional)
        String idMedStr = request.getParameter("idMedicamento");
        Integer idMedicamento = (idMedStr != null && !idMedStr.isEmpty()) ? Integer.valueOf(idMedStr) : null;
        String dosis = request.getParameter("dosis");
        String frecuencia = request.getParameter("frecuencia");
        String duracion = request.getParameter("duracion");

        // DISPARAMOS LA TRANSACCIÓN
        atencionDAO.registrarAtencion(idCita, idHistoria, motivo, diagnostico, idMedico, idMedicamento, dosis, frecuencia, duracion);
        
        // Recargamos la agenda (la cita atendida ya no aparecerá)
        response.sendRedirect("AtencionServlet");
    }
}