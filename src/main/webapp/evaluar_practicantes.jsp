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
    <title>Evaluar practicantes · Clínica UPN</title>
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
                    <div class="topbar__title">Evaluar practicantes</div>
                    <div class="topbar__sub">Practicantes a tu cargo</div>
                </div>
            </header>

            <div class="app-content">
                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-clipboard-check"></i> Mis practicantes</div>
                    </div>
                    <table class="c-table">
                        <thead>
                            <tr>
                                <th>Cód. universitario</th>
                                <th>Practicante</th>
                                <th>Evaluaciones</th>
                                <th style="text-align:right;">Acción</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${misPracticantes}">
                                <tr>
                                    <td class="text-strong">${p.codigo}</td>
                                    <td>${p.nombre} ${p.apellido}</td>
                                    <td><span class="c-badge c-badge--muted">${p.evaluaciones} registradas</span></td>
                                    <td style="text-align:right;">
                                        <button class="c-btn c-btn--primary c-btn--sm btn-evaluar"
                                                data-id="${p.id_practicante}" data-nombre="${p.nombre} ${p.apellido}">
                                            <i class="ti ti-star"></i> Calificar
                                        </button>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty misPracticantes}">
                                <tr><td colspan="4" class="muted" style="text-align:center; padding:28px;">No tienes practicantes asignados actualmente.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: evaluación -->
    <div class="c-modal" id="modalEvaluacion">
        <div class="c-modal__dialog">
            <form action="EvaluacionServlet" method="POST">
                <input type="hidden" name="idPracticante" id="eval_id">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-clipboard-check"></i> Evaluación de desempeño</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="c-alert c-alert--info" style="margin-bottom:16px;">
                        <i class="ti ti-user"></i>
                        <span>Alumno: <strong id="eval_nombre"></strong> · califica del <strong>0 al 20</strong>.</span>
                    </div>
                    <div class="form-row" style="grid-template-columns:1fr 1fr 1fr;">
                        <div class="field"><label>Puntualidad</label><input class="c-input" type="number" min="0" max="20" name="puntualidad" required></div>
                        <div class="field"><label>Conocimiento</label><input class="c-input" type="number" min="0" max="20" name="conocimiento" required></div>
                        <div class="field"><label>Desempeño</label><input class="c-input" type="number" min="0" max="20" name="desempeno" required></div>
                    </div>
                    <div class="field"><label>Retroalimentación</label><textarea class="c-textarea" name="comentario" rows="3" required placeholder="Observaciones sobre su desempeño..."></textarea></div>
                </div>
                <div class="c-modal__foot">
                    <button type="button" class="c-btn c-btn--ghost" data-modal-close>Cancelar</button>
                    <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-device-floppy"></i> Guardar nota</button>
                </div>
            </form>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        document.querySelectorAll('.btn-evaluar').forEach(function (b) {
            b.addEventListener('click', function () {
                document.getElementById('eval_id').value = b.dataset.id;
                document.getElementById('eval_nombre').textContent = b.dataset.nombre;
                document.getElementById('modalEvaluacion').classList.add('is-open');
            });
        });
    </script>
</body>
</html>