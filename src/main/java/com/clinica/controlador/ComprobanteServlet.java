package com.clinica.controlador;

import com.clinica.dao.ComprobanteDAO;
import com.clinica.dao.PagoDAO;
import com.clinica.dao.impl.ComprobanteDAOImpl;
import com.clinica.dao.impl.PagoDAOImpl;
import com.clinica.modelo.Pago;
import com.clinica.modelo.Usuario;
import com.clinica.util.NubefactService;
import com.clinica.util.Roles;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet(name = "ComprobanteServlet", urlPatterns = {"/ComprobanteServlet"})
public class ComprobanteServlet extends HttpServlet {

    private final PagoDAO pagoDAO = new PagoDAOImpl();
    private final ComprobanteDAO comprobanteDAO = new ComprobanteDAOImpl();

    private boolean esRecepcion(Usuario u) {
        return u != null && u.getIdRol() == Roles.RECEPCIONISTA;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession sesion = request.getSession();
        Usuario user = (Usuario) sesion.getAttribute("usuarioLogueado");
        if (!esRecepcion(user)) { response.sendRedirect("index.jsp"); return; }

        try {
            int idPago = Integer.parseInt(request.getParameter("idPago"));
            String tipo = request.getParameter("tipo");           // "Boleta" o "Factura"
            String cliNumDoc = request.getParameter("cliNumDoc");
            String cliNombre = request.getParameter("cliNombre");

            Pago pago = pagoDAO.buscarPago(idPago);
            if (pago == null || !"Pagado".equals(pago.getEstadoPago())) {
                redirigir(response, "error", "Solo se puede facturar un pago en estado Pagado."); return;
            }
            if (comprobanteDAO.buscarPorPago(idPago) != null) {
                redirigir(response, "error", "Este pago ya tiene un comprobante emitido."); return;
            }

            // Cálculo de IGV (18%)
            BigDecimal total = pago.getMonto();
            BigDecimal subtotal, igv;
            if (pago.isAfectoIgv()) {
                subtotal = total.divide(new BigDecimal("1.18"), 2, RoundingMode.HALF_UP);
                igv = total.subtract(subtotal);
            } else {
                subtotal = total;
                igv = BigDecimal.ZERO;
            }

            boolean esFactura = "Factura".equalsIgnoreCase(tipo);
            String serie = esFactura ? "FFF1" : "BBB1";
            String cliTipoDoc = esFactura ? "6" : "1";   // 6=RUC, 1=DNI

            int correlativo = comprobanteDAO.siguienteCorrelativo(serie);

            // 1) Guardar comprobante (estado Pendiente)
            int idComp = comprobanteDAO.registrarComprobante(idPago, tipo, serie, correlativo,
                    cliTipoDoc, cliNumDoc, cliNombre, subtotal, igv, total);
            if (idComp <= 0) { redirigir(response, "error", "No se pudo registrar el comprobante."); return; }

            // 2) Emitir en Nubefact (o simular)
            NubefactService.Resultado r = NubefactService.emitir(tipo, serie, correlativo,
                    cliTipoDoc, cliNumDoc, cliNombre, subtotal, igv, total,
                    pago.isAfectoIgv(), pago.getNombreServicio());

            // 3) Actualizar con la respuesta de SUNAT
            comprobanteDAO.actualizarSunat(idComp, r.estado, r.hash, r.pdf, r.xml);

            redirigir(response, r.exito ? "exito" : "error",
                    r.exito ? (tipo + " " + serie + "-" + correlativo + " emitida. " + r.mensaje) : r.mensaje);

        } catch (Exception e) {
            redirigir(response, "error", "Datos de comprobante inválidos: " + e.getMessage());
        }
    }

    private void redirigir(HttpServletResponse response, String estado, String mensaje) throws IOException {
        String m = URLEncoder.encode(mensaje == null ? "" : mensaje, StandardCharsets.UTF_8);
        response.sendRedirect("PagoServlet?estado=" + estado + "&m=" + m);
    }
}