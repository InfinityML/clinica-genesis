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
    <title>Mi historial · Clínica UPN</title>
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
                    <div class="topbar__title">Mi historial médico</div>
                    <div class="topbar__sub">Consultas y recetas anteriores</div>
                </div>
            </header>

            <div class="app-content">
                <c:if test="${empty historial}">
                    <div class="c-alert c-alert--info">
                        <i class="ti ti-file-search"></i>
                        <span>Aún no tienes atenciones registradas. Cuando un médico te atienda, aparecerá aquí.</span>
                    </div>
                </c:if>

                <div class="grid-2">
                    <c:forEach var="h" items="${historial}">
                        <div class="c-card">
                            <div class="c-card__head">
                                <div class="c-card__title"><i class="ti ti-calendar-event"></i> ${h.fecha}</div>
                                <span class="c-badge c-badge--muted"><i class="ti ti-clock"></i> ${h.hora}</span>
                            </div>
                            <div class="c-card__body">
                                <div class="text-strong" style="color:var(--brand-700);">${h.medico}</div>
                                <div class="muted" style="margin-bottom:14px;"><i class="ti ti-stethoscope"></i> ${h.especialidad}</div>

                                <div class="field" style="margin-bottom:10px;">
                                    <label>Motivo de consulta</label>
                                    <div class="c-alert c-alert--muted" style="margin:0;">${h.motivo}</div>
                                </div>
                                <div class="field" style="margin-bottom:14px;">
                                    <label>Diagnóstico</label>
                                    <div class="c-alert c-alert--muted" style="margin:0;">${h.diagnostico}</div>
                                </div>

                                <div class="text-strong" style="color:var(--brand-700); margin-bottom:8px;"><i class="ti ti-pill"></i> Receta médica</div>
                                <c:choose>
                                    <c:when test="${h.medicamento != 'Sin medicación'}">
                                        <div class="c-alert c-alert--success" style="margin:0; flex-direction:column; align-items:stretch; gap:2px;">
                                            <span><strong>Medicamento:</strong> ${h.medicamento}</span>
                                            <span><strong>Dosis:</strong> ${h.dosis}</span>
                                            <span><strong>Frecuencia:</strong> ${h.frecuencia}</span>
                                            <span><strong>Duración:</strong> ${h.duracion}</span>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <div class="muted">No se recetó medicación en esta consulta.</div>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>
    <script src="assets/js/app.js"></script>
</body>
</html>