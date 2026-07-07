<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.clinica.modelo.Usuario" %>
<%
    Usuario __usr = (Usuario) session.getAttribute("usuarioLogueado");
    if (__usr == null) { response.sendRedirect("index.jsp"); return; }
    if (__usr.getIdRol() != 1 && __usr.getIdRol() != 5) { response.sendRedirect("DashboardServlet"); return; }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Pacientes · Clínica UPN</title>
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
                    <div class="topbar__title">Pacientes</div>
                    <div class="topbar__sub">Registro y directorio de pacientes</div>
                </div>
                <div class="topbar__spacer"></div>
                <button class="c-btn c-btn--primary" data-modal-open="modalNuevo">
                    <i class="ti ti-plus"></i> Registrar paciente
                </button>
            </header>

            <div class="app-content">
                <div class="c-card">
                    <div class="c-card__head">
                        <div class="c-card__title"><i class="ti ti-user-heart"></i> Directorio de pacientes</div>
                        <span class="muted" style="font-size:13px;">${pacientes.size()} registrados</span>
                    </div>
                    <table class="c-table">
                        <thead>
                            <tr>
                                <th>Nombre completo</th>
                                <th>DNI</th>
                                <th>F. nacimiento</th>
                                <th>Teléfono</th>
                                <th>Cuenta</th>
                                <th style="text-align:right;">Acciones</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="p" items="${pacientes}">
                                <tr>
                                    <td class="text-strong">${p.nombre} ${p.apellido}</td>
                                    <td>${p.dni}</td>
                                    <td>${p.fechaNacimiento}</td>
                                    <td>${p.telefono != null ? p.telefono : '—'}</td>
                                    <td>
                                        <c:choose>
                                            <c:when test="${p.username != null}"><span class="c-badge c-badge--success"><i class="ti ti-user-check"></i> ${p.username}</span></c:when>
                                            <c:otherwise><span class="c-badge c-badge--muted">Sin cuenta</span></c:otherwise>
                                        </c:choose>
                                    </td>
                                    <td>
                                        <div class="table-actions" style="justify-content:flex-end;">
                                            <button class="c-btn c-btn--ghost btn-editar"
                                                    data-id="${p.idPaciente}" data-nombre="${p.nombre}"
                                                    data-apellido="${p.apellido}" data-dni="${p.dni}"
                                                    data-fecha="${p.fechaNacimiento}" data-tel="${p.telefono}">
                                                <i class="ti ti-pencil"></i>
                                            </button>
                                            <a class="c-btn c-btn--danger" href="PacienteServlet?accion=eliminar&id=${p.idPaciente}"
                                               onclick="return confirm('¿Desactivar a este paciente?');">
                                                <i class="ti ti-trash"></i>
                                            </a>
                                        </div>
                                    </td>
                                </tr>
                            </c:forEach>
                            <c:if test="${empty pacientes}">
                                <tr><td colspan="6" class="muted" style="text-align:center; padding:28px;">Aún no hay pacientes registrados.</td></tr>
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
            <form action="PacienteServlet" method="POST">
                <input type="hidden" name="accion" value="crear">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-user-heart"></i> Registrar paciente</div>
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
                    <div class="field"><label>Fecha de nacimiento</label><input class="c-input" type="date" name="fechaNacimiento" required></div>
                    <div class="field">
                        <label>Vincular cuenta de usuario (rol Paciente)</label>
                        <select class="c-select" name="idUsuario">
                            <option value="">— Sin cuenta por ahora —</option>
                            <c:forEach var="u" items="${usuarios}">
                                <c:if test="${u.idRol == 4}">
                                    <option value="${u.idUsuario}">${u.username} (${u.email})</option>
                                </c:if>
                            </c:forEach>
                        </select>
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
            <form action="PacienteServlet" method="POST">
                <input type="hidden" name="accion" value="editar">
                <input type="hidden" name="idPaciente" id="ed_id">
                <div class="c-modal__head">
                    <div class="c-modal__title"><i class="ti ti-pencil"></i> Editar paciente</div>
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
                    <div class="field"><label>Fecha de nacimiento</label><input class="c-input" type="date" name="fechaNacimiento" id="ed_fecha" required></div>
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
                document.getElementById('ed_fecha').value = b.dataset.fecha;
                document.getElementById('ed_telefono').value = (b.dataset.tel === 'null') ? '' : b.dataset.tel;
                document.getElementById('modalEditar').classList.add('is-open');
            });
        });
    </script>
</body>
</html>