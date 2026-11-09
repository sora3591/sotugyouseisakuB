package campustask;

import java.io.IOException;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/task")
public class TaskServlet extends HttpServlet {
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
        String action = AppUtil.value(request, "action", "");
        try {
            if ("save".equals(action)) {
                String title = AppUtil.value(request, "title", "");
                String course = AppUtil.value(request, "course", "");
                if (title.isEmpty() || course.isEmpty()) {
                    request.getSession().setAttribute("message", "課題名と授業名を入力してください。");
                    response.sendRedirect(request.getContextPath() + "/home?page=tasks");
                    return;
                }
                LocalDate due = LocalDate.parse(AppUtil.value(request, "due", LocalDate.now().plusDays(3).toString()));
                int estimate = Integer.parseInt(AppUtil.value(request, "estimate", "60"));
                String description = AppUtil.value(request, "description", "");
                String priority = AppUtil.value(request, "priority", "普通");
                String idText = AppUtil.value(request, "id", "");

                if (idText.isEmpty()) {
                    data.getTasks().add(new Task(data.nextId(), title, course, due, description,
                            estimate, "todo", priority));
                } else {
                    Task task = data.findTask(Integer.parseInt(idText));
                    if (task != null) {
                        task.setTitle(title);
                        task.setCourse(course);
                        task.setDue(due);
                        task.setDescription(description);
                        task.setEstimate(estimate);
                        task.setPriority(priority);
                    }
                }
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                data.getTasks().removeIf(t -> t.getId() == id);
            } else if ("toggle".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                Task task = data.findTask(id);
                if (task != null) task.setStatus("done".equals(task.getStatus()) ? "todo" : "done");
            } else if ("ai".equals(action)) {
                Task next = null;
                for (Task task : data.getTasks()) {
                    if (!"done".equals(task.getStatus()) &&
                            (next == null || task.getDue().isBefore(next.getDue()))) next = task;
                }
                String title = next == null ? "次の課題" : next.getTitle();
                data.getNotifications().add(0, "AI提案：期限が近い課題を優先し、今日は「" + title + "」に取り組むのがおすすめです。");
            }
        } catch (Exception e) {
            request.getSession().setAttribute("message", "入力内容を確認してください。");
        }
        response.sendRedirect(request.getContextPath() + "/home?page=" +
                ("ai".equals(action) ? "reminders" : "tasks"));
    }
}
