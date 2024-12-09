package filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import exeptions.BadRequestException;
import exeptions.NotFoundException;
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

    private final ObjectMapper mapper = new ObjectMapper();

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
            mapper.writeValue(httpResponse.getWriter(), new Message(e.getMessage()));
        } catch (NotFoundException e) {
            httpResponse.setStatus(HttpServletResponse.SC_NOT_FOUND);
            mapper.writeValue(httpResponse.getWriter(), new Message(e.getMessage()));
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            mapper.writeValue(httpResponse.getWriter(), new Message("Ошибка (например, база данных недоступна)"));
        }
    }

    record Message(String message) {
    }
}
