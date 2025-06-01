/*
 * Copyright (c) 2021, 2022 Contributors to the Eclipse Foundation
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
 * Contributors:
 *   2021 : Payara Foundation and/or its affiliates
 */
package ee.omnifish.security.example;

import java.io.IOException;

import jakarta.annotation.security.DeclareRoles;
import jakarta.inject.Inject;
import jakarta.security.enterprise.SecurityContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.HttpConstraint;
import jakarta.servlet.annotation.ServletSecurity;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/Secured")
@DeclareRoles("all")
@ServletSecurity(
        @HttpConstraint(rolesAllowed = "all"))
public class SecuredServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Inject
    SecurityContext securityContext;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.getWriter().println("""
<h1>This is a secured web page</h1>
<p>
    User: USER
</p>
<p>
    <a href="CONTEXT_PATH/Logout">Log out</a>
</p>
"""
                .replace("USER", securityContext.getCallerPrincipal().getName())
                .replace("CONTEXT_PATH", request.getContextPath())
        );
    }
}
