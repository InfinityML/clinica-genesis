<%@page pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%
    String __u = "";
    Object __ul = session.getAttribute("usuarioLogueado");
    if (__ul != null) {
        try { __u = (String) __ul.getClass().getMethod("getUsername").invoke(__ul); } catch (Exception e) {}
    }
    String avatarIni = "?";
    if (__u != null && !__u.isEmpty()) {
        String[] __p = __u.trim().split("[ _.]+");
        avatarIni = String.valueOf(Character.toUpperCase(__p[0].charAt(0)));
        if (__p.length > 1 && !__p[1].isEmpty()) avatarIni += Character.toUpperCase(__p[1].charAt(0));
        else if (__p[0].length() > 1) avatarIni += Character.toUpperCase(__p[0].charAt(1));
    }
    request.setAttribute("avatarIni", avatarIni);
%>
<div class="app-overlay"></div>
<aside class="app-sidebar">
    <div class="app-brand">
        <div class="app-brand__mark"><i class="ti ti-heartbeat"></i></div>
        <div>
            <div class="app-brand__name">Clínica UPN</div>
            <div class="app-brand__sub">Sistema clínico</div>
        </div>
    </div>

    <nav class="app-nav">
        <a href="DashboardServlet"><i class="ti ti-layout-dashboard"></i> Inicio</a>

        <c:if test="${sessionScope.usuarioLogueado.idRol == 1}">
            <div class="app-nav__label">Administración</div>
            <a href="UsuarioServlet"><i class="ti ti-users-group"></i> Usuarios</a>
            <a href="MedicoServlet"><i class="ti ti-stethoscope"></i> Médicos</a>
            <a href="PacienteServlet"><i class="ti ti-user-heart"></i> Pacientes</a>
            <a href="PracticanteServlet"><i class="ti ti-school"></i> Practicantes</a>
            <a href="EnfermeroServlet"><i class="ti ti-nurse"></i> Enfermeros</a>
            <a href="RecepcionistaServlet"><i class="ti ti-headset"></i> Recepcionistas</a>
            <a href="ReportesServlet"><i class="ti ti-chart-histogram"></i> Reportes</a>
        </c:if>

        <c:if test="${sessionScope.usuarioLogueado.idRol == 2}">
            <div class="app-nav__label">Consulta</div>
            <a href="AtencionServlet"><i class="ti ti-calendar-heart"></i> Mi agenda</a>
            <a href="HistoriaGeneralServlet"><i class="ti ti-file-medical"></i> Historias clínicas</a>
            <a href="EvaluacionServlet"><i class="ti ti-clipboard-check"></i> Evaluar practicantes</a>
        </c:if>

        <c:if test="${sessionScope.usuarioLogueado.idRol == 6}">
            <div class="app-nav__label">Enfermería</div>
            <a href="TriajeServlet"><i class="ti ti-clipboard-pulse"></i> Registrar triaje</a>
        </c:if>

        <c:if test="${sessionScope.usuarioLogueado.idRol == 3}">
            <div class="app-nav__label">Prácticas</div>
            <a href="TriajeServlet"><i class="ti ti-clipboard-pulse"></i> Apoyo en triaje</a>
        </c:if>

        <c:if test="${sessionScope.usuarioLogueado.idRol == 5}">
            <div class="app-nav__label">Recepción</div>
            <a href="AgendaRecepcionServlet"><i class="ti ti-calendar-event"></i> Agenda de citas</a>
            <a href="PagoServlet"><i class="ti ti-cash"></i> Cobros</a>
            <a href="PacienteServlet"><i class="ti ti-user-heart"></i> Pacientes</a>
        </c:if>

        <c:if test="${sessionScope.usuarioLogueado.idRol == 4}">
            <div class="app-nav__label">Paciente</div>
            <a href="CitaServlet"><i class="ti ti-calendar-plus"></i> Solicitar cita</a>
            <a href="HistorialServlet"><i class="ti ti-notes-medical"></i> Mi historial</a>
        </c:if>
    </nav>

    <div class="app-user">
        <div class="app-user__avatar">${avatarIni}</div>
        <div style="min-width:0;">
            <div class="app-user__name">${sessionScope.usuarioLogueado.username}</div>
            <div class="app-user__role">
                <c:choose>
                    <c:when test="${sessionScope.usuarioLogueado.idRol == 1}">Administrador</c:when>
                    <c:when test="${sessionScope.usuarioLogueado.idRol == 2}">Doctor</c:when>
                    <c:when test="${sessionScope.usuarioLogueado.idRol == 3}">Practicante</c:when>
                    <c:when test="${sessionScope.usuarioLogueado.idRol == 4}">Paciente</c:when>
                    <c:when test="${sessionScope.usuarioLogueado.idRol == 5}">Recepcionista</c:when>
                    <c:when test="${sessionScope.usuarioLogueado.idRol == 6}">Enfermero</c:when>
                    <c:otherwise>Usuario</c:otherwise>
                </c:choose>
            </div>
        </div>
        <a href="logout" class="app-user__logout" title="Cerrar sesión"><i class="ti ti-logout"></i></a>
    </div>
</aside>