package com.clinica.controlador;

import com.clinica.dao.PagoDAO;
import com.clinica.dao.impl.PagoDAOImpl;
import com.clinica.modelo.Usuario;
import com.clinica.util.AppConfig;
import com.clinica.util.MercadoPagoService;
import com.clinica.util.Roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "PagoMercadoPagoServlet", urlPatterns = {"/PagoMercadoPagoServlet"})
public class PagoMercadoPagoServlet extends HttpServlet {

    private final PagoDAO pagoDAO = new PagoDAOImpl();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        if (user == null || user.getIdRol() != Roles.RECEPCIONISTA) {
            response.sendRedirect("index.jsp"); return;
        }

        try {
            int idCita = Integer.parseInt(request.getParameter("idCita"));
            int idServicio = Integer.parseInt(request.getParameter("idServicio"));
            BigDecimal monto = new BigDecimal(request.getParameter("monto"));

            int idPago = pagoDAO.registrarPago(idCita, idServicio, monto, "MercadoPago", null);
            if (idPago <= 0) { redirigir(response, "error", "No se pudo registrar el pago."); return; }

            String base = AppConfig.get("app.base_url", "http://localhost:8090/Clinica_T3");
            String initPoint = MercadoPagoService.crearPreferencia(idPago, "Servicio clínico Clínica UPN", monto, base);

            if (initPoint == null) {
                pagoDAO.anularPago(idPago);
                redirigir(response, "error", "No se pudo iniciar el pago con Mercado Pago. Revisa tu Access Token.");
                return;
            }
            response.sendRedirect(initPoint);
        } catch (Exception e) {
            redirigir(response, "error", "Datos de cobro inválidos: " + e.getMessage());
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String extRef = request.getParameter("external_reference");
        String paymentId = request.getParameter("payment_id");
        if (paymentId == null) paymentId = request.getParameter("collection_id");

        try {
            int idPago = Integer.parseInt(extRef);
            String estado = MercadoPagoService.verificarPago(paymentId);

            if ("approved".equals(estado)) {
                pagoDAO.confirmarPago(idPago, paymentId, paymentId);
                redirigir(response, "exito", "Pago aprobado con Mercado Pago.");
            } else {
                pagoDAO.anularPago(idPago);
                redirigir(response, "error", "El pago con Mercado Pago no se completó.");
            }
        } catch (Exception e) {
            redirigir(response, "error", "No se pudo procesar el retorno de Mercado Pago.");
        }
    }

    private void redirigir(HttpServletResponse response, String estado, String mensaje) throws IOException {
        String m = URLEncoder.encode(mensaje == null ? "" : mensaje, StandardCharsets.UTF_8);
        response.sendRedirect("PagoServlet?estado=" + estado + "&m=" + m);
    }
}