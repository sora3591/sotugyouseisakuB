package campustask;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!Boolean.TRUE.equals(request.getSession().getAttribute("loggedIn"))) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        AppData data = AppUtil.data(request);
        request.setAttribute("data", data);
        String page = AppUtil.value(request, "page", "dashboard");
        request.setAttribute("page", page);
        request.setAttribute("filter", AppUtil.value(request, "filter", "すべて"));
        request.setAttribute("q", AppUtil.value(request, "q", ""));
        request.getRequestDispatcher("/WEB-INF/home.jsp").forward(request, response);
    }
}
