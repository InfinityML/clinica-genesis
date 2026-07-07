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
    <title>Historias clínicas · Clínica UPN</title>
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
                    <div class="topbar__title">Historias clínicas</div>
                    <div class="topbar__sub">Consulta el historial médico por DNI</div>
                </div>
            </header>

            <div class="app-content stack-lg">
                <div class="c-card">
                    <div class="c-card__body">
                        <form action="HistoriaGeneralServlet" method="POST" style="display:flex; gap:10px;">
                            <div class="input-group" style="flex:1;">
                                <i class="ti ti-search"></i>
                                <input class="c-input" type="text" name="dni" placeholder="Ingresa el DNI del paciente..." maxlength="8" pattern="\d{8}" value="${dniBuscado}" required>
                            </div>
                            <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-search"></i> Buscar</button>
                        </form>
                    </div>
                </div>

                <c:if test="${not empty historialDNI}">
                    <div class="c-alert c-alert--success"><i class="ti ti-circle-check"></i><span>Resultados encontrados para el DNI <strong>${dniBuscado}</strong>.</span></div>
                    <div class="c-card">
                        <table class="c-table">
                            <thead>
                                <tr><th>Fecha</th><th>Médico</th><th>Especialidad</th><th>Motivo</th><th>Diagnóstico</th><th>Medicación</th></tr>
                            </thead>
                            <tbody>
                                <c:forEach var="h" items="${historialDNI}">
                                    <tr>
                                        <td class="text-strong">${h.fecha}</td>
                                        <td>${h.medico}</td>
                                        <td><span class="c-badge c-badge--info">${h.especialidad}</span></td>
                                        <td>${h.motivo}</td>
                                        <td>${h.diagnostico}</td>
                                        <td class="text-strong" style="color:var(--brand-700);">${h.medicamento}</td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:if>

                <c:if test="${dniBuscado != null && empty historialDNI}">
                    <div class="c-alert c-alert--danger"><i class="ti ti-alert-triangle"></i><span>No se encontraron registros médicos para el DNI <strong>${dniBuscado}</strong>.</span></div>
                </c:if>
            </div>
        </div>
    </div>
    <script src="assets/js/app.js"></script>
</body>
</html>