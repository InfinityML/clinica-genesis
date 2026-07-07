package com.clinica.controlador;

import com.clinica.dao.TriajeDAO;
import com.clinica.dao.impl.TriajeDAOImpl;
import com.clinica.modelo.Usuario;
import com.clinica.util.Roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "TriajeServlet", urlPatterns = {"/TriajeServlet"})
public class TriajeServlet extends HttpServlet {

    private final TriajeDAO triajeDAO = new TriajeDAOImpl();

    private boolean puedeTriar(Usuario u) {
        return u != null && (u.getIdRol() == Roles.ENFERMERO || u.getIdRol() == Roles.PRACTICANTE);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");

        if (user == null) { response.sendRedirect("index.jsp"); return; }
        if (!puedeTriar(user)) { response.sendRedirect("DashboardServlet"); return; }

        request.setAttribute("citasParaTriaje", triajeDAO.listarCitasParaTriaje());
        request.getRequestDispatcher("triaje.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        if (!puedeTriar(user)) { response.sendRedirect("index.jsp"); return; }

        int idCita = Integer.parseInt(request.getParameter("idCita"));
        double peso = Double.parseDouble(request.getParameter("peso"));
        int talla = Integer.parseInt(request.getParameter("talla"));
        double temp = Double.parseDouble(request.getParameter("temp"));
        String presion = request.getParameter("presion");
        int fc = Integer.parseInt(request.getParameter("fc"));
        String obs = request.getParameter("obs");

        Integer idPracticante = null;
        Integer idEnfermero = null;

        if (user.getIdRol() == Roles.ENFERMERO) {
            int idE = triajeDAO.obtenerIdEnfermeroPorUsuario(user.getIdUsuario());
            if (idE > 0) idEnfermero = idE;
        } else if (user.getIdRol() == Roles.PRACTICANTE) {
            int idP = triajeDAO.obtenerIdPracticantePorUsuario(user.getIdUsuario());
            if (idP > 0) idPracticante = idP;
        }

        if (idPracticante != null || idEnfermero != null) {
            triajeDAO.registrarTriaje(idCita, peso, talla, temp, presion, fc, obs, idPracticante, idEnfermero);
        }

        response.sendRedirect("TriajeServlet");
    }
}