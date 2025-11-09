package com.LaundryApplication.LaundryApplication.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;


import java.io.IOException;
import java.util.Enumeration;

@Component
@WebFilter("/*")
public class RequestResponseLoggingFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        System.out.println("✅ RequestResponseLoggingFilter initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        // Wrap request and response for body access
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(req);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(res);
        chain.doFilter(wrappedRequest, wrappedResponse);
        String body = new String(wrappedRequest.getContentAsByteArray(), request.getCharacterEncoding());

        System.out.println("📩 [INCOMING REQUEST]");
        System.out.println("➡️ Method: " + req.getMethod());
        System.out.println("➡️ URI: " + req.getRequestURI());
        System.out.println("⬅️ Body: " + (body.isEmpty() ? "[empty]" : body));

        Enumeration<String> headerNames = req.getHeaderNames();
        System.out.println("➡️ Headers:");
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            System.out.println("   " + header + ": " + req.getHeader(header));
        }


        // Log response status & body
        String responseBody = new String(wrappedResponse.getContentAsByteArray(), response.getCharacterEncoding());
        System.out.println("📤 [OUTGOING RESPONSE]");
        System.out.println("⬅️ Status: " + res.getStatus());
        System.out.println("⬅️ Body: " + (responseBody.isEmpty() ? "[empty]" : responseBody));
        System.out.println("=========================================");

        // Important: copy back the cached body to the output stream
        wrappedResponse.copyBodyToResponse();
    }


    @Override
    public void destroy() {
        System.out.println("🧹 RequestResponseLoggingFilter destroyed");
    }
}
