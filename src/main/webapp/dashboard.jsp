<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    if (session.getAttribute("usuarioLogueado") == null) {
        response.sendRedirect("index.jsp"); return;
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Panel de control · Clínica UPN</title>
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
                    <div class="topbar__title">Panel de control</div>
                    <div class="topbar__sub">Hola, ${sessionScope.usuarioLogueado.username} · aquí tienes tu resumen</div>
                </div>
                <div class="topbar__spacer"></div>
                <a href="logout" class="c-btn c-btn--ghost c-btn--sm"><i class="ti ti-logout"></i> Salir</a>
            </header>

            <div class="app-content stack-lg">

                <c:if test="${sessionScope.usuarioLogueado.idRol == 1}">
                    <div class="c-stats">
                        <div class="c-stat">
                            <div class="c-stat__top">Usuarios registrados <span class="c-stat__icon ic-brand"><i class="ti ti-users"></i></span></div>
                            <div class="c-stat__num">${stats.usuarios != null ? stats.usuarios : 0}</div>
                            <div class="c-stat__foot">Cuentas activas en el sistema</div>
                        </div>
                        <div class="c-stat">
                            <div class="c-stat__top">Citas de hoy <span class="c-stat__icon ic-info"><i class="ti ti-calendar-check"></i></span></div>
                            <div class="c-stat__num">${stats.citasHoy != null ? stats.citasHoy : 0}</div>
                            <div class="c-stat__foot">Programadas para la jornada</div>
                        </div>
                        <div class="c-stat">
                            <div class="c-stat__top">Triajes pendientes <span class="c-stat__icon ic-warn"><i class="ti ti-clipboard-pulse"></i></span></div>
                            <div class="c-stat__num">${stats.triajes != null ? stats.triajes : 0}</div>
                            <div class="c-stat__foot">En cola para enfermería</div>
                        </div>
                    </div>
                </c:if>

                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-rocket"></i> Accesos rápidos</div>
                    </div>
                    <div class="c-card__body">
                        <div class="grid-2">
                            <c:if test="${sessionScope.usuarioLogueado.idRol == 1}">
                                <a href="UsuarioServlet" class="c-btn c-btn--ghost"><i class="ti ti-users-group"></i> Gestionar usuarios</a>
                                <a href="MedicoServlet" class="c-btn c-btn--ghost"><i class="ti ti-stethoscope"></i> Gestionar médicos</a>
                                <a href="PacienteServlet" class="c-btn c-btn--ghost"><i class="ti ti-user-heart"></i> Gestionar pacientes</a>
                                <a href="ReportesServlet" class="c-btn c-btn--ghost"><i class="ti ti-chart-histogram"></i> Ver reportes</a>
                            </c:if>
                            <c:if test="${sessionScope.usuarioLogueado.idRol == 2}">
                                <a href="AtencionServlet" class="c-btn c-btn--ghost"><i class="ti ti-calendar-heart"></i> Mi agenda del día</a>
                                <a href="HistoriaGeneralServlet" class="c-btn c-btn--ghost"><i class="ti ti-file-medical"></i> Historias clínicas</a>
                                <a href="EvaluacionServlet" class="c-btn c-btn--ghost"><i class="ti ti-clipboard-check"></i> Evaluar practicantes</a>
                            </c:if>
                            <c:if test="${sessionScope.usuarioLogueado.idRol == 3}">
                                <a href="TriajeServlet" class="c-btn c-btn--ghost"><i class="ti ti-clipboard-heart"></i> Registrar triaje</a>
                            </c:if>
                            <c:if test="${sessionScope.usuarioLogueado.idRol == 4}">
                                <a href="CitaServlet" class="c-btn c-btn--ghost"><i class="ti ti-calendar-plus"></i> Solicitar una cita</a>
                                <a href="HistorialServlet" class="c-btn c-btn--ghost"><i class="ti ti-notes-medical"></i> Ver mi historial</a>
                            </c:if>
                            <c:if test="${sessionScope.usuarioLogueado.idRol == 5}">
                                <a href="AgendaRecepcionServlet" class="c-btn c-btn--ghost"><i class="ti ti-calendar-event"></i> Agenda de citas</a>
                                <a href="PagoServlet" class="c-btn c-btn--ghost"><i class="ti ti-cash"></i> Cobros</a>
                                <a href="PacienteServlet" class="c-btn c-btn--ghost"><i class="ti ti-user-heart"></i> Gestionar pacientes</a>
                            </c:if>
                            <c:if test="${sessionScope.usuarioLogueado.idRol == 6}">
                                <a href="TriajeServlet" class="c-btn c-btn--ghost"><i class="ti ti-clipboard-pulse"></i> Registrar triaje</a>
                            </c:if>
                            
                        </div>
                    </div>
                </div>

                <div class="c-alert c-alert--info">
                    <i class="ti ti-info-circle"></i>
                    <span>Tu sesión está protegida. Usa el menú lateral para navegar según tu rol dentro de la clínica.</span>
                </div>

            </div>
        </div>
    </div>
    <script src="assets/js/app.js"></script>
</body>
</html>