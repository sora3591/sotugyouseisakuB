package campustask;

import java.time.LocalDate;

public class Assignment {
    private int id;
    private String title;
    private String course;
    private LocalDate due;
    private String description;
    private int estimate;
    private String status; // "todo", "doing", "done"
    private String priority; // "高", "普通", "低"

    public Assignment(int id, String title, String course, LocalDate due, String description, int estimate, String status, String priority) {
        this.id = id;
        this.title = title;
        this.course = course;
        this.due = due;
        this.description = description;
        this.estimate = estimate;
        this.status = status;
        this.priority = priority;
    }

    // ゲッター・セッター
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public LocalDate getDue() { return due; }
    public void setDue(LocalDate due) { this.due = due; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getEstimate() { return estimate; }
    public void setEstimate(int estimate) { this.estimate = estimate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
}