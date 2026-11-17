package campustask;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Dao.DaoException;
import Dao.UserDao;

/** プロフィール更新（action=save）とログアウト（action=logout） */
@WebServlet("/profile")
public class ProfileServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = AppUtil.value(request, "action", "save");

        // ログアウト：セッションを破棄してログイン画面へ
        if ("logout".equals(action)) {
            HttpSession session = request.getSession(false);
            if (session != null) session.invalidate();
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        String userId = AppUtil.currentUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        String name = AppUtil.value(request, "name", "");
        String email = AppUtil.value(request, "email", "");
        String message;
        if (name.isEmpty() || name.length() > 50) {
            message = "氏名は1〜50文字で入力してください。";
        } else if (email.length() > 255 || !email.matches("[^@\\s]+@[^@\\s]+")) {
            message = "メールアドレスの形式が正しくありません。";
        } else {
            try {
                message = userDao.updateProfile(userId, name, email)
                        ? "プロフィールを更新しました。"
                        : "このメールアドレスは使用できません。";
            } catch (DaoException e) {
                getServletContext().log("プロフィール更新でDBエラー", e);
                message = "データベースエラーが発生しました。";
            }
        }
        request.getSession().setAttribute("message", message);
        response.sendRedirect(request.getContextPath() + "/home?page=profile");
    }
}
