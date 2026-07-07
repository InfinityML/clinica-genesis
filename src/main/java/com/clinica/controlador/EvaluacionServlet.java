package com.clinica.controlador;

import com.clinica.dao.EvaluacionDAO;
import com.clinica.dao.impl.EvaluacionDAOImpl;
import com.clinica.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet(name = "EvaluacionServlet", urlPatterns = {"/EvaluacionServlet"})
public class EvaluacionServlet extends HttpServlet {

    private EvaluacionDAO evaluacionDAO = new EvaluacionDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        
        // Solo para Doctores (Rol 2)
        if (user == null || user.getIdRol() != 2) {
            response.sendRedirect("dashboard.jsp");
            return;
        }

        request.setAttribute("misPracticantes", evaluacionDAO.listarMisPracticantes(user.getIdUsuario()));
        request.getRequestDispatcher("evaluar_practicantes.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        
        int idPracticante = Integer.parseInt(request.getParameter("idPracticante"));
        int puntualidad = Integer.parseInt(request.getParameter("puntualidad"));
        int conocimiento = Integer.parseInt(request.getParameter("conocimiento"));
        int desempeno = Integer.parseInt(request.getParameter("desempeno"));
        String comentario = request.getParameter("comentario");
        
        evaluacionDAO.registrarEvaluacion(idPracticante, user.getIdUsuario(), puntualidad, conocimiento, desempeno, comentario);
        
        response.sendRedirect("EvaluacionServlet");
    }
}