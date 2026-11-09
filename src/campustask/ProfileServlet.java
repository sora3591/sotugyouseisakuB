package campustask;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        if (!Boolean.TRUE.equals(request.getSession().getAttribute("loggedIn"))) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        AppData data = AppUtil.data(request);
        String action = AppUtil.value(request, "action", "save");
        if ("logout".equals(action)) {
            request.getSession().invalidate();
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        String name = AppUtil.value(request, "name", data.getUserName());
        String email = AppUtil.value(request, "email", data.getUserEmail());
        if (!name.isEmpty()) data.setUserName(name);
        if (!email.isEmpty()) data.setUserEmail(email);
        request.getSession().setAttribute("message", "プロフィールを更新しました。");
        response.sendRedirect(request.getContextPath() + "/home?page=profile");
    }
}
