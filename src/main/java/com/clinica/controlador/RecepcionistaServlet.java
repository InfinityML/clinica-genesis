package com.clinica.controlador;

import com.clinica.dao.RecepcionistaDAO;
import com.clinica.dao.UsuarioDAO;
import com.clinica.dao.impl.RecepcionistaDAOImpl;
import com.clinica.dao.impl.UsuarioDAOImpl;
import com.clinica.modelo.Recepcionista;
import com.clinica.modelo.Usuario;
import com.clinica.util.Roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "RecepcionistaServlet", urlPatterns = {"/RecepcionistaServlet"})
public class RecepcionistaServlet extends HttpServlet {

    private final RecepcionistaDAO recepcionistaDAO = new RecepcionistaDAOImpl();
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
            recepcionistaDAO.eliminar(Integer.parseInt(request.getParameter("id")));
            response.sendRedirect("RecepcionistaServlet");
            return;
        }

        request.setAttribute("recepcionistas", recepcionistaDAO.listar());
        request.setAttribute("usuarios", usuarioDAO.listar());
        request.getRequestDispatcher("recepcionistas.jsp").forward(request, response);
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
            Recepcionista r = new Recepcionista();
            r.setNombre(request.getParameter("nombre"));
            r.setApellido(request.getParameter("apellido"));
            r.setDni(request.getParameter("dni"));
            r.setTelefono(request.getParameter("telefono"));
            String idUsr = request.getParameter("idUsuario");
            r.setIdUsuario((idUsr != null && !idUsr.isEmpty()) ? Integer.valueOf(idUsr) : null);
            recepcionistaDAO.registrar(r);
            response.sendRedirect("RecepcionistaServlet");

        } else if ("editar".equals(accion)) {
            Recepcionista r = new Recepcionista();
            r.setIdRecepcionista(Integer.parseInt(request.getParameter("idRecepcionista")));
            r.setNombre(request.getParameter("nombre"));
            r.setApellido(request.getParameter("apellido"));
            r.setDni(request.getParameter("dni"));
            r.setTelefono(request.getParameter("telefono"));
            recepcionistaDAO.actualizar(r);
            response.sendRedirect("RecepcionistaServlet");
        }
    }
}