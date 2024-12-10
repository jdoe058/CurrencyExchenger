package filters;

import exceptions.BadRequestException;
import exceptions.ErrorException;
import exceptions.NotFoundException;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@WebFilter({
        "/currencies",
        "/currency/*",
        "/exchangeRates",
        "/exchangeRate/*"
})
public class RestApiFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {

        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;

        // Добавляем CORS заголовки
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // Обработка preflight запроса
        if ("OPTIONS".equalsIgnoreCase(((HttpServletRequest) servletRequest).getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        servletResponse.setContentType("application/json");
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8.name());

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (BadRequestException e) {
            httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            httpResponse.getWriter().write(e.jsonMessage());
        } catch (NotFoundException e) {
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            httpResponse.getWriter().write(e.jsonMessage());
        } catch (ErrorException e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.getWriter().write(e.jsonMessage());
        }
    }
}
