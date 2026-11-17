package campustask;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import Dao.DaoException;
import Dao.NotificationDao;
import Dao.TaskDao;
import Dao.UserDao;
import bean.Task;
import bean.User;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final UserDao userDao = new UserDao();
    private final TaskDao taskDao = new TaskDao();
    private final NotificationDao notificationDao = new NotificationDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String userId = AppUtil.currentUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }
        try {
            User user = userDao.findById(userId);
            if (user == null) { // DB側でユーザーが消えている場合
                HttpSession session = request.getSession(false);
                if (session != null) session.invalidate();
                response.sendRedirect(request.getContextPath() + "/auth");
                return;
            }
            List<Task> tasks = taskDao.findAll(userId);
            List<String> notifications = notificationDao.findMessages(userId);
            request.setAttribute("data", new AppData(user, tasks, notifications));
        } catch (DaoException e) {
            throw new ServletException("データの読み込みに失敗しました", e);
        }
        request.setAttribute("page", AppUtil.value(request, "page", "dashboard"));
        request.setAttribute("filter", AppUtil.value(request, "filter", "すべて"));
        request.setAttribute("q", AppUtil.value(request, "q", ""));
        request.getRequestDispatcher("/WEB-INF/home.jsp").forward(request, response);
    }
}
