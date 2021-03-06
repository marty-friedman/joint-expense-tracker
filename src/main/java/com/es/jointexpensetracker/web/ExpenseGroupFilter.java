package com.es.jointexpensetracker.web;

import com.es.jointexpensetracker.service.expenses.ExpenseService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpenseGroupFilter implements Filter {
    private Pattern urlPattern;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        urlPattern = Pattern.compile("/expense-groups/(\\p{XDigit}{8}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{4}-\\p{XDigit}{12})(.*)");
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        Matcher matcher = urlPattern.matcher(request.getServletPath());
        if (!matcher.matches()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        ExpenseService expenseService = ExpenseService.getInstance(UUID.fromString(matcher.group(1)));
        if (expenseService == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        request.setAttribute("expenseService", expenseService);

        String path = matcher.group(2);
        String contextPath = request.getContextPath() + "/expense-groups/" + matcher.group(1);
        if (path.equals("")){
            response.sendRedirect(contextPath + "/expenses");
            return;
        }
        RequestDispatcher requestDispatcher = request.getRequestDispatcher(matcher.group(2));
        if (requestDispatcher == null)
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        else
            requestDispatcher.forward(new RewriteContextPathRequestWrapper(request, contextPath), response);
    }

    @Override
    public void destroy() {}

    private static class RewriteContextPathRequestWrapper extends HttpServletRequestWrapper {
        private String contextPath;

        private RewriteContextPathRequestWrapper(HttpServletRequest request, String contextPath) {
            super(request);
            this.contextPath = contextPath;
        }

        @Override
        public String getContextPath() {
            return contextPath;
        }

        @Override
        public String getRequestURI() {
            String servletPath = getServletPath();
            String pathInfo = getPathInfo();
            return contextPath + servletPath + (pathInfo == null ? "" : pathInfo);
        }
    }
}
