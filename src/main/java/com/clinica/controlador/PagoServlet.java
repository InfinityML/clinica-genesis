package com.clinica.controlador;

import com.clinica.dao.CitaDAO;
import com.clinica.dao.ComprobanteDAO;
import com.clinica.dao.PagoDAO;
import com.clinica.dao.ServicioDAO;
import com.clinica.dao.impl.CitaDAOImpl;
import com.clinica.dao.impl.ComprobanteDAOImpl;
import com.clinica.dao.impl.PagoDAOImpl;
import com.clinica.dao.impl.ServicioDAOImpl;
import com.clinica.modelo.Comprobante;
import com.clinica.modelo.Usuario;
import com.clinica.util.CulquiConfig;
import com.clinica.util.CulquiService;
import com.clinica.util.Roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "PagoServlet", urlPatterns = {"/PagoServlet"})
public class PagoServlet extends HttpServlet {

    private final PagoDAO pagoDAO = new PagoDAOImpl();
    private final ServicioDAO servicioDAO = new ServicioDAOImpl();
    private final CitaDAO citaDAO = new CitaDAOImpl();
    private final ComprobanteDAO comprobanteDAO = new ComprobanteDAOImpl();

    private boolean esRecepcion(Usuario u) {
        return u != null && u.getIdRol() == Roles.RECEPCIONISTA;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        if (user == null) { response.sendRedirect("index.jsp"); return; }
        if (!esRecepcion(user)) { response.sendRedirect("DashboardServlet"); return; }

        request.setAttribute("citas", citaDAO.listarTodas());
        request.setAttribute("servicios", servicioDAO.listar());

        List<Map<String, Object>> pagos = pagoDAO.listarPagos();
        Map<Integer, Comprobante> comprobantes = new HashMap<>();
        for (Map<String, Object> pg : pagos) {
            int idp = (Integer) pg.get("id_pago");
            if ("Pagado".equals(pg.get("estado"))) {
                Comprobante c = comprobanteDAO.buscarPorPago(idp);
                if (c != null) comprobantes.put(idp, c);
            }
        }
        request.setAttribute("pagos", pagos);
        request.setAttribute("comprobantes", comprobantes);
        request.setAttribute("culquiPublicKey", CulquiConfig.publicKey());
        request.setAttribute("culquiConfigurado", CulquiConfig.estaConfigurado());
        request.getRequestDispatcher("cobros.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        if (!esRecepcion(user)) { response.sendRedirect("index.jsp"); return; }

        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            int idServicio = Integer.parseInt(request.getParameter("idServicio"));
            BigDecimal monto = new BigDecimal(request.getParameter("monto"));
            String token = request.getParameter("token");
            String email = request.getParameter("email");

            int idPago = pagoDAO.registrarPago(idCita, idServicio, monto, "Culqui", null);
            if (idPago <= 0) { redirigir(response, "error", "No se pudo registrar el pago."); return; }

            int centimos = monto.multiply(new BigDecimal("100")).intValue();
            CulquiService.Resultado res = CulquiService.crearCargo(token, centimos, email);

            if (res.exito) {
                pagoDAO.confirmarPago(idPago, token, res.chargeId);
                redirigir(response, "exito", res.mensaje);
            } else {
                pagoDAO.anularPago(idPago);
                redirigir(response, "error", res.mensaje);
            }
        } catch (Exception e) {
            redirigir(response, "error", "Datos de cobro inválidos: " + e.getMessage());
        }
    }

    private void redirigir(HttpServletResponse response, String estado, String mensaje) throws IOException {
        String m = URLEncoder.encode(mensaje == null ? "" : mensaje, StandardCharsets.UTF_8);
        response.sendRedirect("PagoServlet?estado=" + estado + "&m=" + m);
    }
}
