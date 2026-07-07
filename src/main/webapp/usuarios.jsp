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
    <title>Usuarios · Clínica UPN</title>
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
                    <div class="topbar__title">Usuarios</div>
                    <div class="topbar__sub">Cuentas de acceso al sistema</div>
                </div>
                <div class="topbar__spacer"></div>
                <button class="c-btn c-btn--primary" data-modal-open="modalNuevo">
                    <i class="ti ti-plus"></i> Nuevo usuario
                </button>
            </header>

            <div class="app-content">
                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-users-group"></i> Cuentas registradas</div>
                        <span class="muted" style="font-size:13px;">${usuarios.size()} cuentas</span>
                    </div>
                    <table class="c-table">
                        <thead>
                            <tr>
                                <th>Usuario</th>
                                <th>Correo</th>
                                <th>Rol</th>
                                <th>Estado</th>
                                <th style="text-align:right;">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="u" items="${usuarios}">
                                <tr>
                                    <td class="text-strong">${u.username}</td>
                                    <td>${u.email}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${u.idRol == 1}"><span class="c-badge c-badge--danger">Administrador</span></c:when>
                                            <c:when test="${u.idRol == 2}"><span class="c-badge c-badge--info">Doctor</span></c:when>
                                            <c:when test="${u.idRol == 3}"><span class="c-badge c-badge--info">Practicante</span></c:when>
                                            <c:when test="${u.idRol == 4}"><span class="c-badge c-badge--muted">Paciente</span></c:when>
                                            <c:when test="${u.idRol == 5}"><span class="c-badge c-badge--warning">Recepcionista</span></c:when>
                                            <c:when test="${u.idRol == 6}"><span class="c-badge c-badge--success">Enfermero</span></c:when>
                                            <c:otherwise><span class="c-badge c-badge--muted">—</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${u.estado == 1}"><span class="c-badge c-badge--success">Activo</span></c:when>
                                            <c:otherwise><span class="c-badge c-badge--danger">Inactivo</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="table-actions" style="justify-content:flex-end;">
                                            <button class="c-btn c-btn--ghost btn-editar"
                                                    data-id="${u.idUsuario}" data-username="${u.username}"
                                                    data-email="${u.email}" data-rol="${u.idRol}">
                                                <i class="ti ti-pencil"></i>
                                            </button>
                                            <a class="c-btn c-btn--danger" href="UsuarioServlet?accion=eliminar&id=${u.idUsuario}"
                                               onclick="return confirm('¿Desactivar esta cuenta?');">
                                                <i class="ti ti-trash"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty usuarios}">
                                <tr><td colspan="5" class="muted" style="text-align:center; padding:28px;">No hay cuentas registradas.</td></tr>
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
            <form action="UsuarioServlet" method="POST">
                <input type="hidden" name="accion" value="crear">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-user-plus"></i> Nuevo usuario</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="field"><label>Usuario</label><input class="c-input" type="text" name="username" required></div>
                    <div class="field"><label>Correo electrónico</label><input class="c-input" type="email" name="email" required></div>
                    <div class="field"><label>Contraseña temporal</label><input class="c-input" type="password" name="password" required></div>
                    <div class="field">
                        <label>Rol del sistema</label>
                        <select class="c-select" name="rol" required>
                            <option value="" selected disabled>Selecciona un rol...</option>
                            <option value="1">Administrador</option>
                            <option value="2">Doctor</option>
                            <option value="3">Practicante</option>
                            <option value="4">Paciente</option>
                            <option value="5">Recepcionista</option>
                            <option value="6">Enfermero</option>
                        </select>
                    </div>
                </div>
                <div class="c-modal__foot">
                    <button type="button" class="c-btn c-btn--ghost" data-modal-close>Cancelar</button>
                    <button type="submit" class="c-btn c-btn--primary"><i class="ti ti-device-floppy"></i> Crear cuenta</button>
                </div>
            </form>
        </div>
    </div>

    <!-- Modal: editar -->
    <div class="c-modal" id="modalEditar">
        <div class="c-modal__dialog">
            <form action="UsuarioServlet" method="POST">
                <input type="hidden" name="accion" value="editar">
                <input type="hidden" name="idUsuario" id="ed_id">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-pencil"></i> Editar usuario</div>
                    <button type="button" class="c-modal__close" data-modal-close><i class="ti ti-x"></i></button>
                </div>
                <div class="c-modal__body">
                    <div class="field"><label>Usuario</label><input class="c-input" type="text" name="username" id="ed_username" required></div>
                    <div class="field"><label>Correo electrónico</label><input class="c-input" type="email" name="email" id="ed_email" required></div>
                    <div class="field">
                        <label>Rol del sistema</label>
                        <select class="c-select" name="rol" id="ed_rol" required>
                            <option value="1">Administrador</option>
                            <option value="2">Doctor</option>
                            <option value="3">Practicante</option>
                            <option value="4">Paciente</option>
                            <option value="5">Recepcionista</option>
                            <option value="6">Enfermero</option>
                        </select>
                    </div>
                    <div class="hint">La contraseña no se modifica desde aquí.</div>
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
                document.getElementById('ed_username').value = b.dataset.username;
                document.getElementById('ed_email').value = b.dataset.email;
                document.getElementById('ed_rol').value = b.dataset.rol;
                document.getElementById('modalEditar').classList.add('is-open');
            });
        });
    </script>
</body>
</html>