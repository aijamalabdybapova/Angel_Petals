// Admin functionality for Flower Shop
class AdminManager {
    constructor() {
        this.init();
    }

    init() {
        this.bindEvents();
        this.loadStatistics();
    }

    bindEvents() {
        // User management
        document.addEventListener('click', (e) => {
            if (e.target.closest('.change-role-btn')) {
                const button = e.target.closest('.change-role-btn');
                const userId = button.dataset.userId;
                this.showRoleChangeModal(userId);
            }

            if (e.target.closest('.delete-user-btn')) {
                const button = e.target.closest('.delete-user-btn');
                const userId = button.dataset.userId;
                this.deleteUser(userId);
            }

            if (e.target.closest('.restore-user-btn')) {
                const button = e.target.closest('.restore-user-btn');
                const userId = button.dataset.userId;
                this.restoreUser(userId);
            }
        });

        // Order management
        document.addEventListener('click', (e) => {
            if (e.target.closest('.update-order-status')) {
                const button = e.target.closest('.update-order-status');
                const orderId = button.dataset.orderId;
                const status = button.dataset.status;
                this.updateOrderStatus(orderId, status);
            }
        });

        // Bulk actions
        document.addEventListener('click', (e) => {
            if (e.target.id === 'bulkDeleteBtn') {
                this.bulkDeleteUsers();
            }
        });
    }

    loadStatistics() {
        // Load dashboard statistics
        if (document.getElementById('statsContainer')) {
            this.fetchStatistics();
        }
    }

    fetchStatistics() {
        fetch('/flowershop/api/orders/stats/count-by-status?status=PENDING')
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    const pendingOrdersElement = document.getElementById('pendingOrdersCount');
                    if (pendingOrdersElement) {
                        pendingOrdersElement.textContent = data.data;
                    }
                }
            })
            .catch(error => {
                console.error('Error fetching statistics:', error);
            });
    }

    showRoleChangeModal(userId) {
        // In a real application, this would show a modal with role options
        const newRole = prompt('Введите новую роль (ROLE_USER, ROLE_ADMIN):');
        if (newRole && ['ROLE_USER', 'ROLE_ADMIN'].includes(newRole)) {
            this.changeUserRole(userId, newRole);
        } else if (newRole) {
            this.showNotification('Неверная роль', 'error');
        }
    }

    changeUserRole(userId, newRole) {
        fetch(`/flowershop/api/users/${userId}/role?role=${newRole}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
            }
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                this.showNotification('Роль пользователя изменена', 'success');
                setTimeout(() => location.reload(), 1000);
            } else {
                this.showNotification('Ошибка при изменении роли', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            this.showNotification('Ошибка при изменении роли', 'error');
        });
    }

    deleteUser(userId) {
        if (!confirm('Вы уверены, что хотите удалить этого пользователя?')) return;

        fetch(`/flowershop/api/users/${userId}`, {
            method: 'DELETE'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                this.showNotification('Пользователь удален', 'success');
                setTimeout(() => location.reload(), 1000);
            } else {
                this.showNotification('Ошибка при удалении пользователя', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            this.showNotification('Ошибка при удалении пользователя', 'error');
        });
    }

    restoreUser(userId) {
        fetch(`/flowershop/api/users/${userId}/restore`, {
            method: 'POST'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                this.showNotification('Пользователь восстановлен', 'success');
                setTimeout(() => location.reload(), 1000);
            } else {
                this.showNotification('Ошибка при восстановлении пользователя', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            this.showNotification('Ошибка при восстановлении пользователя', 'error');
        });
    }

    updateOrderStatus(orderId, status) {
        fetch(`/flowershop/api/orders/${orderId}/status?status=${status}`, {
            method: 'PUT'
        })
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                this.showNotification('Статус заказа обновлен', 'success');
                setTimeout(() => location.reload(), 1000);
            } else {
                this.showNotification('Ошибка при обновлении статуса', 'error');
            }
        })
        .catch(error => {
            console.error('Error:', error);
            this.showNotification('Ошибка при обновлении статуса', 'error');
        });
    }

    bulkDeleteUsers() {
        const selectedUsers = Array.from(document.querySelectorAll('.user-checkbox:checked'))
            .map(checkbox => checkbox.value);

        if (selectedUsers.length === 0) {
            this.showNotification('Выберите пользователей для удаления', 'warning');
            return;
        }

        if (!confirm(`Вы уверены, что хотите удалить ${selectedUsers.length} пользователей?`)) return;

        // In a real application, this would send a batch delete request
        selectedUsers.forEach(userId => {
            this.deleteUser(userId);
        });
    }

    showNotification(message, type = 'info') {
        const alertClass = {
            'success': 'alert-success',
            'error': 'alert-danger',
            'warning': 'alert-warning',
            'info': 'alert-info'
        }[type] || 'alert-info';

        const notification = document.createElement('div');
        notification.className = `alert ${alertClass} alert-dismissible fade show position-fixed`;
        notification.style.cssText = 'top: 20px; right: 20px; z-index: 1050; min-width: 300px;';
        notification.innerHTML = `
            <i class="fas fa-${type === 'success' ? 'check' : 'exclamation'}-circle me-2"></i>
            ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;

        document.body.appendChild(notification);

        setTimeout(() => {
            if (notification.parentNode) {
                notification.remove();
            }
        }, 3000);
    }

    // Chart initialization for statistics
    initCharts() {
        const statsCtx = document.getElementById('statsChart');
        if (statsCtx) {
            // Initialize charts using Chart.js
            this.loadChartData();
        }
    }

    loadChartData() {
        // Load data for charts
        // This would typically make API calls to get chart data
    }
}

// Initialize admin manager when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    window.adminManager = new AdminManager();
});

// Utility functions for admin panel
const AdminUtils = {
    // Export data to CSV
    exportToCSV: function(data, filename) {
        const csvContent = this.convertToCSV(data);
        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8;' });
        const link = document.createElement('a');

        if (link.download !== undefined) {
            const url = URL.createObjectURL(blob);
            link.setAttribute('href', url);
            link.setAttribute('download', filename);
            link.style.visibility = 'hidden';
            document.body.appendChild(link);
            link.click();
            document.body.removeChild(link);
        }
    },

    convertToCSV: function(data) {
        if (data.length === 0) return '';

        const headers = Object.keys(data[0]);
        const csvRows = [];

        // Add headers
        csvRows.push(headers.join(','));

        // Add data rows
        for (const row of data) {
            const values = headers.map(header => {
                const value = row[header] || '';
                const escaped = ('' + value).replace(/"/g, '""');
                return `"${escaped}"`;
            });
            csvRows.push(values.join(','));
        }

        return csvRows.join('\n');
    },

    // Date range picker for statistics
    initDateRangePicker: function() {
        const dateRangeBtn = document.getElementById('dateRangeBtn');
        if (dateRangeBtn) {
            dateRangeBtn.addEventListener('click', function() {
                // Initialize date range picker
                // This would typically use a library like flatpickr
            });
        }
    }
};