<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.clinica.modelo.Usuario" %>
<%
    Usuario __usr = (Usuario) session.getAttribute("usuarioLogueado");
    if (__usr == null) { response.sendRedirect("index.jsp"); return; }
    if (__usr.getIdRol() != 4) { response.sendRedirect("DashboardServlet"); return; }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Mis citas · Clínica UPN</title>
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
                    <div class="topbar__title">Portal del paciente</div>
                    <div class="topbar__sub">Solicita citas y únete a tus teleconsultas</div>
                </div>
                <div class="topbar__spacer"></div>
                <button class="c-btn c-btn--primary" data-modal-open="modalCita">
                    <i class="ti ti-calendar-plus"></i> Solicitar cita
                </button>
            </header>

            <div class="app-content">
                
                <c:if test="${param.tele == 'pendiente'}">
                    <div class="c-alert c-alert--info" style="margin-bottom:16px;"><i class="ti ti-clock-pause"></i><span>Tu teleconsulta aún no ha sido aceptada por el médico. Podrás unirte cuando la confirme.</span></div>
                </c:if>
                <c:if test="${param.tele == 'fuera'}">
                    <div class="c-alert c-alert--muted" style="margin-bottom:16px;"><i class="ti ti-clock"></i><span>Podrás unirte a tu teleconsulta desde 15 minutos antes de la hora de tu cita.</span></div>
                </c:if>
                    
                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-calendar-heart"></i> Mis citas</div>
                        <span class="muted" style="font-size:13px;">${misCitas.size()} en total</span>
                    </div>
                    <table class="c-table">
                        <thead>
                            <tr>
                                <th>Fecha y hora</th>
                                <th>Médico</th>
                                <th>Especialidad</th>
                                <th>Modalidad</th>
                                <th>Estado</th>
                                <th style="text-align:right;">Teleconsulta</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${misCitas}">
                                <tr>
                                    <td><span class="text-strong">${c.fecha}</span> <span class="muted">${c.hora}</span></td>
                                    <td>Dr. ${c.medico}</td>
                                    <td>${c.especialidad}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.modalidad == 'Teleconsulta'}"><span class="c-badge c-badge--info"><i class="ti ti-video"></i> Teleconsulta</span></c:when>
                                            <c:otherwise><span class="c-badge c-badge--muted"><i class="ti ti-building-hospital"></i> Presencial</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${c.estado == 'Atendida'}"><span class="c-badge c-badge--success">Atendida</span></c:when>
                                            <c:when test="${c.estado == 'Cancelada'}"><span class="c-badge c-badge--danger">Cancelada</span></c:when>
                                            <c:otherwise><span class="c-badge c-badge--warning">Pendiente</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td style="text-align:right;">
                                        <c:choose>
                                            <c:when test="${c.estadoUnion == 'disponible'}">
                                                <a class="c-btn c-btn--primary c-btn--sm" href="VideollamadaServlet?idCita=${c.id_cita}">
                                                    <i class="ti ti-video"></i> Unirse
                                                </a>
                                            </c:when>
                                            <c:when test="${c.estadoUnion == 'pendiente_confirmacion'}">
                                                <span class="c-badge c-badge--warning"><i class="ti ti-clock-pause"></i> Esperando confirmación</span>
                                            </c:when>
                                            <c:when test="${c.estadoUnion == 'fuera_de_hora'}">
                                                <span class="c-badge c-badge--muted"><i class="ti ti-clock"></i> Disponible a la hora</span>
                                            </c:when>
                                            <c:otherwise><span class="muted">—</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty misCitas}">
                                <tr><td colspan="6" class="muted" style="text-align:center; padding:28px;">Aún no tienes citas. Solicita la primera con el botón de arriba.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: solicitar cita -->
    <div class="c-modal" id="modalCita">
        <div class="c-modal__dialog">
            <form action="CitaServlet" method="POST">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-calendar-plus"></i> Solicitar nueva cita</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="field">
                        <label>Especialista</label>
                        <select class="c-select" name="idMedico" required>
                            <option value="">— Selecciona un médico —</option>
                            <c:forEach var="m" items="${medicos}">
                                <option value="${m.idMedico}">Dr. ${m.nombre} ${m.apellido} · ${m.nombreEspecialidad}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="form-row">
                        <div class="field"><label>Fecha</label><input class="c-input" type="date" name="fecha" required></div>
                        <div class="field"><label>Hora</label><input class="c-input" type="time" name="hora" required></div>
                    </div>
                    <div class="field">
                        <label>Modalidad</label>
                        <select class="c-select" name="modalidad" required>
                            <option value="Presencial">Presencial</option>
                            <option value="Teleconsulta">Teleconsulta virtual</option>
                        </select>
                    </div>
                </div>
                <div class="c-modal__foot">
                    <button type="button" class="c-btn c-btn--ghost" data-modal-close>Cancelar</button>
                    <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-calendar-check"></i> Agendar</button>
                </div>
            </form>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
</body>
</html>