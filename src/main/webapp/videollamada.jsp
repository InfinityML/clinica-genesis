<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="com.clinica.modelo.Usuario" %>
<%
    Usuario __usr = (Usuario) session.getAttribute("usuarioLogueado");
    if (__usr == null) { response.sendRedirect("index.jsp"); return; }
    if (request.getAttribute("sala") == null) { response.sendRedirect("DashboardServlet"); return; }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Teleconsulta · Clínica UPN</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.44.0/dist/tabler-icons.min.css">
    <link rel="stylesheet" href="assets/css/app.css">
    <style>
        html, body { height: 100%; margin: 0; }
        .vc-shell { height: 100vh; display: flex; flex-direction: column; background: #06382C; }
        .vc-bar {
            display: flex; align-items: center; gap: 14px;
            padding: 12px 20px; color: #fff;
            background: linear-gradient(90deg, #06382C, #0F6E56);
            flex-shrink: 0;
        }
        .vc-bar__brand { display: flex; align-items: center; gap: 10px; font-weight: 700; }
        .vc-bar__brand i { font-size: 22px; }
        .vc-bar__info { font-size: 13px; opacity: .9; }
        .vc-bar__spacer { flex: 1; }
        .vc-salir {
            display: inline-flex; align-items: center; gap: 6px;
            background: rgba(255,255,255,.15); color: #fff; text-decoration: none;
            padding: 8px 14px; border-radius: 10px; font-weight: 600; font-size: 14px;
        }
        .vc-salir:hover { background: rgba(255,255,255,.28); }
        .vc-body { flex: 1; min-height: 0; display: flex; }
        #jitsi { flex: 1; min-height: 0; min-width: 0; }
        .vc-form {
            width: 400px; flex-shrink: 0; background: #f4f6f8; overflow-y: auto;
            padding: 18px; border-left: 3px solid #0F6E56;
        }
        .vc-form h3 { margin: 0 0 4px; color: var(--brand-900); font-size: 16px; }
        .vc-form .muted { font-size: 12.5px; }
        .vc-vitals {
            display: grid; grid-template-columns: 1fr 1fr; gap: 6px;
            background: #fff; border: 1px solid #e2e8ee; border-radius: 10px;
            padding: 10px; margin: 12px 0; font-size: 12.5px;
        }
        .vc-vitals span b { color: var(--brand-700); }
        @media (max-width: 900px) {
            .vc-body { flex-direction: column; }
            .vc-form { width: auto; border-left: none; border-top: 3px solid #0F6E56; }
        }
    </style>
</head>
<body>
    <div class="vc-shell">
        <div class="vc-bar">
            <div class="vc-bar__brand"><i class="ti ti-video"></i> Clínica UPN</div>
            <div class="vc-bar__info">
                Teleconsulta · <strong>${cita.paciente}</strong> con <strong>Dr. ${cita.medico}</strong>
                &nbsp;·&nbsp; ${cita.fecha} ${cita.hora}
            </div>
            <div class="vc-bar__spacer"></div>
            <a class="vc-salir" href="${volverUrl}"><i class="ti ti-logout"></i> Salir</a>
        </div>

        <div class="vc-body">
            <div id="jitsi"
                 data-sala="${sala}"
                 data-name="${displayName}"
                 data-volver="${volverUrl}"></div>

            <c:if test="${esMedico}">
                <aside class="vc-form">
                    <h3><i class="ti ti-clipboard-heart"></i> Registro de la consulta</h3>
                    <p class="muted">Documenta mientras atiendes. Al guardar, la cita queda atendida.</p>

                    <div class="vc-vitals">
                        <span><b>Peso:</b> ${empty cita.peso ? '—' : cita.peso} kg</span>
                        <span><b>Talla:</b> ${empty cita.talla ? '—' : cita.talla} cm</span>
                        <span><b>Temp:</b> ${empty cita.temp ? '—' : cita.temp} °C</span>
                        <span><b>P. arterial:</b> ${empty cita.presion ? '—' : cita.presion}</span>
                        <span><b>F. cardíaca:</b> ${empty cita.fc ? '—' : cita.fc} lpm</span>
                        <span><b>Obs.:</b> ${empty cita.obs_triaje ? '—' : cita.obs_triaje}</span>
                    </div>

                    <form action="AtencionServlet" method="POST">
                        <input type="hidden" name="idCita" value="${cita.id_cita}">
                        <input type="hidden" name="idPaciente" value="${cita.id_paciente}">
                        <input type="hidden" name="idHistoria" value="${cita.id_historia}">
                        <input type="hidden" name="idMedico" value="${cita.id_medico}">

                        <div class="field">
                            <label>Motivo de consulta</label>
                            <textarea class="c-textarea" name="motivo" rows="2" required></textarea>
                        </div>
                        <div class="field">
                            <label>Diagnóstico</label>
                            <textarea class="c-textarea" name="diagnostico" rows="2" required></textarea>
                        </div>

                        <div class="text-strong" style="color:var(--brand-700); margin:6px 0;"><i class="ti ti-pill"></i> Receta (opcional)</div>
                        <div class="field">
                            <label>Medicamento</label>
                            <select class="c-select" name="idMedicamento">
                                <option value="">— Sin receta —</option>
                                <c:forEach var="m" items="${medicamentos}">
                                    <option value="${m.id_medicamento}">${m.medicamento}</option>
                                </c:forEach>
                            </select>
                        </div>
                        <div class="form-row">
                            <div class="field"><label>Dosis</label><input class="c-input" type="text" name="dosis" placeholder="1 tableta"></div>
                            <div class="field"><label>Frecuencia</label><input class="c-input" type="text" name="frecuencia" placeholder="Cada 8 h"></div>
                        </div>
                        <div class="field">
                            <label>Duración</label>
                            <input class="c-input" type="text" name="duracion" placeholder="5 días">
                        </div>

                        <button type="submit" class="c-btn c-btn--primary c-btn--block" style="margin-top:8px;"
                                onclick="return confirm('¿Finalizar la atención y guardar? La cita quedará como atendida.');">
                            <i class="ti ti-device-floppy"></i> Finalizar y guardar
                        </button>
                    </form>
                </aside>
            </c:if>
        </div>
    </div>

    <script src="https://meet.jit.si/external_api.js"></script>
    <script>
        (function () {
            var box = document.getElementById('jitsi');
            if (typeof JitsiMeetExternalAPI === 'undefined') {
                box.innerHTML = '<div style="color:#fff;text-align:center;padding:40px;">No se pudo cargar la videollamada. Revisa tu conexión e inténtalo de nuevo.</div>';
                return;
            }
            var api = new JitsiMeetExternalAPI('meet.jit.si', {
                roomName: box.dataset.sala,
                parentNode: box,
                userInfo: { displayName: box.dataset.name },
                width: '100%',
                height: '100%',
                configOverwrite: {
                    prejoinPageEnabled: false,
                    prejoinConfig: { enabled: false },
                    disableDeepLinking: true,
                    startWithAudioMuted: false,
                    startWithVideoMuted: false
                },
                interfaceConfigOverwrite: {
                    SHOW_JITSI_WATERMARK: false,
                    DEFAULT_BACKGROUND: '#06382C'
                }
            });
            api.addEventListener('readyToClose', function () {
                window.location.href = box.dataset.volver;
            });
        })();
    </script>
</body>
</html>