<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Campus Task - 課題管理</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.5/font/bootstrap-icons.css">
<style>
  body { background-color: #F6F7FB; font-family: 'Arial', sans-serif; }
  .sidebar { width: 248px; background: white; min-height: 100vh; position: fixed; border-right: 1px solid #dee2e6; }
  .main-content { margin-left: 248px; padding: 30px; }
  .card-stat { border: none; border-radius: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.03); }
</style>
</head>
<body>

<div class="sidebar p-4 d-flex flex-column">
    <div class="d-flex align-items-center mb-4">
        <div class="bg-primary text-white p-2 rounded-3 me-2"><i class="bi bi-school fs-4"></i></div>
        <div>
            <h5 class="fw-bold mb-0">Campus Task</h5>
            <small class="text-muted">課題管理アプリ</small>
        </div>
    </div>
    <span class="text-muted small fw-bold mb-2">MENU</span>
    <a href="tasks" class="text-decoration-none text-primary fw-bold p-2 rounded bg-light mb-1"><i class="bi bi-dashboard me-2"></i> ホーム・課題一覧</a>
    <hr>
    <div class="mt-auto p-3 bg-light rounded-4 d-flex align-items-center">
        <div class="bg-primary-subtle text-primary rounded-circle p-2 me-2"><i class="bi bi-person"></i></div>
        <div>
            <div class="fw-bold small">${sessionScope.userName}</div>
            <small class="text-muted" style="font-size: 10px;">学生アカウント</small>
        </div>
    </div>
</div>

<div class="main-content">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <div>
            <h2>おかえりなさい！ 👋</h2>
            <p class="text-muted">課題の進捗を確認して、今日やることを整理しましょう。</p>
        </div>
        <button class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addModal"><i class="bi bi-plus"></i> 課題を追加</button>
    </div>

    <!-- 統計カード -->
    <div class="row g-3 mb-4">
        <div class="col-md-4">
            <div class="card card-stat p-3 bg-white">
                <small class="text-muted">登録課題</small>
                <h3 class="fw-bold text-primary">${tasks.size()} 件</h3>
            </div>
        </div>
    </div>

    <!-- 課題リスト -->
    <h4 class="fw-bold mb-3">課題一覧</h4>
    <div class="list-group">
        <c:forEach var="t" items="${tasks}">
            <div class="list-group-item p-3 mb-2 rounded-3 border-0 shadow-sm d-flex justify-content-between align-items-center">
                <div>
                    <form action="tasks" method="post" class="d-inline">
                        <input type="hidden" name="action" value="toggle">
                        <input type="hidden" name="id" value="${t.id}">
                        <button type="submit" class="btn btn-sm ${t.status == 'done' ? 'btn-success' : 'btn-outline-secondary'} me-2">
                            <i class="bi ${t.status == 'done' ? 'bi-check-lg' : 'bi-dash'}"></i>
                        </button>
                    </form>
                    <span class="${t.status == 'done' ? 'text-decoration-line-through text-muted' : 'fw-bold'}">${t.title}</span>
                    <span class="badge bg-secondary ms-2">${t.course}</span>
                    <span class="badge bg-info text-dark ms-1">期限: ${t.due}</span>
                </div>
                <a href="tasks?action=delete&id=${t.id}" class="btn btn-outline-danger btn-sm"><i class="bi bi-trash"></i></a>
            </div>
        </c:forEach>
    </div>
</div>

<!-- 課題追加モーダル -->
<div class="modal fade" id="addModal" tabindex="-1">
  <div class="modal-dialog">
    <form action="tasks" method="post" class="modal-content">
      <input type="hidden" name="action" value="add">
      <div class="modal-header">
        <h5 class="modal-title">新しい課題を追加</h5>
        <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
      </div>
      <div class="modal-content p-3 border-0">
          <div class="mb-3">
              <label class="form-label">課題名 *</label>
              <input type="text" name="title" class="form-control" required>
          </div>
          <div class="mb-3">
              <label class="form-label">授業名 *</label>
              <input type="text" name="course" class="form-control" required>
          </div>
          <div class="mb-3">
              <label class="form-label">詳細</label>
              <textarea name="description" class="form-control" rows="2"></textarea>
          </div>
          <div class="mb-3">
              <label class="form-label">予想作業時間（分）</label>
              <input type="number" name="estimate" class="form-control" value="60">
          </div>
          <div class="mb-3">
              <label class="form-label">期限</label>
              <input type="date" name="due" class="form-control" required>
          </div>
          <div class="mb-3">
              <label class="form-label">優先度</label>
              <select name="priority" class="form-select">
                  <option value="高">高</option>
                  <option value="普通" selected>普通</option>
                  <option value="低">低</option>
              </select>
          </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">キャンセル</button>
        <button type="submit" class="btn btn-primary">保存</button>
      </div>
    </form>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>