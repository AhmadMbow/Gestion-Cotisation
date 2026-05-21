<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Connexion | Gestion Cotisations</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Inter:wght@300;400;500;600;700&display=swap">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/plugins/fontawesome-free/css/all.min.css">

    <style>
        :root {
            --grad-start: #667eea;
            --grad-end: #764ba2;
            --text-dark: #1f2937;
            --text-muted: #6b7280;
            --border: #e5e7eb;
            --bg: #f9fafb;
            --accent: #667eea;
            --danger: #ef4444;
            --success: #10b981;
        }

        * { box-sizing: border-box; }

        body {
            margin: 0;
            min-height: 100vh;
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif;
            background: var(--bg);
            color: var(--text-dark);
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 24px;
        }

        .auth-card {
            display: flex;
            width: 100%;
            max-width: 960px;
            min-height: 600px;
            background: #fff;
            border-radius: 24px;
            overflow: hidden;
            box-shadow: 0 25px 50px -12px rgba(0, 0, 0, 0.15),
                        0 0 0 1px rgba(0, 0, 0, 0.05);
            animation: fadeUp .6s ease-out;
        }

        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(20px); }
            to   { opacity: 1; transform: translateY(0); }
        }

        /* ---- Panneau de gauche : branding ---- */
        .auth-brand {
            flex: 1;
            position: relative;
            padding: 48px 40px;
            background: linear-gradient(135deg, var(--grad-start) 0%, var(--grad-end) 100%);
            color: #fff;
            display: flex;
            flex-direction: column;
            justify-content: space-between;
            overflow: hidden;
        }

        /* Cercles décoratifs animés en arrière-plan */
        .auth-brand::before,
        .auth-brand::after {
            content: '';
            position: absolute;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.08);
        }
        .auth-brand::before {
            width: 280px;
            height: 280px;
            top: -80px;
            right: -60px;
            animation: float 8s ease-in-out infinite;
        }
        .auth-brand::after {
            width: 200px;
            height: 200px;
            bottom: -60px;
            left: -40px;
            animation: float 10s ease-in-out infinite reverse;
        }

        @keyframes float {
            0%, 100% { transform: translateY(0) translateX(0); }
            50%      { transform: translateY(-20px) translateX(10px); }
        }

        .brand-logo {
            position: relative;
            z-index: 1;
            display: inline-flex;
            align-items: center;
            gap: 12px;
            font-size: 1.25rem;
            font-weight: 700;
            letter-spacing: -0.01em;
        }
        .brand-logo i {
            width: 44px;
            height: 44px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            background: rgba(255, 255, 255, 0.2);
            backdrop-filter: blur(10px);
            border-radius: 12px;
            font-size: 1.25rem;
        }

        .brand-hero {
            position: relative;
            z-index: 1;
            text-align: center;
            padding: 16px 0;
        }
        .brand-hero .illustration {
            font-size: 7rem;
            opacity: 0.95;
            margin-bottom: 16px;
            filter: drop-shadow(0 8px 16px rgba(0, 0, 0, 0.15));
        }
        .brand-hero h2 {
            font-size: 1.875rem;
            font-weight: 600;
            margin: 0 0 12px;
            letter-spacing: -0.02em;
        }
        .brand-hero p {
            font-size: 0.95rem;
            opacity: 0.85;
            line-height: 1.6;
            margin: 0;
            max-width: 320px;
            margin: 0 auto;
        }

        .brand-footer {
            position: relative;
            z-index: 1;
            font-size: 0.8rem;
            opacity: 0.7;
            display: flex;
            gap: 20px;
        }
        .brand-footer span { display: inline-flex; align-items: center; gap: 6px; }

        /* ---- Panneau de droite : formulaire ---- */
        .auth-form {
            flex: 1;
            padding: 56px 48px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }
        .auth-form h1 {
            font-size: 1.75rem;
            font-weight: 600;
            margin: 0 0 8px;
            letter-spacing: -0.02em;
        }
        .auth-form .lead {
            color: var(--text-muted);
            font-size: 0.95rem;
            margin: 0 0 32px;
        }

        .form-field { margin-bottom: 18px; }
        .form-field label {
            display: block;
            font-size: 0.85rem;
            font-weight: 500;
            color: var(--text-dark);
            margin-bottom: 6px;
        }
        .form-field .input-wrap {
            position: relative;
        }
        .form-field .input-wrap i {
            position: absolute;
            left: 14px;
            top: 50%;
            transform: translateY(-50%);
            color: var(--text-muted);
            pointer-events: none;
            transition: color 0.2s;
        }
        .form-field input {
            width: 100%;
            padding: 12px 14px 12px 42px;
            font-size: 0.95rem;
            font-family: inherit;
            color: var(--text-dark);
            background: var(--bg);
            border: 1.5px solid var(--border);
            border-radius: 10px;
            transition: all 0.2s;
        }
        .form-field input:focus {
            outline: none;
            background: #fff;
            border-color: var(--accent);
            box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.15);
        }
        .form-field input:focus + i,
        .form-field .input-wrap:focus-within i {
            color: var(--accent);
        }

        .form-options {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin: 8px 0 24px;
            font-size: 0.875rem;
        }
        .form-options label {
            display: inline-flex;
            align-items: center;
            gap: 8px;
            color: var(--text-muted);
            cursor: pointer;
        }
        .form-options input[type="checkbox"] {
            width: 16px;
            height: 16px;
            accent-color: var(--accent);
            cursor: pointer;
        }
        .form-options a {
            color: var(--accent);
            text-decoration: none;
            font-weight: 500;
        }
        .form-options a:hover { text-decoration: underline; }

        .btn-submit {
            width: 100%;
            padding: 13px;
            font-size: 0.95rem;
            font-weight: 600;
            color: #fff;
            background: linear-gradient(135deg, var(--grad-start), var(--grad-end));
            border: none;
            border-radius: 10px;
            cursor: pointer;
            transition: transform 0.15s, box-shadow 0.2s;
        }
        .btn-submit:hover {
            transform: translateY(-1px);
            box-shadow: 0 10px 20px -8px rgba(102, 126, 234, 0.5);
        }
        .btn-submit:active { transform: translateY(0); }

        .alert {
            padding: 12px 16px;
            border-radius: 10px;
            font-size: 0.875rem;
            margin-bottom: 18px;
            display: flex;
            align-items: flex-start;
            gap: 10px;
        }
        .alert i { margin-top: 2px; }
        .alert-danger {
            background: #fef2f2;
            color: #991b1b;
            border: 1px solid #fecaca;
        }
        .alert-success {
            background: #f0fdf4;
            color: #166534;
            border: 1px solid #bbf7d0;
        }

        .demo-hint {
            margin-top: 24px;
            padding: 14px 16px;
            background: var(--bg);
            border-radius: 10px;
            font-size: 0.8rem;
            color: var(--text-muted);
            line-height: 1.6;
        }
        .demo-hint strong { color: var(--text-dark); }
        .demo-hint code {
            background: #fff;
            padding: 2px 6px;
            border-radius: 4px;
            font-size: 0.8rem;
            color: var(--text-dark);
        }

        /* ---- Responsive ---- */
        @media (max-width: 800px) {
            .auth-card {
                flex-direction: column;
                min-height: 0;
            }
            .auth-brand {
                padding: 32px 24px;
                min-height: 200px;
            }
            .brand-hero { padding: 8px 0; }
            .brand-hero .illustration { font-size: 4rem; margin-bottom: 8px; }
            .brand-hero h2 { font-size: 1.5rem; }
            .brand-hero p { font-size: 0.875rem; }
            .brand-footer { display: none; }
            .auth-form { padding: 32px 24px; }
        }
    </style>
</head>
<body>

<div class="auth-card">

    <!-- Panneau gauche : branding -->
    <div class="auth-brand">
        <div class="brand-logo">
            <i class="fas fa-hand-holding-usd"></i>
            <span>Gestion Cotisations</span>
        </div>

        <div class="brand-hero">
            <i class="fas fa-users-cog illustration"></i>
            <h2>Bienvenue 👋</h2>
            <p>
                Gérez en un clin d'œil les membres, cotisations
                et amendes de votre association.
            </p>
        </div>

        <div class="brand-footer">
            <span><i class="fas fa-shield-alt"></i> Sécurisé</span>
            <span><i class="fas fa-bolt"></i> Rapide</span>
            <span><i class="fas fa-mobile-alt"></i> Responsive</span>
        </div>
    </div>

    <!-- Panneau droite : formulaire -->
    <div class="auth-form">
        <h1>Connexion</h1>
        <p class="lead">Entrez vos identifiants pour accéder à votre espace.</p>

        <jsp:include page="/WEB-INF/views/layout/flash-messages.jsp"/>

        <c:if test="${not empty error}">
            <div class="alert alert-danger">
                <i class="fas fa-exclamation-circle"></i>
                <span>${error}</span>
            </div>
        </c:if>
        <c:if test="${not empty success}">
            <div class="alert alert-success">
                <i class="fas fa-check-circle"></i>
                <span>${success}</span>
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post" autocomplete="on">
            <input type="hidden" name="_csrf" value="${csrfToken}">

            <div class="form-field">
                <label for="email">Adresse email</label>
                <div class="input-wrap">
                    <input type="email" id="email" name="email" required autofocus
                           placeholder="vous@exemple.com" value="${param.email}">
                    <i class="fas fa-envelope"></i>
                </div>
            </div>

            <div class="form-field">
                <label for="password">Mot de passe</label>
                <div class="input-wrap">
                    <input type="password" id="password" name="password" required
                           placeholder="••••••••">
                    <i class="fas fa-lock"></i>
                </div>
            </div>

            <div class="form-options">
                <label>
                    <input type="checkbox" name="remember">
                    <span>Se souvenir de moi</span>
                </label>
                <a href="${pageContext.request.contextPath}/forgot-password">Mot de passe oublié ?</a>
            </div>

            <button type="submit" class="btn-submit">
                <i class="fas fa-sign-in-alt mr-1"></i> Se connecter
            </button>
        </form>

        <div class="demo-hint">
            <strong>Comptes de démo</strong> — mot de passe&nbsp;<code>admin123</code><br>
            <code>admin@asso.sn</code> &middot; <code>aminata@asso.sn</code> &middot; <code>moussa@asso.sn</code>
        </div>
    </div>
</div>

</body>
</html>
