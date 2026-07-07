package com.clinica.controlador;

import com.clinica.dao.UsuarioDAO;
import com.clinica.dao.impl.UsuarioDAOImpl;
import com.clinica.modelo.Usuario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import com.clinica.util.RecaptchaService;

/**
 * Controlador para gestionar la autenticación y sesiones de la clínica.
 */
@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet", "/logout"})
public class LoginServlet extends HttpServlet {

    // Instanciamos el DAO respetando la interfaz (Bajo Acoplamiento)
    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Obtener los parámetros enviados desde el formulario HTML/JSP
        String user = request.getParameter("username");
        String pass = request.getParameter("password");

        // Verificación de reCAPTCHA (anti fuerza bruta)
        String captcha = request.getParameter("g-recaptcha-response");
        if (!RecaptchaService.verificar(captcha, request.getRemoteAddr())) {
            request.setAttribute("errorAuth", "Confirma que no eres un robot e inténtalo de nuevo.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
            return;
        }

        // 2. Validar contra la base de datos usando nuestro DAO
        Usuario usuarioValido = usuarioDAO.validarLogin(user, pass);

        // 3. Manejo de Sesiones (Requisito estricto de la Rúbrica)
        if (usuarioValido != null) {
            // Login exitoso: Creamos una sesión HTTP segura
            HttpSession sesion = request.getSession();
            sesion.setAttribute("usuarioLogueado", usuarioValido);
            
            // Redirigimos al panel de control (que diseñaremos después)
            response.sendRedirect("DashboardServlet");
        } else {
            // Login fallido: Devolvemos a la página de inicio con un mensaje de error
            request.setAttribute("errorAuth", "Credenciales incorrectas. Verifique su usuario y contraseña.");
            request.getRequestDispatcher("index.jsp").forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Manejo del Logout (Cerrar sesión)
        String path = request.getServletPath();
        
        if (path.equals("/logout")) {
            HttpSession sesion = request.getSession(false);
            if (sesion != null) {
                sesion.invalidate(); // Destruye la sesión por completo
            }
            response.sendRedirect("index.jsp");
        } else {
            // Si intentan entrar por URL por accidente (GET), los mandamos al login
            response.sendRedirect("index.jsp");
        }
    }
}