<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.clinica.modelo.Usuario" %>
<%
    Usuario __usr = (Usuario) session.getAttribute("usuarioLogueado");
    if (__usr == null) { response.sendRedirect("index.jsp"); return; }
    if (__usr.getIdRol() != 6 && __usr.getIdRol() != 3) { response.sendRedirect("DashboardServlet"); return; }
    boolean __esEnfermero = __usr.getIdRol() == 6;
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Triaje · Clínica UPN</title>
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
                    <div class="topbar__title">Triaje</div>
                    <div class="topbar__sub"><%= __esEnfermero ? "Toma de signos vitales · Enfermería" : "Apoyo en toma de signos vitales" %></div>
                </div>
                <div class="topbar__spacer"></div>
                <span class="c-badge c-badge--info"><i class="ti ti-clipboard-pulse"></i> ${citasParaTriaje.size()} en espera</span>
            </header>

            <div class="app-content">
                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-clipboard-pulse"></i> Pacientes en espera</div>
                    </div>
                    <table class="c-table">
                        <thead>
                            <tr>
                                <th>Fecha y hora</th>
                                <th>Paciente</th>
                                <th>DNI</th>
                                <th>Médico asignado</th>
                                <th style="text-align:right;">Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${citasParaTriaje}">
                                <tr>
                                    <td><span class="text-strong">${c.fecha}</span> <span class="muted">${c.hora}</span></td>
                                    <td>${c.paciente}</td>
                                    <td>${c.dni}</td>
                                    <td>Dr. ${c.medico}</td>
                                    <td style="text-align:right;">
                                        <button class="c-btn c-btn--primary c-btn--sm btn-triaje"
                                                data-id="${c.id_cita}" data-paciente="${c.paciente}">
                                            <i class="ti ti-heart-rate-monitor"></i> Tomar signos
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty citasParaTriaje}">
                                <tr><td colspan="5" class="muted" style="text-align:center; padding:28px;">No hay pacientes en espera de triaje.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal de triaje -->
    <div class="c-modal" id="modalTriaje">
        <div class="c-modal__dialog">
            <form action="TriajeServlet" method="POST">
                <input type="hidden" name="idCita" id="triaje_idcita">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-heart-rate-monitor"></i> Signos vitales</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="c-alert c-alert--info" style="margin-bottom:18px;">
                        <i class="ti ti-user"></i>
                        <span>Paciente: <strong id="triaje_paciente"></strong></span>
                    </div>
                    <div class="form-row">
                        <div class="field"><label>Peso (kg)</label><input class="c-input" type="number" step="0.01" name="peso" required></div>
                        <div class="field"><label>Talla (cm)</label><input class="c-input" type="number" name="talla" required></div>
                    </div>
                    <div class="form-row">
                        <div class="field"><label>Temperatura (°C)</label><input class="c-input" type="number" step="0.1" name="temp" required></div>
                        <div class="field"><label>Presión arterial</label><input class="c-input" type="text" name="presion" placeholder="Ej. 120/80" required></div>
                    </div>
                    <div class="field"><label>Frecuencia cardíaca (lpm)</label><input class="c-input" type="number" name="fc" required></div>
                    <div class="field"><label>Observaciones</label><textarea class="c-textarea" name="obs" rows="2"></textarea></div>
                </div>
                <div class="c-modal__foot">
                    <button type="button" class="c-btn c-btn--ghost" data-modal-close>Cancelar</button>
                    <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-device-floppy"></i> Guardar triaje</button>
                </div>
            </form>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        document.querySelectorAll('.btn-triaje').forEach(function (b) {
            b.addEventListener('click', function () {
                document.getElementById('triaje_idcita').value = b.dataset.id;
                document.getElementById('triaje_paciente').textContent = b.dataset.paciente;
                document.getElementById('modalTriaje').classList.add('is-open');
            });
        });
    </script>
</body>
</html>