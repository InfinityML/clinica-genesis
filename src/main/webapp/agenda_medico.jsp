<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.clinica.modelo.Usuario" %>
<%
    Usuario __usr = (Usuario) session.getAttribute("usuarioLogueado");
    if (__usr == null) { response.sendRedirect("index.jsp"); return; }
    if (__usr.getIdRol() != 2) { response.sendRedirect("DashboardServlet"); return; }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mi agenda · Clínica UPN</title>
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
                    <div class="topbar__title">Mi agenda</div>
                    <div class="topbar__sub">Citas pendientes de atención</div>
                </div>
                <div class="topbar__spacer"></div>
                <span class="c-badge c-badge--info"><i class="ti ti-calendar-heart"></i> ${agenda.size()} pendientes</span>
            </header>

            <div class="app-content">
                <div class="grid-2">
                    <c:forEach var="cita" items="${agenda}">
                        <div class="c-card">
                            <div class="c-card__body">
                                <div style="display:flex; align-items:center; justify-content:space-between; gap:10px; margin-bottom:12px;">
                                    <div class="c-card__title"><i class="ti ti-user"></i> ${cita.paciente}</div>
                                    <c:choose>
                                        <c:when test="${cita.modalidad == 'Teleconsulta'}"><span class="c-badge c-badge--info"><i class="ti ti-video"></i> Teleconsulta</span></c:when>
                                        <c:otherwise><span class="c-badge c-badge--muted"><i class="ti ti-building-hospital"></i> Presencial</span></c:otherwise>
                                    </c:choose>
                                </div>
                                <p class="muted" style="margin:0 0 14px;"><i class="ti ti-calendar"></i> ${cita.fecha} &nbsp;·&nbsp; <i class="ti ti-clock"></i> ${cita.hora}</p>

                                <c:if test="${cita.modalidad == 'Teleconsulta'}">
                                    <c:choose>
                                        <c:when test="${cita.confirmada == 1}">
                                            <a class="c-btn c-btn--primary c-btn--block" href="VideollamadaServlet?idCita=${cita.id_cita}" style="margin-bottom:10px;">
                                                <i class="ti ti-video"></i> Iniciar teleconsulta
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a class="c-btn c-btn--ghost c-btn--block" href="AtencionServlet?accion=confirmar&idCita=${cita.id_cita}" style="margin-bottom:10px;">
                                                <i class="ti ti-check"></i> Aceptar teleconsulta
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:if>

                                <button class="c-btn c-btn--primary c-btn--block btn-atender"
                                        data-idcita="${cita.id_cita}" data-idpaciente="${cita.id_paciente}"
                                        data-idhistoria="${cita.id_historia}" data-idmedico="${cita.id_medico}"
                                        data-paciente="${cita.paciente}"
                                        data-peso="${cita.peso}" data-talla="${cita.talla}" data-temp="${cita.temp}"
                                        data-presion="${cita.presion}" data-fc="${cita.fc}" data-obs="${cita.obs_triaje}">
                                    <i class="ti ti-clipboard-pulse"></i> Iniciar atención
                                </button>
                            </div>
                        </div>
                    </c:forEach>

                    <c:if test="${empty agenda}">
                        <div class="c-alert c-alert--info" style="grid-column:1/-1;">
                            <i class="ti ti-coffee"></i>
                            <span>No tienes citas pendientes asignadas por ahora.</span>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal de atención -->
    <div class="c-modal" id="modalAtencion">
        <div class="c-modal__dialog" style="max-width:640px;">
            <form action="AtencionServlet" method="POST">
                <input type="hidden" name="idCita" id="modal_idcita">
                <input type="hidden" name="idPaciente" id="modal_idpaciente">
                <input type="hidden" name="idHistoria" id="modal_idhistoria">
                <input type="hidden" name="idMedico" id="modal_idmedico">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-heart-rate-monitor"></i> Registro clínico y receta</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="c-alert c-alert--info" style="margin-bottom:16px; flex-direction:column; align-items:stretch;">
                        <div style="display:flex; align-items:center; gap:8px; font-weight:600; margin-bottom:6px;"><i class="ti ti-user"></i> <span id="modal_nombrepaciente"></span></div>
                        <div style="font-size:13px; color:var(--text); display:grid; grid-template-columns:repeat(3,1fr); gap:4px 12px;">
                            <span><strong>Peso:</strong> <span id="modal_peso"></span> kg</span>
                            <span><strong>Talla:</strong> <span id="modal_talla"></span> cm</span>
                            <span><strong>Temp:</strong> <span id="modal_temp"></span> °C</span>
                            <span><strong>Presión:</strong> <span id="modal_presion"></span></span>
                            <span><strong>FC:</strong> <span id="modal_fc"></span> lpm</span>
                        </div>
                        <div style="font-size:12.5px; color:var(--text-soft); margin-top:6px; font-style:italic;"><strong>Obs. triaje:</strong> <span id="modal_obs"></span></div>
                    </div>

                    <div class="field"><label>Motivo de consulta (síntomas)</label><textarea class="c-textarea" name="motivo" rows="2" required></textarea></div>
                    <div class="field"><label>Diagnóstico médico final</label><textarea class="c-textarea" name="diagnostico" rows="3" required></textarea></div>

                    <div class="c-card" style="margin-top:6px;">
                        <div class="c-card__head"><div class="c-card__title"><i class="ti ti-pill"></i> Receta médica (opcional)</div></div>
                        <div class="c-card__body">
                            <div class="field">
                                <label>Medicamento</label>
                                <select class="c-select" name="idMedicamento">
                                    <option value="">— No recetar —</option>
                                    <c:forEach var="med" items="${medicamentos}">
                                        <option value="${med.id_medicamento}">${med.medicamento}</option>
                                    </c:forEach>
                                </select>
                            </div>
                            <div class="form-row" style="grid-template-columns:1fr 1fr 1fr;">
                                <div class="field" style="margin-bottom:0;"><label>Dosis</label><input class="c-input" type="text" name="dosis" placeholder="1 pastilla"></div>
                                <div class="field" style="margin-bottom:0;"><label>Frecuencia</label><input class="c-input" type="text" name="frecuencia" placeholder="Cada 8 hrs"></div>
                                <div class="field" style="margin-bottom:0;"><label>Duración</label><input class="c-input" type="text" name="duracion" placeholder="5 días"></div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="c-modal__foot">
                    <button type="button" class="c-btn c-btn--ghost" data-modal-close>Cancelar</button>
                    <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-circle-check"></i> Finalizar y guardar</button>
                </div>
            </form>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        document.querySelectorAll('.btn-atender').forEach(function (b) {
            b.addEventListener('click', function () {
                var d = b.dataset;
                document.getElementById('modal_idcita').value = d.idcita;
                document.getElementById('modal_idpaciente').value = d.idpaciente;
                document.getElementById('modal_idhistoria').value = d.idhistoria;
                document.getElementById('modal_idmedico').value = d.idmedico;
                document.getElementById('modal_nombrepaciente').textContent = d.paciente;
                document.getElementById('modal_peso').textContent = d.peso;
                document.getElementById('modal_talla').textContent = d.talla;
                document.getElementById('modal_temp').textContent = d.temp;
                document.getElementById('modal_presion').textContent = d.presion;
                document.getElementById('modal_fc').textContent = d.fc;
                document.getElementById('modal_obs').textContent = d.obs;
                document.getElementById('modalAtencion').classList.add('is-open');
            });
        });
    </script>
</body>
</html>