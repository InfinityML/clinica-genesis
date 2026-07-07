<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.clinica.util.RecaptchaService" %>
<% boolean __captchaOn = RecaptchaService.estaConfigurado();
   String __siteKey = RecaptchaService.siteKey(); %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Clínica Universitaria UPN · Iniciar sesión</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/@tabler/icons-webfont@3.44.0/dist/tabler-icons.min.css">
    <link rel="stylesheet" href="assets/css/app.css">
</head>
<body>
    <div class="auth">
        <aside class="auth__aside">
            <div class="auth__brand">
                <span class="mark"><i class="ti ti-heartbeat"></i></span>
                Clínica Génesis
            </div>
            <h1 class="auth__headline">Cuidado clínico,<br>gestión sin fricción.</h1>
            <p class="auth__sub">Agenda, triaje, historias clínicas, cobros y facturación electrónica en un solo sistema.</p>
            <div class="auth__pts">
                <span class="auth__pt"><i class="ti ti-shield-lock"></i> Acceso por roles</span>
                <span class="auth__pt"><i class="ti ti-calendar-heart"></i> Citas y teleconsulta</span>
                <span class="auth__pt"><i class="ti ti-file-invoice"></i> Comprobantes SUNAT</span>
            </div>
        </aside>

        <main class="auth__main">
            <div class="auth__card">
                <h2 class="auth__title">Bienvenido de nuevo</h2>
                <p class="auth__lead">Ingresa tus credenciales para acceder al sistema.</p>

                <% if (request.getAttribute("errorAuth") != null) { %>
                    <div class="c-alert c-alert--danger" style="margin-bottom:20px;">
                        <i class="ti ti-alert-circle"></i>
                        <span><%= request.getAttribute("errorAuth") %></span>
                    </div>
                <% } %>

                <form action="LoginServlet" method="POST">
                    <div class="field">
                        <label for="username">Usuario</label>
                        <div class="input-group">
                            <i class="ti ti-user"></i>
                            <input class="c-input" type="text" id="username" name="username"
                                   placeholder="tu.usuario" required autofocus>
                        </div>
                    </div>

                    <div class="field">
                        <label for="password">Contraseña</label>
                        <div class="input-group">
                            <i class="ti ti-lock"></i>
                            <input class="c-input" type="password" id="password" name="password"
                                   placeholder="Tu contraseña" required>
                        </div>
                    </div>

                    <% if (__captchaOn) { %>
                    <div class="g-recaptcha" data-sitekey="<%= __siteKey %>" style="margin:6px 0 16px; display:flex; justify-content:center;"></div>
                    <% } %>

                    <button type="submit" class="c-btn c-btn--primary c-btn--block" style="margin-top:8px;">
                        <i class="ti ti-login-2"></i> Iniciar sesión
                    </button>
                </form>

                <p class="muted" style="text-align:center; font-size:12.5px; margin-top:28px;">
                    Universidad Privada del Norte · Soluciones Web y Aplicaciones Distribuidas
                </p>
            </div>
        </main>
    </div>

    <% if (__captchaOn) { %>
    <script src="https://www.google.com/recaptcha/api.js" async defer></script>
    <% } %>
</body>
</html>
