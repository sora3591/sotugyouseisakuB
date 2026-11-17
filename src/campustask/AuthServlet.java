package campustask;

import java.io.IOException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Dao.DaoException;
import Dao.NotificationDao;
import Dao.UserDao;
import bean.User;

/** ログイン（mode=login）とアカウント作成（mode=signup） */
@WebServlet("/auth")
public class AuthServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Pattern USER_ID_PATTERN = Pattern.compile("[A-Za-z0-9_-]{3,20}");

    private final UserDao userDao = new UserDao();
    private final NotificationDao notificationDao = new NotificationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (AppUtil.currentUserId(request) != null) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }
        forwardToLogin(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String mode = AppUtil.value(request, "mode", "login");
        String userId = AppUtil.value(request, "studentId", "");
        String name = AppUtil.value(request, "name", "");
        String email = AppUtil.value(request, "email", "");
        // パスワードは前後の空白も意味を持つので trim しない
        String password = request.getParameter("password");
        if (password == null) password = "";

        // 入力エラー時に画面へ戻す値（パスワードは戻さない）
        request.setAttribute("mode", mode);
        request.setAttribute("studentId", userId);
        request.setAttribute("name", name);
        request.setAttribute("email", email);

        try {
            User user;
            if ("signup".equals(mode)) {
                String error = validateSignup(userId, name, email, password);
                if (error != null) {
                    fail(request, response, error);
                    return;
                }
                user = userDao.register(userId, name, email, password);
                if (user == null) {
                    fail(request, response, "その学生IDまたはメールアドレスはすでに登録されています。");
                    return;
                }
                notificationDao.add(userId, null,
                        "Campus Task へようこそ！まずは課題を登録してみましょう。",
                        NotificationDao.TYPE_SYSTEM);
            } else {
                if (userId.isEmpty() || password.isEmpty()) {
                    fail(request, response, "学生IDとパスワードを入力してください。");
                    return;
                }
                user = userDao.authenticate(userId, password);
                if (user == null) {
                    fail(request, response, "学生IDまたはパスワードが正しくありません。");
                    return;
                }
            }
            logIn(request, user);
            response.sendRedirect(request.getContextPath() + "/home");
        } catch (DaoException e) {
            getServletContext().log("認証処理でDBエラー", e);
            System.out.println(e);
            fail(request, response, "システムエラーが発生しました。しばらくしてからやり直してください。");
        }
    }

    private String validateSignup(String userId, String name, String email, String password) {
        if (!USER_ID_PATTERN.matcher(userId).matches()) {
            return "学生IDは半角英数字・「_」「-」の3〜20文字で入力してください。";
        }
        if (name.isEmpty() || name.length() > 50) {
            return "氏名は1〜50文字で入力してください。";
        }
        if (email.length() > 255 || !email.matches("[^@\\s]+@[^@\\s]+")) {
            return "メールアドレスの形式が正しくありません。";
        }
        if (password.length() < 8) {
            return "パスワードは8文字以上で入力してください。";
        }
        return null;
    }

    /** セッション固定攻撃対策のため、ログイン成功時は古いセッションを破棄して作り直す */
    private void logIn(HttpServletRequest request, User user) {
        HttpSession old = request.getSession(false);
        if (old != null) old.invalidate();
        request.getSession(true).setAttribute(AppUtil.SESSION_USER_ID, user.getUserId());
    }

    private void fail(HttpServletRequest request, HttpServletResponse response, String error)
            throws ServletException, IOException {
        request.setAttribute("error", error);
        forwardToLogin(request, response);
    }

    private void forwardToLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/login.jsp").forward(request, response);
    }
}
