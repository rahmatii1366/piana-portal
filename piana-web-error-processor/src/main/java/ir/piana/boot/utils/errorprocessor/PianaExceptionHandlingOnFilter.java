package ir.piana.boot.utils.errorprocessor;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class PianaExceptionHandlingOnFilter extends OncePerRequestFilter {
    private final ObjectMapper objectMapper;

    public PianaExceptionHandlingOnFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            filterChain.doFilter(request, response);
        } catch (ApiException apiException) {
            if (!response.isCommitted()) {
                synchronized (response) {
                    response.setStatus(apiException.getStatus().value());
                    byte[] errorBody = objectMapper.writeValueAsBytes(apiException.apiError);
                    response.getOutputStream().write(errorBody);
                    response.addHeader("content-type", "application/json; charset=utf-8");
                }
            }
        }
    }
}
