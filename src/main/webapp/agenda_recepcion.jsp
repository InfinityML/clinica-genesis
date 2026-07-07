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
    <title>Agenda · Clínica UPN</title>
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
                    <div class="topbar__title">Agenda de citas</div>
                    <div class="topbar__sub">Programa y consulta las citas de la clínica</div>
                </div>
                <div class="topbar__spacer"></div>
                <button class="c-btn c-btn--primary" data-modal-open="modalCita">
                    <i class="ti ti-calendar-plus"></i> Agendar cita
                </button>
            </header>

            <div class="app-content">
                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-calendar-event"></i> Citas registradas</div>
                        <span class="muted" style="font-size:13px;">${citas.size()} en total</span>
                    </div>
                    <table class="c-table">
                        <thead>
                            <tr>
                                <th>Fecha y hora</th>
                                <th>Paciente</th>
                                <th>DNI</th>
                                <th>Médico</th>
                                <th>Modalidad</th>
                                <th>Estado</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="c" items="${citas}">
                                <tr>
                                    <td><span class="text-strong">${c.fecha}</span> <span class="muted">${c.hora}</span></td>
                                    <td>${c.paciente}</td>
                                    <td>${c.dni}</td>
                                    <td>Dr. ${c.medico}</td>
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
                                </tr>
                            </c:forEach>
                            <c:if test="${empty citas}">
                                <tr><td colspan="6" class="muted" style="text-align:center; padding:28px;">Aún no hay citas registradas.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: agendar cita -->
    <div class="c-modal" id="modalCita">
        <div class="c-modal__dialog">
            <form action="AgendaRecepcionServlet" method="POST">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-calendar-plus"></i> Agendar nueva cita</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="field">
                        <label>Paciente</label>
                        <select class="c-select" name="idPaciente" required>
                            <option value="">— Selecciona un paciente —</option>
                            <c:forEach var="p" items="${pacientes}">
                                <option value="${p.idPaciente}">${p.nombre} ${p.apellido} · ${p.dni}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="field">
                        <label>Médico</label>
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
                    <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-device-floppy"></i> Agendar</button>
                </div>
            </form>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
</body>
</html>