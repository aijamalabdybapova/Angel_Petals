// Функция для показа деталей аудита
function showAuditDetails(auditId) {
    console.log('Loading audit details for ID:', auditId);

    // Сначала показываем модальное окно
    const modalElement = document.getElementById('auditDetailsModal');
    const modal = new bootstrap.Modal(modalElement);
    modal.show();

    // Затем загружаем данные
    fetch('/api/audit/' + auditId)
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Audit data received:', data);
            if (data.success) {
                const auditLog = data.data;
                const content = `
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <strong>Таблица:</strong> ${auditLog.tableName}
                        </div>
                        <div class="col-md-6">
                            <strong>Действие:</strong>
                            <span class="badge bg-${getActionBadgeClass(auditLog.action)}">
                                ${getActionText(auditLog.action)}
                            </span>
                        </div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-md-6">
                            <strong>ID записи:</strong> ${auditLog.recordId}
                        </div>
                        <div class="col-md-6">
                            <strong>Пользователь:</strong> ${auditLog.changedBy || 'Система'}
                        </div>
                    </div>
                    <div class="row mb-3">
                        <div class="col-12">
                            <strong>Дата:</strong> ${new Date(auditLog.changedAt).toLocaleString('ru-RU')}
                        </div>
                    </div>
                    ${auditLog.oldData ? `
                    <div class="row mb-3">
                        <div class="col-12">
                            <strong>Старые данные:</strong>
                            <pre class="mt-2 p-2 bg-light border rounded" style="font-size: 0.8rem;">${formatJsonData(auditLog.oldData)}</pre>
                        </div>
                    </div>
                    ` : ''}
                    ${auditLog.newData ? `
                    <div class="row">
                        <div class="col-12">
                            <strong>Новые данные:</strong>
                            <pre class="mt-2 p-2 bg-light border rounded" style="font-size: 0.8rem;">${formatJsonData(auditLog.newData)}</pre>
                        </div>
                    </div>
                    ` : ''}
                    ${!auditLog.oldData && !auditLog.newData ? `
                    <div class="alert alert-info">
                        <i class="fas fa-info-circle me-2"></i>
                        Нет дополнительных данных для отображения
                    </div>
                    ` : ''}
                `;
                document.getElementById('auditDetailsContent').innerHTML = content;
            } else {
                document.getElementById('auditDetailsContent').innerHTML =
                    '<div class="alert alert-danger">Ошибка: ' + (data.message || 'Неизвестная ошибка') + '</div>';
            }
        })
        .catch(error => {
            console.error('Error loading audit details:', error);
            document.getElementById('auditDetailsContent').innerHTML =
                '<div class="alert alert-danger">Ошибка загрузки деталей аудита: ' + error.message + '</div>';
        });
}

// Вспомогательные функции для стилей действий
function getActionBadgeClass(action) {
    const actionClasses = {
        'CREATE': 'success',
        'UPDATE': 'warning',
        'DELETE': 'danger'
    };
    return actionClasses[action] || 'secondary';
}

function getActionText(action) {
    const actionTexts = {
        'CREATE': 'Создание',
        'UPDATE': 'Обновление',
        'DELETE': 'Удаление'
    };
    return actionTexts[action] || action;
}

// Функция для форматирования JSON данных
function formatJsonData(jsonString) {
    if (!jsonString) return 'Нет данных';

    try {
        // Пробуем распарсить JSON
        const parsed = JSON.parse(jsonString);
        return JSON.stringify(parsed, null, 2);
    } catch (e) {
        // Если не JSON, возвращаем как есть
        return jsonString;
    }
}

// Функция применения фильтров
function applyFilters() {
    const tableName = document.getElementById('tableFilter').value;
    const action = document.getElementById('actionFilter').value;
    const username = document.getElementById('userFilter').value.trim();

    let url = '/admin/audit?';
    const params = [];

    if (tableName) params.push('table=' + encodeURIComponent(tableName));
    if (action) params.push('action=' + encodeURIComponent(action));
    if (username) params.push('username=' + encodeURIComponent(username));

    window.location.href = url + params.join('&');
}

// Функция экспорта аудита
function exportAudit() {
    console.log('Exporting audit data...');

    fetch('/api/audit?size=1000')
        .then(response => {
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            console.log('Export data received:', data);
            if (data.success) {
                downloadAuditCSV(data.data.content);
                showNotification('Журнал аудита экспортирован успешно', 'success');
            } else {
                showNotification('Ошибка при экспорте: ' + (data.message || 'Неизвестная ошибка'), 'error');
            }
        })
        .catch(error => {
            console.error('Error exporting audit:', error);
            showNotification('Ошибка при экспорте журнала аудита: ' + error.message, 'error');
        });
}

// Функция скачивания CSV
function downloadAuditCSV(auditLogs) {
    if (!auditLogs || auditLogs.length === 0) {
        showNotification('Нет данных для экспорта', 'warning');
        return;
    }

    const headers = ['ID', 'Дата', 'Таблица', 'Действие', 'ID записи', 'Пользователь'];
    const csvContent = [
        headers.join(','),
        ...auditLogs.map(log => [
            log.id,
            new Date(log.changedAt).toLocaleString('ru-RU'),
            '"' + log.tableName + '"',
            '"' + getActionText(log.action) + '"',
            log.recordId,
            '"' + (log.changedBy || 'Система') + '"'
        ].join(','))
    ].join('\n');

    const blob = new Blob(['\uFEFF' + csvContent], { type: 'text/csv;charset=utf-8;' });
    const link = document.createElement('a');
    const url = URL.createObjectURL(blob);

    link.setAttribute('href', url);
    link.setAttribute('download', 'audit_log_' + new Date().toISOString().split('T')[0] + '.csv');
    link.style.visibility = 'hidden';

    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
}

// Функция показа уведомлений
function showNotification(message, type) {
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-' + (type === 'error' ? 'danger' : type) + ' alert-dismissible fade show position-fixed';
    alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 1060; min-width: 300px;';

    const icon = type === 'success' ? 'check' : 'exclamation-circle';
    alertDiv.innerHTML = `
        <i class="fas fa-${icon} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;

    document.body.appendChild(alertDiv);

    setTimeout(() => {
        if (alertDiv.parentNode) {
            alertDiv.remove();
        }
    }, 5000);
}

// Инициализация при загрузке страницы
document.addEventListener('DOMContentLoaded', function() {
    const userFilter = document.getElementById('userFilter');
    if (userFilter) {
        userFilter.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                applyFilters();
            }
        });
    }

    // Заполняем текущие значения фильтров из URL
    const urlParams = new URLSearchParams(window.location.search);
    if (urlParams.has('table')) {
        document.getElementById('tableFilter').value = urlParams.get('table');
    }
    if (urlParams.has('action')) {
        document.getElementById('actionFilter').value = urlParams.get('action');
    }
    if (urlParams.has('username')) {
        document.getElementById('userFilter').value = urlParams.get('username');
    }

    // Проверяем, что Bootstrap загружен
    if (typeof bootstrap === 'undefined') {
        console.error('Bootstrap not loaded!');
    } else {
        console.log('Bootstrap loaded successfully');
    }
});