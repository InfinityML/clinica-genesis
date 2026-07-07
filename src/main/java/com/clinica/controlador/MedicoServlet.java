package com.clinica.controlador;

import com.clinica.dao.MedicoDAO;
import com.clinica.dao.UsuarioDAO;
import com.clinica.dao.impl.MedicoDAOImpl;
import com.clinica.dao.impl.UsuarioDAOImpl;
import com.clinica.modelo.Medico;
import com.clinica.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet(name = "MedicoServlet", urlPatterns = {"/MedicoServlet"})
public class MedicoServlet extends HttpServlet {

    private MedicoDAO medicoDAO = new MedicoDAOImpl();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl(); // Lo usaremos para el combo del Modal

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();
        if (sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // Interceptar Eliminación (Soft Delete)
        String accion = request.getParameter("accion");
        if ("eliminar".equals(accion)) {
            int idEliminar = Integer.parseInt(request.getParameter("id"));
            medicoDAO.eliminar(idEliminar);
            response.sendRedirect("MedicoServlet");
            return;
        }

        // Mandamos los médicos y también los usuarios para el formulario
        request.setAttribute("medicos", medicoDAO.listar());
        request.setAttribute("usuarios", usuarioDAO.listar());
        request.getRequestDispatcher("medicos.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        if ("crear".equals(accion)) {
            Medico m = new Medico();
            m.setNombre(request.getParameter("nombre"));
            m.setApellido(request.getParameter("apellido"));
            m.setDni(request.getParameter("dni"));
            m.setIdEspecialidad(Integer.parseInt(request.getParameter("idEspecialidad")));
            m.setIdUsuario(Integer.parseInt(request.getParameter("idUsuario")));
            
            medicoDAO.registrar(m);
            response.sendRedirect("MedicoServlet");
            
        } else if ("editar".equals(accion)) {
            Medico m = new Medico();
            m.setIdMedico(Integer.parseInt(request.getParameter("idMedico")));
            m.setNombre(request.getParameter("nombre"));
            m.setApellido(request.getParameter("apellido"));
            m.setDni(request.getParameter("dni"));
            m.setIdEspecialidad(Integer.parseInt(request.getParameter("idEspecialidad")));
            // El ID Usuario no se edita por seguridad transaccional
            
            medicoDAO.actualizar(m);
            response.sendRedirect("MedicoServlet");
        }
    }
}