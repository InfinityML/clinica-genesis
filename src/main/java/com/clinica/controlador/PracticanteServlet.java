package com.clinica.controlador;

import com.clinica.dao.PracticanteDAO;
import com.clinica.dao.MedicoDAO;
import com.clinica.dao.UsuarioDAO;
import com.clinica.dao.impl.PracticanteDAOImpl;
import com.clinica.dao.impl.MedicoDAOImpl;
import com.clinica.dao.impl.UsuarioDAOImpl;
import com.clinica.modelo.Practicante;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "PracticanteServlet", urlPatterns = {"/PracticanteServlet"})
public class PracticanteServlet extends HttpServlet {

    private PracticanteDAO practicanteDAO = new PracticanteDAOImpl();
    private MedicoDAO medicoDAO = new MedicoDAOImpl();
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();
        if (sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        String accion = request.getParameter("accion");
        if ("eliminar".equals(accion)) {
            int idEliminar = Integer.parseInt(request.getParameter("id"));
            practicanteDAO.eliminar(idEliminar);
            response.sendRedirect("PracticanteServlet");
            return;
        }

        // Cargamos todas las listas necesarias para la vista
        request.setAttribute("practicantes", practicanteDAO.listar());
        request.setAttribute("medicos", medicoDAO.listar());
        request.setAttribute("usuarios", usuarioDAO.listar());
        request.getRequestDispatcher("practicantes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");

        if ("crear".equals(accion)) {
            Practicante p = new Practicante();
            p.setNombre(request.getParameter("nombre"));
            p.setApellido(request.getParameter("apellido"));
            p.setDni(request.getParameter("dni"));
            p.setCodigoUniversitario(request.getParameter("codigo"));
            
            String idTutor = request.getParameter("idTutor");
            p.setIdMedicoTutor((idTutor != null && !idTutor.isEmpty()) ? Integer.valueOf(idTutor) : null);
            
            String idUsr = request.getParameter("idUsuario");
            p.setIdUsuario((idUsr != null && !idUsr.isEmpty()) ? Integer.valueOf(idUsr) : null);
            
            practicanteDAO.registrar(p);
            response.sendRedirect("PracticanteServlet");
            
        } else if ("editar".equals(accion)) {
            Practicante p = new Practicante();
            p.setIdPracticante(Integer.parseInt(request.getParameter("idPracticante")));
            p.setNombre(request.getParameter("nombre"));
            p.setApellido(request.getParameter("apellido"));
            p.setDni(request.getParameter("dni"));
            p.setCodigoUniversitario(request.getParameter("codigo"));
            
            String idTutor = request.getParameter("idTutor");
            p.setIdMedicoTutor((idTutor != null && !idTutor.isEmpty()) ? Integer.valueOf(idTutor) : null);
            
            practicanteDAO.actualizar(p);
            response.sendRedirect("PracticanteServlet");
        }
    }
}