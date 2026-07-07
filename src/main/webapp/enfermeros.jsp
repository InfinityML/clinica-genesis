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
    <title>Enfermeros · Clínica UPN</title>
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
                    <div class="topbar__title">Enfermeros</div>
                    <div class="topbar__sub">Gestión del personal de enfermería</div>
                </div>
                <div class="topbar__spacer"></div>
                <button class="c-btn c-btn--primary" data-modal-open="modalNuevo">
                    <i class="ti ti-plus"></i> Registrar enfermero
                </button>
            </header>

            <div class="app-content">
                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-nurse"></i> Directorio de enfermería</div>
                        <span class="muted" style="font-size:13px;">${enfermeros.size()} registrados</span>
                    </div>
                    <table class="c-table">
                        <thead>
                            <tr>
                                <th>Nombre completo</th>
                                <th>DNI</th>
                                <th>Teléfono</th>
                                <th>Cuenta</th>
                                <th style="text-align:right;">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="e" items="${enfermeros}">
                                <tr>
                                    <td class="text-strong">${e.nombre} ${e.apellido}</td>
                                    <td>${e.dni}</td>
                                    <td>${e.telefono != null ? e.telefono : '—'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${e.username != null}"><span class="c-badge c-badge--success"><i class="ti ti-user-check"></i> ${e.username}</span></c:when>
                                            <c:otherwise><span class="c-badge c-badge--muted">Sin cuenta</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="table-actions" style="justify-content:flex-end;">
                                            <button class="c-btn c-btn--ghost btn-editar"
                                                    data-id="${e.idEnfermero}" data-nombre="${e.nombre}"
                                                    data-apellido="${e.apellido}" data-dni="${e.dni}"
                                                    data-telefono="${e.telefono}">
                                                <i class="ti ti-pencil"></i>
                                            </button>
                                            <a class="c-btn c-btn--danger" href="EnfermeroServlet?accion=eliminar&id=${e.idEnfermero}"
                                               onclick="return confirm('¿Desactivar a este enfermero?');">
                                                <i class="ti ti-trash"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty enfermeros}">
                                <tr><td colspan="5" class="muted" style="text-align:center; padding:28px;">Aún no hay enfermeros registrados.</td></tr>
                            </c:if>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>
    </div>

    <!-- Modal: nuevo -->
    <div class="c-modal" id="modalNuevo">
        <div class="c-modal__dialog">
            <form action="EnfermeroServlet" method="POST">
                <input type="hidden" name="accion" value="crear">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-nurse"></i> Registrar enfermero</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="form-row">
                        <div class="field"><label>Nombres</label><input class="c-input" type="text" name="nombre" required></div>
                        <div class="field"><label>Apellidos</label><input class="c-input" type="text" name="apellido" required></div>
                    </div>
                    <div class="form-row">
                        <div class="field"><label>DNI</label><input class="c-input" type="text" name="dni" maxlength="8" pattern="\d{8}" required></div>
                        <div class="field"><label>Teléfono</label><input class="c-input" type="text" name="telefono" maxlength="20"></div>
                    </div>
                    <div class="field">
                        <label>Vincular cuenta de usuario (rol Enfermero)</label>
                        <select class="c-select" name="idUsuario">
                            <option value="">— Sin cuenta —</option>
                            <c:forEach var="u" items="${usuarios}">
                                <c:if test="${u.idRol == 6}">
                                    <option value="${u.idUsuario}">${u.username}</option>
                                </c:if>
                            </c:forEach>
                        </select>
                        <div class="hint">Primero crea el usuario con rol Enfermero en la sección Usuarios.</div>
                    </div>
                </div>
                <div class="c-modal__foot">
                    <button type="button" class="c-btn c-btn--ghost" data-modal-close>Cancelar</button>
                    <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-device-floppy"></i> Guardar</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal: editar -->
    <div class="c-modal" id="modalEditar">
        <div class="c-modal__dialog">
            <form action="EnfermeroServlet" method="POST">
                <input type="hidden" name="accion" value="editar">
                <input type="hidden" name="idEnfermero" id="ed_id">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-pencil"></i> Editar enfermero</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="form-row">
                        <div class="field"><label>Nombres</label><input class="c-input" type="text" name="nombre" id="ed_nombre" required></div>
                        <div class="field"><label>Apellidos</label><input class="c-input" type="text" name="apellido" id="ed_apellido" required></div>
                    </div>
                    <div class="form-row">
                        <div class="field"><label>DNI</label><input class="c-input" type="text" name="dni" id="ed_dni" maxlength="8" pattern="\d{8}" required></div>
                        <div class="field"><label>Teléfono</label><input class="c-input" type="text" name="telefono" id="ed_telefono" maxlength="20"></div>
                    </div>
                </div>
                <div class="c-modal__foot">
                    <button type="button" class="c-btn c-btn--ghost" data-modal-close>Cancelar</button>
                    <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-device-floppy"></i> Actualizar</button>
                </div>
            </form>
        </div>
    </div>

    <script src="assets/js/app.js"></script>
    <script>
        document.querySelectorAll('.btn-editar').forEach(function (b) {
            b.addEventListener('click', function () {
                document.getElementById('ed_id').value = b.dataset.id;
                document.getElementById('ed_nombre').value = b.dataset.nombre;
                document.getElementById('ed_apellido').value = b.dataset.apellido;
                document.getElementById('ed_dni').value = b.dataset.dni;
                document.getElementById('ed_telefono').value = (b.dataset.telefono === 'null') ? '' : b.dataset.telefono;
                document.getElementById('modalEditar').classList.add('is-open');
            });
        });
    </script>
</body>
</html>