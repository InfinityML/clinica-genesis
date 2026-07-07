<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.clinica.modelo.Usuario" %>
<%
    Usuario __usr = (Usuario) session.getAttribute("usuarioLogueado");
    if (__usr == null) { response.sendRedirect("index.jsp"); return; }
    if (__usr.getIdRol() != 1) { response.sendRedirect("DashboardServlet"); return; }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Reportes · Clínica UPN</title>
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
                    <div class="topbar__title">Reportes</div>
                    <div class="topbar__sub">Rendimiento de la clínica por especialidad</div>
                </div>
                <div class="topbar__spacer"></div>
                <div class="table-actions">
                    <a class="c-btn c-btn--ghost" href="ExportarReporteServlet?formato=pdf"><i class="ti ti-file-type-pdf"></i> PDF</a>
                    <a class="c-btn c-btn--ghost" href="ExportarReporteServlet?formato=excel"><i class="ti ti-file-type-xls"></i> Excel</a>
                    <a class="c-btn c-btn--ghost" href="ExportarReporteServlet?formato=csv"><i class="ti ti-file-type-csv"></i> CSV</a>
                </div>
                <button class="c-btn c-btn--ghost" onclick="window.print()"><i class="ti ti-printer"></i> Imprimir</button>
            </header>

            <div class="app-content">
                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-chart-pie"></i> Rendimiento por especialidad</div>
                    </div>
                    <table class="c-table">
                        <thead>
                            <tr>
                                <th>Especialidad</th>
                                <th>Total citas</th>
                                <th>Atendidas</th>
                                <th>Pendientes</th>
                                <th style="width:220px;">Eficiencia</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="r" items="${reporte}">
                                <tr>
                                    <td class="text-strong">${r.especialidad}</td>
                                    <td>${r.total}</td>
                                    <td style="color:var(--brand-700); font-weight:600;">${r.atendidas}</td>
                                    <td style="color:var(--warning); font-weight:600;">${r.pendientes}</td>
                                    <td>
                                        <c:set var="porcentaje" value="${r.total > 0 ? (r.atendidas * 100.0) / r.total : 0.0}" />
                                        <div style="display:flex; align-items:center; gap:10px;">
                                            <div class="c-progress" style="flex:1;">
                                                <div class="c-progress__bar" style="width: ${porcentaje}%;"></div>
                                            </div>
                                            <span class="text-strong" style="font-size:13px; min-width:38px; text-align:right;">${String.format("%.0f", porcentaje)}%</span>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty reporte}">
                                <tr><td colspan="5" class="muted" style="text-align:center; padding:28px;">No hay datos de citas para reportar.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>
    <script src="assets/js/app.js"></script>
</body>
</html>