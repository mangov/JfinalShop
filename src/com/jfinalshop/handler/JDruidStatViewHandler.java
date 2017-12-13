/**
 * Copyright (c) 2011-2015, James Zhan 詹波 (jfinal@126.com).
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jfinalshop.handler;

import com.alibaba.druid.support.http.ResourceServlet;
import com.alibaba.druid.support.http.StatViewServlet;
import com.jfinal.handler.Handler;
import com.jfinal.kit.HandlerKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.druid.IDruidStatViewAuth;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 替代 StatViewServlet
 */
public class JDruidStatViewHandler extends Handler {

    private IDruidStatViewAuth auth;
    private String visitPath = "/druid";
    private StatViewServlet servlet = new JFinalStatViewServlet();

    public JDruidStatViewHandler(String visitPath) {
        this(visitPath,
                new IDruidStatViewAuth() {
                    public boolean isPermitted(HttpServletRequest request) {
                        if (request.getSession().getAttribute(ResourceServlet.SESSION_USER_KEY) != null) {
                            return true;
                        } else {
                            String requestURI = request.getRequestURI();
                            String path = requestURI;
                            if (path.endsWith("login.html") || path.endsWith(".js") || path.endsWith(".css")) {
                                return true;
                            } else if (path.contains("/submitLogin")) {
                                String usernameParam = request.getParameter(ResourceServlet.PARAM_NAME_USERNAME);
                                String passwordParam = request.getParameter(ResourceServlet.PARAM_NAME_PASSWORD);
                                PropKit.use("appconfig.properties");
                                String username = PropKit.get("druid.user", "admin");//缺省配置用户名密码均为admin
                                String password = PropKit.get("druid.password", "admin");
                                if (username.equals(usernameParam) && password.equals(passwordParam)) {
                                    request.getSession().setAttribute(ResourceServlet.SESSION_USER_KEY, usernameParam);
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                });
    }

    public JDruidStatViewHandler(String visitPath, IDruidStatViewAuth druidStatViewAuth) {
        if (StrKit.isBlank(visitPath))
            throw new IllegalArgumentException("visitPath can not be blank");
        if (druidStatViewAuth == null)
            throw new IllegalArgumentException("druidStatViewAuth can not be null");

        visitPath = visitPath.trim();
        if (!visitPath.startsWith("/"))
            visitPath = "/" + visitPath;
        this.visitPath = visitPath;
        this.auth = druidStatViewAuth;
    }

    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (target.startsWith(visitPath)) {
            isHandled[0] = true;

            if (target.equals(visitPath) && !target.endsWith("/index.html")) {
                HandlerKit.redirect(target += "/index.html", request, response, isHandled);
                return;
            }

            try {
                servlet.service(request, response);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            next.handle(target, request, response, isHandled);
        }
    }

    class JFinalStatViewServlet extends StatViewServlet {

        private static final long serialVersionUID = 2898674199964021798L;

        public boolean isPermittedRequest(HttpServletRequest request) {
            return auth.isPermitted(request);
        }

        public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
            String contextPath = request.getContextPath();

            String requestURI = request.getRequestURI();

            response.setCharacterEncoding("utf-8");

            if (contextPath == null) { // root context
                contextPath = "";
            }
            int index = contextPath.length() + visitPath.length();
            String uri = requestURI.substring(0, index);
            String path = requestURI.substring(index);

            if (!isPermittedRequest(request)) {
                path = "/nopermit.html";
                returnResourceFile(path, uri, response);
                return;
            }

            if (isRequireAuth() //
                    && !ContainsUser(request)//
                    && !("/login.html".equals(path) //
                    || path.startsWith("/css")//
                    || path.startsWith("/js") //
                    || path.startsWith("/img"))) {
                if (contextPath == null || contextPath.equals("") || contextPath.equals("/")) {
                    response.sendRedirect("/druid/login.html");
                } else {
                    if ("".equals(path)) {
                        response.sendRedirect("druid/login.html");
                    } else {
                        response.sendRedirect("login.html");
                    }
                }
                return;
            }

            if ("".equals(path)) {
                if (contextPath == null || contextPath.equals("") || contextPath.equals("/")) {
                    response.sendRedirect("/druid/index.html");
                } else {
                    response.sendRedirect("druid/index.html");
                }
                return;
            }

            if ("/".equals(path)) {
                response.sendRedirect("index.html");
                return;
            }

            if (path.indexOf(".json") >= 0) {
                String fullUrl = path;
                if (request.getQueryString() != null && request.getQueryString().length() > 0) {
                    fullUrl += "?" + request.getQueryString();
                }
                response.getWriter().print(process(fullUrl));
                return;
            }

            if ("/submitLogin".equals(path)) {
                response.getWriter().print("success");
                return;
            }

            // find file in resources path
            returnResourceFile(path, uri, response);
        }
    }
}




