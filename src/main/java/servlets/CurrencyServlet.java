package servlets;

import dto.ResponseDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import services.CurrencyService;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private static final CurrencyService service = CurrencyService.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        ResponseDto responseDto = service.findByCode(req.getPathInfo());
        resp.setStatus(responseDto.status());
        resp.getWriter().write(responseDto.message());
    }
}
