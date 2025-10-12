<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('firstName','lastName','email','username','password','password-confirm') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "header">
        <div class="text-center">
            <h1 class="text-2xl font-bold text-white mb-2">Регистрация в StreamFlow</h1>
            <p class="text-gray-400 text-sm">Создайте новый аккаунт</p>
        </div>
    <#elseif section = "form">
    <div id="kc-form">
        <div id="kc-form-wrapper">
            <form id="kc-register-form" action="${url.registrationAction}" method="post">
                <div class="form-group">
                    <label for="firstName">Имя</label>
                    <input type="text" id="firstName" class="form-control" name="firstName"
                           value="${(register.formData.firstName!'')}"
                           placeholder="Введите ваше имя"
                           aria-invalid="<#if messagesPerField.existsError('firstName')>true</#if>"
                    />
                </div>

                <div class="form-group">
                    <label for="lastName">Фамилия</label>
                    <input type="text" id="lastName" class="form-control" name="lastName"
                           value="${(register.formData.lastName!'')}"
                           placeholder="Введите вашу фамилию"
                           aria-invalid="<#if messagesPerField.existsError('lastName')>true</#if>"
                    />
                </div>

                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="text" id="email" class="form-control" name="email"
                           value="${(register.formData.email!'')}" autocomplete="email"
                           placeholder="Введите ваш email"
                           aria-invalid="<#if messagesPerField.existsError('email')>true</#if>"
                    />
                </div>

                <#if !realm.registrationEmailAsUsername>
                    <div class="form-group">
                        <label for="username">Логин</label>
                        <input type="text" id="username" class="form-control" name="username"
                               value="${(register.formData.username!'')}" autocomplete="username"
                               placeholder="Введите логин"
                               aria-invalid="<#if messagesPerField.existsError('username')>true</#if>"
                        />
                    </div>
                </#if>

                <div class="form-group">
                    <label for="password">Пароль</label>
                    <input type="password" id="password" class="form-control" name="password"
                           autocomplete="new-password"
                           placeholder="Введите пароль"
                           aria-invalid="<#if messagesPerField.existsError('password','password-confirm')>true</#if>"
                    />
                </div>

                <div class="form-group">
                    <label for="password-confirm">Подтвердите пароль</label>
                    <input type="password" id="password-confirm" class="form-control"
                           name="password-confirm" autocomplete="new-password"
                           placeholder="Подтвердите пароль"
                           aria-invalid="<#if messagesPerField.existsError('password-confirm')>true</#if>"
                    />
                </div>

                <#if recaptchaRequired??>
                    <div class="form-group">
                        <div class="g-recaptcha" data-size="compact" data-sitekey="${recaptchaSiteKey}"></div>
                    </div>
                </#if>

                <div id="kc-form-buttons" class="form-group">
                    <button class="btn btn-primary" type="submit">
                        <span style="display: flex; align-items: center; justify-content: center; width: 100%; text-align: center;">Зарегистрироваться</span>
                    </button>
                </div>
            </form>
        </div>
    </div>
    <#elseif section = "info" >
        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div id="kc-registration">
                <span>Уже есть аккаунт? <a href="${url.loginUrl}">Войти</a></span>
            </div>
        </#if>
    </#if>
</@layout.registrationLayout>
