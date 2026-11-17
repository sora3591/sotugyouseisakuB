package campustask;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
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
import bean.Task;

@WebServlet("/task")
public class TaskServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final List<String> PRIORITIES = Arrays.asList("高", "普通", "低");

    private final TaskDao taskDao = new TaskDao();
    private final NotificationDao notificationDao = new NotificationDao();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String userId = AppUtil.currentUserId(request);
        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/auth");
            return;
        }

        HttpSession session = request.getSession();
        String action = AppUtil.value(request, "action", "");
        try {
            if ("save".equals(action)) {
                save(request, session, userId);
            } else if ("delete".equals(action)) {
                taskDao.delete(userId, Integer.parseInt(request.getParameter("id")));
            } else if ("toggle".equals(action)) {
                taskDao.toggleDone(userId, Integer.parseInt(request.getParameter("id")));
            } else if ("ai".equals(action)) {
                Task next = taskDao.findNextDue(userId);
                String title = next == null ? "次の課題" : next.getTitle();
                notificationDao.add(userId, next == null ? null : next.getId(),
                        "AI提案：期限が近い課題を優先し、今日は「" + title + "」に取り組むのがおすすめです。",
                        NotificationDao.TYPE_AI);
            }
        } catch (NumberFormatException | DateTimeParseException e) {
            session.setAttribute("message", "入力内容を確認してください。");
        } catch (DaoException e) {
            getServletContext().log("課題操作でDBエラー", e);
            session.setAttribute("message", "データベースエラーが発生しました。");
        }
        response.sendRedirect(request.getContextPath() + "/home?page=" +
                ("ai".equals(action) ? "reminders" : "tasks"));
    }

    private void save(HttpServletRequest request, HttpSession session, String userId) {
        String title = AppUtil.value(request, "title", "");
        String course = AppUtil.value(request, "course", "");
        if (title.isEmpty() || course.isEmpty()) {
            session.setAttribute("message", "課題名と授業名を入力してください。");
            return;
        }
        if (title.length() > 100 || course.length() > 100) {
            session.setAttribute("message", "課題名・授業名は100文字以内で入力してください。");
            return;
        }
        LocalDate due = LocalDate.parse(AppUtil.value(request, "due", LocalDate.now().plusDays(3).toString()));
        int estimate = Integer.parseInt(AppUtil.value(request, "estimate", "60"));
        String description = AppUtil.value(request, "description", "");
        String priority = AppUtil.value(request, "priority", "普通");
        if (estimate < 0 || !PRIORITIES.contains(priority)) {
            session.setAttribute("message", "入力内容を確認してください。");
            return;
        }

        String idText = AppUtil.value(request, "id", "");
        if (idText.isEmpty()) {
            taskDao.insert(userId, new Task(0, title, course, due, description, estimate, "todo", priority));
        } else {
            // 自分の課題でなければ update は 0 件更新で何も起きない（状態は変更されない）
            taskDao.update(userId, new Task(Integer.parseInt(idText), title, course, due,
                    description, estimate, "todo", priority));
        }
    }
}
