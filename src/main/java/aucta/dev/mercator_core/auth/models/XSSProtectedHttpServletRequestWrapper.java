package aucta.dev.mercator_core.auth.models;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;

public class XSSProtectedHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private final Map<String, String[]> sanitizedParameters;

    public XSSProtectedHttpServletRequestWrapper(HttpServletRequest request, Map<String, String[]> sanitizedParameters) throws IOException {
        super(request);
        this.sanitizedParameters = sanitizedParameters;
    }

    @Override
    public String getParameter(String name) {
        String[] values = sanitizedParameters.get(name);
        return values != null && values.length > 0 ? values[0] : null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return sanitizedParameters;
    }

    @Override
    public String[] getParameterValues(String name) {
        return sanitizedParameters.get(name);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return super.getParameterNames();
    }
}