<#macro registrationLayout bodyClass="" displayInfo=false displayMessage=true displayRequiredFields=false>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>StreamFlow - Вход</title>
    
    <!-- Подключаем Google Fonts -->
    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">
    
    <!-- Подключаем наши стили через properties -->
    <#if properties.styles?has_content>
        <#list properties.styles?split(' ') as style>
            <link href="${url.resourcesPath}/${style}" rel="stylesheet" />
        </#list>
    </#if>
    
    <!-- Полные инлайн стили для гарантированного применения -->
    <style>
        /* Сброс стилей */
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        /* Основные стили */
        html, body {
            background: #0f0f0f !important;
            color: #ffffff !important;
            font-family: 'Inter', -apple-system, BlinkMacSystemFont, sans-serif !important;
            height: 100vh !important;
            margin: 0 !important;
            padding: 0 !important;
        }
        
        /* Контейнер - принудительное центрирование */
        #kc-container {
            min-height: 100vh !important;
            display: flex !important;
            align-items: center !important;
            justify-content: center !important;
            padding: 1rem !important;
            background: #0f0f0f !important;
            width: 100vw !important;
            position: relative !important;
        }
        
        #kc-container-wrapper {
            width: 100% !important;
            max-width: 440px !important;
            margin: 0 auto !important;
            display: flex !important;
            flex-direction: column !important;
            align-items: center !important;
            justify-content: center !important;
        }
        
        /* Форма */
        #kc-form-wrapper,
        #kc-registration {
            background: #1a1a1a !important;
            border: 1px solid #333333 !important;
            border-radius: 1rem !important;
            padding: 2.5rem !important;
            box-shadow: 0 20px 25px -5px rgba(0, 0, 0, 0.8), 0 10px 10px -5px rgba(0, 0, 0, 0.4) !important;
            width: 100% !important;
            margin: 0 auto !important;
        }
        
        /* Дополнительные отступы для регистрации */
        #kc-registration {
            padding-top: 1.5rem !important;
        }
        
        /* Содержимое формы выравниваем по левому краю */
        #kc-form-wrapper form,
        #kc-registration form {
            text-align: left !important;
        }
        
        #kc-form-wrapper .form-group,
        #kc-registration .form-group {
            text-align: left !important;
            width: 100% !important;
        }
        
        /* ПРИНУДИТЕЛЬНОЕ ЛЕВОЕ ВЫРАВНИВАНИЕ ДЛЯ ПОЛЕЙ */
        .form-group,
        .form-group *,
        .form-control,
        input[type="text"],
        input[type="password"],
        input[type="email"],
        label {
            text-align: left !important;
            margin-left: 0 !important;
            margin-right: 0 !important;
            display: block !important;
            width: 100% !important;
        }
        
        /* Убираем любые стили центрирования для полей */
        .form-group input,
        .form-group label {
            text-align: left !important;
            justify-content: flex-start !important;
            align-items: flex-start !important;
        }
        
        /* Заголовки */
        h1 {
            font-size: 1.5rem !important;
            font-weight: 700 !important;
            color: #ffffff !important;
            margin-bottom: 0.5rem !important;
            margin-top: 0 !important;
            text-align: center !important;
        }
        
        .subtitle {
            color: #9ca3af !important;
            font-size: 0.875rem !important;
            text-align: center !important;
            margin-bottom: 2rem !important;
            margin-top: 0 !important;
        }
        
        /* Специальные стили для заголовков в формах */
        #kc-page-title h1 {
            margin-top: 0 !important;
        }
        
        #kc-page-title .subtitle {
            margin-top: 0.5rem !important;
        }
        
        #kc-page-title {
            text-align: center !important;
            margin-bottom: 2rem !important;
            margin-top: 1rem !important;
        }
        
        /* Поля ввода */
        .form-control,
        input[type="text"],
        input[type="password"],
        input[type="email"] {
            width: 100% !important;
            padding: 0.875rem 1rem !important;
            font-size: 0.875rem !important;
            color: #ffffff !important;
            background: #2a2a2a !important;
            border: 1px solid #404040 !important;
            border-radius: 0.75rem !important;
            outline: none !important;
            transition: all 0.2s ease !important;
        }
        
        .form-control:focus,
        input:focus {
            border-color: #3b82f6 !important;
            box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.15) !important;
            background: #2f2f2f !important;
        }
        
        .form-control::placeholder,
        input::placeholder {
            color: #9ca3af !important;
        }
        
        /* Лейблы */
        label {
            display: block !important;
            font-size: 0.875rem !important;
            font-weight: 500 !important;
            color: #e5e7eb !important;
            margin-bottom: 0.5rem !important;
            text-align: left !important;
            width: 100% !important;
        }
        
        /* Отступ для кнопки */
        #kc-form-buttons {
            margin-top: 1rem !important;
            margin-bottom: 1rem !important;
        }
        
        /* Дополнительный отступ между label и input */
        .form-group label {
            margin-bottom: 0.75rem !important;
        }
        
        /* Кнопки */
        .btn-primary,
        input[type="submit"] {
            width: 100% !important;
            padding: 0.875rem 1.5rem !important;
            font-size: 0.875rem !important;
            font-weight: 600 !important;
            color: white !important;
            background: #3b82f6 !important;
            border: none !important;
            border-radius: 0.75rem !important;
            cursor: pointer !important;
            transition: all 0.2s ease !important;
            box-shadow: 0 4px 6px -1px rgba(59, 130, 246, 0.3) !important;
            text-align: center !important;
        }
        
        .btn-primary:hover,
        input[type="submit"]:hover {
            background: #2563eb !important;
            box-shadow: 0 6px 8px -1px rgba(59, 130, 246, 0.4) !important;
            transform: translateY(-1px) !important;
        }
        
        /* Кнопка "Назад" в верхнем левом углу */
        #back-button-container {
            position: fixed !important;
            top: 1rem !important;
            left: 1rem !important;
            z-index: 1000 !important;
        }
        
        .btn-back {
            padding: 0.5rem 1rem !important;
            font-size: 0.875rem !important;
            font-weight: 500 !important;
            color: #9ca3af !important;
            background: rgba(42, 42, 42, 0.9) !important;
            border: 1px solid #404040 !important;
            border-radius: 0.5rem !important;
            cursor: pointer !important;
            transition: all 0.2s ease !important;
            backdrop-filter: blur(10px) !important;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
            text-align: center !important;
            display: block !important;
            margin: 0 auto !important;
            width: auto !important;
            min-width: 120px !important;
        }
        
        .btn-back:hover {
            background: rgba(51, 51, 51, 0.95) !important;
            border-color: #4a4a4a !important;
            color: #e5e7eb !important;
            transform: translateY(-1px) !important;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.4) !important;
        }
        
        /* Чекбоксы */
        input[type="checkbox"] {
            width: 0.875rem !important;
            height: 0.875rem !important;
            color: #3b82f6 !important;
            background: #0a0a0a !important;
            border: 1px solid #2a2a2a !important;
            border-radius: 0.25rem !important;
            cursor: pointer !important;
        }
        
        .checkbox {
            display: flex !important;
            align-items: center !important;
            gap: 0.5rem !important;
        }
        
        .checkbox label {
            margin: 0 !important;
            cursor: pointer !important;
        }
        
        /* Социальные провайдеры */
        .kc-social-section::before {
            content: '' !important;
            display: block !important;
            height: 1px !important;
            background: #404040 !important;
            margin: 1.5rem 0 !important;
        }
        
        .kc-social-links {
            display: flex !important;
            flex-direction: column !important;
            gap: 0.75rem !important;
        }
        
        .kc-social-link {
            display: flex !important;
            align-items: center !important;
            justify-content: center !important;
            width: 100% !important;
            padding: 0.875rem 1.5rem !important;
            font-size: 0.875rem !important;
            font-weight: 500 !important;
            color: #ffffff !important;
            background: #2a2a2a !important;
            border: 1px solid #404040 !important;
            border-radius: 0.75rem !important;
            cursor: pointer !important;
            transition: all 0.2s ease !important;
            text-decoration: none !important;
            text-align: center !important;
        }
        
        .kc-social-link:hover {
            background: #333333 !important;
            border-color: #4a4a4a !important;
            text-decoration: none !important;
            transform: translateY(-1px) !important;
        }
        
        .kc-social-provider-logo {
            width: 1.25rem !important;
            height: 1.25rem !important;
            margin-right: 0.5rem !important;
        }
        
        /* Футер - центрируем только фразы навигации */
        #kc-registration {
            margin-top: 2rem !important;
            margin-bottom: 2rem !important;
            padding: 1.5rem 0 !important;
            border-top: 1px solid #404040 !important;
            text-align: center !important;
            font-size: 0.875rem !important;
            color: #9ca3af !important;
            display: flex !important;
            align-items: center !important;
            justify-content: center !important;
            min-height: 3rem !important;
        }
        
        /* Специально для фраз навигации */
        #kc-registration span {
            text-align: center !important;
            display: block !important;
            width: 100% !important;
        }
        
        /* Убеждаемся, что поля формы остаются слева */
        .form-group,
        .form-group input,
        .form-group label,
        #kc-form-wrapper .form-group,
        #kc-form-wrapper input,
        #kc-form-wrapper label {
            text-align: left !important;
            justify-content: flex-start !important;
            align-items: flex-start !important;
        }
        
        /* ПРОСТОЕ И ЭФФЕКТИВНОЕ ЦЕНТРИРОВАНИЕ КНОПОК */
        input[type="submit"],
        .btn-primary {
            text-align: center !important;
            display: block !important;
            margin: 0 auto !important;
        }
        
        /* Кнопка "Назад" */
        .btn-back {
            text-align: center !important;
            display: block !important;
            margin: 0 auto !important;
            width: auto !important;
            min-width: 120px !important;
        }
        
        /* Социальные кнопки */
        .kc-social-link {
            text-align: center !important;
            display: block !important;
            margin: 0 auto !important;
        }
        
        #kc-info {
            text-align: center !important;
            width: 100% !important;
        }
        
        /* JavaScript принудительное центрирование */
        .force-center {
            text-align: center !important;
            display: flex !important;
            align-items: center !important;
            justify-content: center !important;
        }
        
        /* Центрирование только контейнеров, не содержимого */
        #kc-page-title,
        .alert,
        #kc-form-wrapper,
        #kc-info,
        #kc-registration {
            margin-left: auto !important;
            margin-right: auto !important;
        }
        
        #kc-registration a {
            color: #3b82f6 !important;
            font-weight: 600 !important;
            text-decoration: none !important;
        }
        
        #kc-registration a:hover {
            color: #2563eb !important;
            text-decoration: underline !important;
        }
        
        /* Сообщения об ошибках */
        .alert {
            padding: 0.75rem !important;
            border-radius: 0.5rem !important;
            margin-bottom: 1rem !important;
            font-size: 0.875rem !important;
        }
        
        .alert-error {
            background: rgba(239, 68, 68, 0.1) !important;
            border: 1px solid rgba(239, 68, 68, 0.2) !important;
            color: #ef4444 !important;
        }
        
        /* Скрываем стандартные элементы Keycloak */
        #kc-header,
        #kc-header-wrapper,
        .fa,
        .pficon {
            display: none !important;
        }
        
        /* Адаптивность */
        @media (max-width: 640px) {
            #kc-container {
                padding: 0.5rem !important;
            }
            
            #kc-form-wrapper,
            #kc-registration {
                padding: 1.5rem !important;
            }
        }
        
        /* Центрирование только основных контейнеров */
        #kc-container {
            display: flex !important;
            justify-content: center !important;
            align-items: center !important;
            margin: 0 auto !important;
            padding: 0 !important;
        }
        
        #kc-container-wrapper {
            display: block !important;
            margin: 0 auto !important;
        }
        
        /* ФИНАЛЬНОЕ ИСПРАВЛЕНИЕ - ПРИНУДИТЕЛЬНОЕ ЛЕВОЕ ВЫРАВНИВАНИЕ */
        input, label, .form-control, .form-group {
            text-align: left !important;
            justify-content: flex-start !important;
            align-items: flex-start !important;
            display: block !important;
            margin: 0 !important;
            width: 100% !important;
        }
        
        /* Убираем все flex-центрирование для полей */
        .form-group {
            display: block !important;
            text-align: left !important;
        }
        
        /* Переопределяем любые стили центрирования */
        #kc-form-wrapper *,
        #kc-registration *,
        .form-group *,
        .form-control,
        input,
        label {
            text-align: left !important;
            margin-left: 0 !important;
            margin-right: auto !important;
            justify-content: flex-start !important;
            align-items: flex-start !important;
        }
    </style>
</head>

<body>
    <!-- Кнопка "Назад" в верхнем левом углу -->
    <div id="back-button-container">
        <button type="button" class="btn-back" onclick="window.location.href='http://localhost:5173'">
            <span style="display: flex; align-items: center; justify-content: center; width: 100%; text-align: center;">← Назад на сайт</span>
        </button>
    </div>
    
    <div id="kc-container">
        <div id="kc-container-wrapper">
            
            <!-- Заголовок страницы -->
            <div id="kc-page-title"><#nested "header"></div>

            <!-- Сообщения об ошибках -->
            <#if displayMessage && message?has_content>
                <div class="alert alert-${message.type}">
                    <span class="kc-feedback-text">${kcSanitize(message.summary)?no_esc}</span>
                </div>
            </#if>

            <!-- Основная форма -->
            <#nested "form">

            <!-- Дополнительная информация -->
            <#if displayInfo>
                <div id="kc-info">
                    <div id="kc-info-wrapper">
                        <#nested "info">
                    </div>
                </div>
            </#if>

        </div>
    </div>
    
    <!-- JavaScript для принудительного центрирования текста на кнопках -->
    <script>
        document.addEventListener('DOMContentLoaded', function() {
            // Центрируем все кнопки
            const buttons = document.querySelectorAll('input[type="submit"], .btn-primary, .btn-back, .kc-social-link');
            
            buttons.forEach(function(button) {
                // Добавляем класс для центрирования
                button.classList.add('force-center');
                
                // Принудительно устанавливаем стили
                button.style.textAlign = 'center';
                button.style.display = 'flex';
                button.style.alignItems = 'center';
                button.style.justifyContent = 'center';
                
                // Для input кнопок добавляем дополнительную обработку
                if (button.tagName === 'INPUT') {
                    button.style.paddingLeft = '0';
                    button.style.paddingRight = '0';
                }
            });
            
            // Дополнительно центрируем текст через CSS переменные
            const style = document.createElement('style');
            style.textContent = `
                input[type="submit"], .btn-primary, .btn-back, .kc-social-link {
                    text-align: center !important;
                    display: flex !important;
                    align-items: center !important;
                    justify-content: center !important;
                }
            `;
            document.head.appendChild(style);
        });
    </script>
</body>
</html>
</#macro>