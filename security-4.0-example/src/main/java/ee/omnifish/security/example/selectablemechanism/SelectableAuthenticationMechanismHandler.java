/*
 * Copyright (c) 2025 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */
package ee.omnifish.security.example.selectablemechanism;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.SessionScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;
import jakarta.security.enterprise.AuthenticationException;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpAuthenticationMechanismHandler;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.authentication.mechanism.http.CustomFormAuthenticationMechanismDefinition.CustomFormAuthenticationMechanism;
import jakarta.security.enterprise.authentication.mechanism.http.HttpMessageContext;
import jakarta.security.enterprise.authentication.mechanism.http.OpenIdAuthenticationMechanismDefinition.OpenIdAuthenticationMechanism;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serializable;

import static jakarta.interceptor.Interceptor.Priority.APPLICATION;
import static jakarta.security.enterprise.AuthenticationStatus.SEND_CONTINUE;
import static jakarta.security.enterprise.AuthenticationStatus.SUCCESS;

@Alternative
@Priority(APPLICATION)
@SessionScoped
public class SelectableAuthenticationMechanismHandler implements HttpAuthenticationMechanismHandler, Serializable {

    @Inject
    @CustomFormAuthenticationMechanism
    HttpAuthenticationMechanism formAuthMechanism;

    @Inject
    @OpenIdAuthenticationMechanism
    HttpAuthenticationMechanism oidcAuthMechanism;

    String previousAuth = "";

    @Override
    public AuthenticationStatus validateRequest(HttpServletRequest request, HttpServletResponse response, HttpMessageContext httpMessageContext) throws AuthenticationException {
        String authType = request.getParameter("auth");
        if (authType != null) {
            previousAuth = authType;
        } else {
            if (CHOOSE_LOGIN_PAGE_PATH.equals(request.getServletPath())) {
                authType = "choose";
            } else {
                authType = previousAuth;
            }
        }

        return switch (authType) {
            case "oidc" ->
                oidcAuthMechanism.validateRequest(request, response, httpMessageContext);
            case "form" ->
                formAuthMechanism.validateRequest(request, response, httpMessageContext);
            case "choose" ->
                SUCCESS;
            default -> {
                if (httpMessageContext.isProtected()) {
                    redirect(response, request, CHOOSE_LOGIN_PAGE_PATH);
                    yield SEND_CONTINUE;
                } else {
                    yield SUCCESS;
                }
            }
        };
    }
    ;
    private static final String CHOOSE_LOGIN_PAGE_PATH = "/choose-login.xhtml";

    private void redirect(HttpServletResponse response, HttpServletRequest request, String page) throws AuthenticationException {
        try {
            response.sendRedirect(request.getContextPath() + page);
        } catch (IOException ex) {
            throw new AuthenticationException(ex);
        }
    }

}
