<#import "template.ftl" as layout>
<@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section = "header">
        <div class="text-center">
            <h1 class="text-2xl font-bold text-white mb-2">Добро пожаловать в StreamFlow</h1>
            <p class="text-gray-400 text-sm">Войдите в свой аккаунт</p>
        </div>
    <#elseif section = "form">
    <div id="kc-form">
        <div id="kc-form-wrapper">
            <#if realm.password>
                <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                    <#if !usernameHidden??>
                        <div class="form-group">
                            <label for="username">
                                <#if !realm.loginWithEmailAllowed>Логин<#elseif !realm.registrationEmailAsUsername>Логин или email<#else>Email</#if>
                            </label>
                            <input tabindex="1" id="username" class="form-control" name="username" value="${(login.username!'')}" type="text" autofocus autocomplete="username"
                                   placeholder="Введите логин или email"
                                   aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                            />
                        </div>
                    </#if>

                    <div class="form-group">
                        <label for="password">Пароль</label>
                        <input tabindex="2" id="password" class="form-control" name="password" type="password" autocomplete="current-password"
                               placeholder="Введите пароль"
                               aria-invalid="<#if messagesPerField.existsError('username','password')>true</#if>"
                        />
                    </div>

                    <div class="form-group">
                        <div id="kc-form-options">
                            <#if realm.rememberMe && !usernameHidden??>
                                <div class="checkbox">
                                    <input tabindex="3" id="rememberMe" name="rememberMe" type="checkbox" <#if login.rememberMe??>checked</#if>>
                                    <label for="rememberMe">Запомнить меня</label>
                                </div>
                            </#if>
                        </div>
                    </div>

                    <div id="kc-form-buttons" class="form-group">
                        <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"</#if>/>
                        <button type="submit" class="btn btn-primary" name="login" id="kc-login" tabindex="4">
                            <span style="display: flex; align-items: center; justify-content: center; width: 100%; text-align: center;">Войти</span>
                        </button>
                    </div>
                </form>
            </#if>
            </div>

            <#if realm.password && social.providers??>
                <div class="kc-social-section">
                    <#if social.providers?size gt 0>
                        <div class="kc-social-links">
                            <#list social.providers as p>
                                <a id="social-${p.alias}" class="kc-social-link ${p.providerId}" href="${p.loginUrl}" style="display: flex; align-items: center; justify-content: center;">
                                    <svg class="kc-social-provider-logo" viewBox="0 0 24 24" style="margin-right: 8px;">
                                        <path fill="#4285F4" d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z"/>
                                        <path fill="#34A853" d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z"/>
                                        <path fill="#FBBC05" d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z"/>
                                        <path fill="#EA4335" d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z"/>
                                    </svg>
                                    <span class="kc-social-provider-name" style="text-align: center;">Войти через Google</span>
                                </a>
                            </#list>
                        </div>
                    </#if>
                </div>
            </#if>
        </div>
    <#elseif section = "info" >
        <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
            <div id="kc-registration">
                <span>Нет аккаунта? <a tabindex="6" href="${url.registrationUrl}">Зарегистрироваться</a></span>
            </div>
        </#if>
    <#elseif section = "socialProviders" >
        <#-- Social providers rendered inline above -->
    </#if>

</@layout.registrationLayout>
