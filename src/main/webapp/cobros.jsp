<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.clinica.modelo.Usuario" %>
<%
    Usuario __usr = (Usuario) session.getAttribute("usuarioLogueado");
    if (__usr == null) { response.sendRedirect("index.jsp"); return; }
    if (__usr.getIdRol() != 5) { response.sendRedirect("DashboardServlet"); return; }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cobros · Clínica UPN</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.44.0/dist/tabler-icons.min.css">
    <link rel="stylesheet" href="assets/css/app.css">
</head>
<body>
    <div class="app-shell">
        <jsp:include page="sidebar.jsp" />
        <div class="app-main">
            <header class="topbar">
                <button class="topbar__toggle" aria-label="Abrir menú"><i class="ti ti-menu-2"></i></button>
                <div>
                    <div class="topbar__title">Cobros</div>
                    <div class="topbar__sub">Pagos y comprobantes electrónicos</div>
                </div>
                <div class="topbar__spacer"></div>
                <button class="c-btn c-btn--primary" data-modal-open="modalCobro">
                    <i class="ti ti-cash"></i> Registrar cobro
                </button>
            </header>

            <div class="app-content stack-lg">

                <c:if test="${param.estado == 'exito'}">
                    <div class="c-alert c-alert--success"><i class="ti ti-circle-check"></i><span>${param.m}</span></div>
                </c:if>
                <c:if test="${param.estado == 'error'}">
                    <div class="c-alert c-alert--danger"><i class="ti ti-alert-circle"></i><span>${param.m}</span></div>
                </c:if>

                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-receipt"></i> Historial de cobros</div>
                        <span class="muted" style="font-size:13px;">${pagos.size()} registrados</span>
                    </div>
                    <table class="c-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Paciente</th>
                                <th>Servicio</th>
                                <th>Monto</th>
                                <th>Estado</th>
                                <th>Comprobante</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${pagos}">
                                <tr>
                                    <td class="muted">#${p.id_pago}</td>
                                    <td class="text-strong">${p.paciente_nombre} ${p.paciente_apellido}</td>
                                    <td>${p.servicio}</td>
                                    <td class="text-strong">S/ ${p.monto}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.estado == 'Pagado'}"><span class="c-badge c-badge--success">Pagado</span></c:when>
                                            <c:when test="${p.estado == 'Anulado'}"><span class="c-badge c-badge--danger">Anulado</span></c:when>
                                            <c:otherwise><span class="c-badge c-badge--warning">Pendiente</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:set var="comp" value="${comprobantes[p.id_pago]}" />
                                        <c:choose>
                                            <c:when test="${comp != null}">
                                                <div class="table-actions">
                                                    <span class="c-badge c-badge--info">${comp.tipoComprobante} ${comp.serie}-${comp.correlativo}</span>
                                                    <c:if test="${not empty comp.enlacePdf}">
                                                        <a class="c-btn c-btn--ghost c-btn--sm" href="${comp.enlacePdf}" target="_blank"><i class="ti ti-file-type-pdf"></i> PDF</a>
                                                    </c:if>
                                                </div>
                                            </c:when>
                                            <c:when test="${p.estado == 'Pagado'}">
                                                <button class="c-btn c-btn--ghost c-btn--sm btn-comprobante" data-id="${p.id_pago}">
                                                    <i class="ti ti-file-invoice"></i> Generar
                                                </button>
                                            </c:when>
                                            <c:otherwise><span class="muted">—</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty pagos}">
                                <tr><td colspan="6" class="muted" style="text-align:center; padding:28px;">Aún no hay cobros registrados.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: registrar cobro -->
    <div class="c-modal" id="modalCobro">
        <div class="c-modal__dialog">
            <form id="formCobro" action="PagoServlet" method="POST">
                <input type="hidden" name="monto" id="cobro_monto">
                <input type="hidden" name="token" id="cobro_token">
                <input type="hidden" name="email" id="cobro_email">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-cash"></i> Registrar cobro</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="field">
                        <label>Cita</label>
                        <select class="c-select" name="idCita" required>
                            <option value="">— Selecciona una cita —</option>
                            <c:forEach var="c" items="${citas}">
                                <option value="${c.id_cita}">${c.paciente} · ${c.fecha} ${c.hora}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="field">
                        <label>Servicio</label>
                        <select class="c-select" name="idServicio" id="cobro_servicio" required onchange="actualizarTotal()">
                            <option value="">— Selecciona un servicio —</option>
                            <c:forEach var="s" items="${servicios}">
                                <option value="${s.idServicio}" data-precio="${s.precio}">${s.nombreServicio} · S/ ${s.precio}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="c-alert c-alert--info" style="justify-content:space-between; align-items:center;">
                        <span><i class="ti ti-receipt-2"></i> Total a cobrar</span>
                        <strong id="cobro_total" style="font-size:18px;">S/ 0.00</strong>
                    </div>
                </div>
                <div class="c-modal__foot">
                    <button type="button" class="c-btn c-btn--ghost" data-modal-close>Cancelar</button>
                    <button type="button" class="c-btn c-btn--primary" onclick="pagar()"><i class="ti ti-credit-card"></i> Tarjeta (Culqui)</button>
                    <button type="button" class="c-btn c-btn--primary" onclick="pagarMP()" style="background:#009ee3;border-color:#009ee3;"><i class="ti ti-wallet"></i> Mercado Pago</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal: generar comprobante -->
    <div class="c-modal" id="modalComprobante">
        <div class="c-modal__dialog">
            <form action="ComprobanteServlet" method="POST">
                <input type="hidden" name="idPago" id="comp_idpago">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-file-invoice"></i> Emitir comprobante</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="field">
                        <label>Tipo de comprobante</label>
                        <select class="c-select" name="tipo" id="comp_tipo" required onchange="ajustarDoc()">
                            <option value="Boleta">Boleta de venta</option>
                            <option value="Factura">Factura</option>
                        </select>
                    </div>
                    <div class="field">
                        <label id="comp_labeldoc">DNI del cliente</label>
                        <input class="c-input" type="text" name="cliNumDoc" id="comp_doc" maxlength="11" required>
                    </div>
                    <div class="field">
                        <label id="comp_labelnombre">Nombre del cliente</label>
                        <input class="c-input" type="text" name="cliNombre" required>
                    </div>
                    <div class="c-alert c-alert--info"><i class="ti ti-percentage"></i><span>El IGV (18%) se calcula automáticamente sobre el monto pagado.</span></div>
                </div>
                <div class="c-modal__foot">
                    <button type="button" class="c-btn c-btn--ghost" data-modal-close>Cancelar</button>
                    <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-send"></i> Emitir a SUNAT</button>
                </div>
            </form>
        </div>
    </div>

    <script src="https://checkout.culqi.com/v2"></script>
    <script src="assets/js/app.js"></script>
    <script>
        if (typeof Culqi !== 'undefined') { Culqi.publicKey = '${culquiPublicKey}'; }

        function precioSel() {
            var s = document.getElementById('cobro_servicio');
            var o = s.options[s.selectedIndex];
            return (o && o.dataset.precio) ? parseFloat(o.dataset.precio) : 0;
        }
        function actualizarTotal() {
            var p = precioSel();
            document.getElementById('cobro_total').textContent = 'S/ ' + p.toFixed(2);
            document.getElementById('cobro_monto').value = p.toFixed(2);
        }
        function pagar() {
            var cita = document.querySelector('[name=idCita]').value;
            var serv = document.getElementById('cobro_servicio').value;
            var precio = precioSel();
            if (!cita || !serv || precio <= 0) { alert('Selecciona la cita y el servicio.'); return; }
            document.getElementById('cobro_monto').value = precio.toFixed(2);
            if (typeof Culqi === 'undefined') { alert('No se pudo cargar Culqui. Revisa tu conexión.'); return; }
            Culqi.settings({ title: 'Clínica UPN', currency: 'PEN', description: 'Servicio clínico', amount: Math.round(precio * 100) });
            Culqi.open();
        }
        function pagarMP() {
            var cita = document.querySelector('[name=idCita]').value;
            var serv = document.getElementById('cobro_servicio').value;
            var precio = precioSel();
            if (!cita || !serv || precio <= 0) { alert('Selecciona la cita y el servicio.'); return; }
            document.getElementById('cobro_monto').value = precio.toFixed(2);
            var f = document.getElementById('formCobro');
            f.action = 'PagoMercadoPagoServlet';
            f.submit();
        }
        function culqi() {
            if (typeof Culqi !== 'undefined' && Culqi.token) {
                document.getElementById('cobro_token').value = Culqi.token.id;
                document.getElementById('cobro_email').value = Culqi.token.email || '';
                document.getElementById('formCobro').submit();
            } else if (typeof Culqi !== 'undefined' && Culqi.error) {
                alert(Culqi.error.user_message);
            }
        }

        document.querySelectorAll('.btn-comprobante').forEach(function (b) {
            b.addEventListener('click', function () {
                document.getElementById('comp_idpago').value = b.dataset.id;
                document.getElementById('modalComprobante').classList.add('is-open');
            });
        });
        function ajustarDoc() {
            var esFactura = document.getElementById('comp_tipo').value === 'Factura';
            document.getElementById('comp_labeldoc').textContent = esFactura ? 'RUC del cliente' : 'DNI del cliente';
            document.getElementById('comp_labelnombre').textContent = esFactura ? 'Razón social' : 'Nombre del cliente';
            document.getElementById('comp_doc').maxLength = esFactura ? 11 : 8;
        }
    </script>
</body>
</html>