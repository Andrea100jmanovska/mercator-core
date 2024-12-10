package aucta.dev.mercator_core.auth;

import aucta.dev.mercator_core.auth.models.XSSProtectedHttpServletRequestWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component
public class XSSProtectionFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Map<String, String[]> sanitizedParameters = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();


        while (parameterNames.hasMoreElements()) {
            String parameterName = parameterNames.nextElement();
            String[] parameterValues = request.getParameterValues(parameterName);
            String[] sanitizedValues = new String[parameterValues.length];

            for (int i = 0; i < parameterValues.length; i++) {
                String parameterInitialValue = parameterValues[i];
                String parameterChangedValue = sanitizeInput(parameterValues[i]);

                if(!parameterInitialValue.equals(parameterChangedValue)) {
                    response.setStatus(HttpStatus.BAD_REQUEST.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write("{\"error\": \"Invalid input detected in request parameters.\"}");
                    return;
                }

                sanitizedValues[i] = parameterChangedValue;
            }

            sanitizedParameters.put(parameterName, sanitizedValues);
        }
        XSSProtectedHttpServletRequestWrapper sanitizedRequest = new XSSProtectedHttpServletRequestWrapper(request, sanitizedParameters);

        filterChain.doFilter(sanitizedRequest, response);
    }
    private String sanitizeInput(String input) {
        if (input == null) {
            return null;
        }

        input = input.replaceAll("(?i)<script.*?>.*?</script>", "")
                .replaceAll("(?i)<.*?>", "")
                .replaceAll("(?i)on\\w+=", "")
                .replaceAll("(?i)javascript:", "")
                .replaceAll("(?i)vbscript:", "")
                .replaceAll("(?i)expression\\(", "")
                .replaceAll("(?i)eval\\((.*)\\)", "");

        return input;
    }
}
