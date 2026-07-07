package com.clinica.controlador;

import com.clinica.dao.EnfermeroDAO;
import com.clinica.dao.UsuarioDAO;
import com.clinica.dao.impl.EnfermeroDAOImpl;
import com.clinica.dao.impl.UsuarioDAOImpl;
import com.clinica.modelo.Enfermero;
import com.clinica.modelo.Usuario;
import com.clinica.util.Roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "EnfermeroServlet", urlPatterns = {"/EnfermeroServlet"})
public class EnfermeroServlet extends HttpServlet {

    private final EnfermeroDAO enfermeroDAO = new EnfermeroDAOImpl();
    private final UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    private boolean esAdmin(HttpServletRequest request) {
        Object u = request.getSession().getAttribute("usuarioLogueado");
        return (u instanceof Usuario) && ((Usuario) u).getIdRol() == Roles.ADMINISTRADOR;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        if (sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("index.jsp");
            return;
        }
        if (!esAdmin(request)) {
            response.sendRedirect("DashboardServlet");
            return;
        }

        String accion = request.getParameter("accion");
        if ("eliminar".equals(accion)) {
            enfermeroDAO.eliminar(Integer.parseInt(request.getParameter("id")));
            response.sendRedirect("EnfermeroServlet");
            return;
        }

        request.setAttribute("enfermeros", enfermeroDAO.listar());
        request.setAttribute("usuarios", usuarioDAO.listar());
        request.getRequestDispatcher("enfermeros.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        if (!esAdmin(request)) {
            response.sendRedirect("index.jsp");
            return;
        }

        String accion = request.getParameter("accion");

        if ("crear".equals(accion)) {
            Enfermero e = new Enfermero();
            e.setNombre(request.getParameter("nombre"));
            e.setApellido(request.getParameter("apellido"));
            e.setDni(request.getParameter("dni"));
            e.setTelefono(request.getParameter("telefono"));
            String idUsr = request.getParameter("idUsuario");
            e.setIdUsuario((idUsr != null && !idUsr.isEmpty()) ? Integer.valueOf(idUsr) : null);
            enfermeroDAO.registrar(e);
            response.sendRedirect("EnfermeroServlet");

        } else if ("editar".equals(accion)) {
            Enfermero e = new Enfermero();
            e.setIdEnfermero(Integer.parseInt(request.getParameter("idEnfermero")));
            e.setNombre(request.getParameter("nombre"));
            e.setApellido(request.getParameter("apellido"));
            e.setDni(request.getParameter("dni"));
            e.setTelefono(request.getParameter("telefono"));
            enfermeroDAO.actualizar(e);
            response.sendRedirect("EnfermeroServlet");
        }
    }
}