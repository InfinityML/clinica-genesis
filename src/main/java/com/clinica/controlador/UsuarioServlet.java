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
import java.util.List;

@WebServlet(name = "UsuarioServlet", urlPatterns = {"/UsuarioServlet"})
public class UsuarioServlet extends HttpServlet {

    private UsuarioDAO usuarioDAO = new UsuarioDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Validar que el usuario tenga una sesión activa
        HttpSession sesion = request.getSession();
        if (sesion.getAttribute("usuarioLogueado") == null) {
            response.sendRedirect("index.jsp");
            return;
        }

        // 2. Interceptar acciones enviadas por la URL (Ej. Eliminar)
        String accion = request.getParameter("accion");
        if (accion != null && accion.equals("eliminar")) {
            // Capturamos el ID, lo eliminamos (estado = 0) y recargamos el Servlet
            int idEliminar = Integer.parseInt(request.getParameter("id"));
            usuarioDAO.eliminar(idEliminar);
            response.sendRedirect("UsuarioServlet");
            return; // Detiene el código aquí para que no siga ejecutando lo de abajo
        }

        // 3. Obtener la lista de usuarios activos desde la Base de Datos
        List<Usuario> listaUsuarios = usuarioDAO.listar();

        // 4. Enviar la lista a la vista (JSP)
        request.setAttribute("usuarios", listaUsuarios);
        request.getRequestDispatcher("usuarios.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String accion = request.getParameter("accion");
        
        if ("crear".equals(accion)) {
            // Lógica de Crear que ya programamos...
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            int idRol = Integer.parseInt(request.getParameter("rol"));
            
            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setUsername(username);
            nuevoUsuario.setEmail(email);
            nuevoUsuario.setPassword(password);
            nuevoUsuario.setIdRol(idRol);
            
            usuarioDAO.registrar(nuevoUsuario);
            response.sendRedirect("UsuarioServlet");
            
        } else if ("editar".equals(accion)) {
            // 1. Recibir los datos actualizados
            int id = Integer.parseInt(request.getParameter("idUsuario"));
            String username = request.getParameter("username");
            String email = request.getParameter("email");
            int idRol = Integer.parseInt(request.getParameter("rol"));
            
            // 2. Encapsular en el objeto
            Usuario uEdit = new Usuario();
            uEdit.setIdUsuario(id);
            uEdit.setUsername(username);
            uEdit.setEmail(email);
            uEdit.setIdRol(idRol);
            
            // 3. Ejecutar la actualización a través del DAO (Llama al Stored Procedure)
            boolean exito = usuarioDAO.actualizar(uEdit);
            if(exito){
                System.out.println("✅ Usuario " + id + " actualizado correctamente.");
            }
            
            // 4. Redirigir a la tabla
            response.sendRedirect("UsuarioServlet");
        }
    }
}