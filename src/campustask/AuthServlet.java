package campustask;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String mode = AppUtil.value(request, "mode", "login");
        String name = AppUtil.value(request, "name", "山田 太郎");
        String email = AppUtil.value(request, "email", "student@example.com");
        String studentId = AppUtil.value(request, "studentId", "");
        String password = AppUtil.value(request, "password", "");

        if ("signup".equals(mode)) {
            if (name.isEmpty() || email.isEmpty() || password.length() < 8) {
                request.setAttribute("error", "氏名・メールアドレス・8文字以上のパスワードを入力してください。");
                request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
                return;
            }
        } else if (studentId.isEmpty() || password.isEmpty()) {
            request.setAttribute("error", "学生IDとパスワードを入力してください。");
            request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
            return;
        }

        // プロトタイプのため、ここでは実際のID・パスワード認証は行いません。
        AppData data = AppUtil.data(request);
        if ("signup".equals(mode)) {
            data.setUserName(name);
            data.setUserEmail(email);
        }
        HttpSession session = request.getSession();
        session.setAttribute("loggedIn", Boolean.TRUE);
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
