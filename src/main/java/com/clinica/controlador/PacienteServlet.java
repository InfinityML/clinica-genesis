package com.clinica.controlador;

import com.clinica.dao.PacienteDAO;
import com.clinica.dao.UsuarioDAO;
import com.clinica.dao.impl.PacienteDAOImpl;
import com.clinica.dao.impl.UsuarioDAOImpl;
import com.clinica.modelo.Paciente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Date;

@WebServlet(name = "PacienteServlet", urlPatterns = {"/PacienteServlet"})
public class PacienteServlet extends HttpServlet {

    private PacienteDAO pacienteDAO = new PacienteDAOImpl();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Validación estricta de sesión
        HttpSession sesion = request.getSession();
        if (sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // Interceptar eliminaciones
        String accion = request.getParameter("accion");
        if ("eliminar".equals(accion)) {
            int idEliminar = Integer.parseInt(request.getParameter("id"));
            pacienteDAO.eliminar(idEliminar);
            response.sendRedirect("PacienteServlet");
            return;
        }

        // Cargar listas y enviar a la vista
        request.setAttribute("pacientes", pacienteDAO.listar());
        request.setAttribute("usuarios", usuarioDAO.listar());
        request.getRequestDispatcher("pacientes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        if ("crear".equals(accion)) {
            Paciente p = new Paciente();
            p.setNombre(request.getParameter("nombre"));
            p.setApellido(request.getParameter("apellido"));
            p.setDni(request.getParameter("dni"));
            p.setFechaNacimiento(Date.valueOf(request.getParameter("fechaNacimiento")));
            p.setTelefono(request.getParameter("telefono"));
            
            // Asignar ID de usuario solo si seleccionaron uno
            String idUsr = request.getParameter("idUsuario");
            p.setIdUsuario((idUsr != null && !idUsr.isEmpty()) ? Integer.valueOf(idUsr) : null);
            
            pacienteDAO.registrar(p);
            response.sendRedirect("PacienteServlet");
            
        } else if ("editar".equals(accion)) {
            Paciente p = new Paciente();
            p.setIdPaciente(Integer.parseInt(request.getParameter("idPaciente")));
            p.setNombre(request.getParameter("nombre"));
            p.setApellido(request.getParameter("apellido"));
            p.setDni(request.getParameter("dni"));
            p.setFechaNacimiento(Date.valueOf(request.getParameter("fechaNacimiento")));
            p.setTelefono(request.getParameter("telefono"));
            
            pacienteDAO.actualizar(p);
            response.sendRedirect("PacienteServlet");
        }
    }
}