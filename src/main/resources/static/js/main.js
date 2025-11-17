// Main JavaScript file for Flower Shop

// Utility functions
const FlowerShop = {
    // Theme management
    theme: {
        current: 'light',
        
        init: function() {
            // Загружаем сохраненную тему из localStorage
            const savedTheme = localStorage.getItem('theme');
            if (savedTheme) {
                this.current = savedTheme;
            } else {
                // Проверяем системные настройки
                if (window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches) {
                    this.current = 'dark';
                }
            }
            
            this.applyTheme();
            this.updateThemeIcon();
        },
        
        toggle: function() {
            this.current = this.current === 'light' ? 'dark' : 'light';
            this.applyTheme();
            this.updateThemeIcon();
            this.saveTheme();
        },
        
        applyTheme: function() {
            document.documentElement.setAttribute('data-theme', this.current);
            
            // Добавляем класс для плавного перехода
            document.body.classList.add('theme-transition');
            
            // Убираем класс после завершения перехода
            setTimeout(() => {
                document.body.classList.remove('theme-transition');
            }, 300);
        },
        
        updateThemeIcon: function() {
            const themeIcon = document.getElementById('themeIcon');
            if (themeIcon) {
                themeIcon.className = this.current === 'light' ? 'fas fa-moon' : 'fas fa-sun';
            }
        },
        
        saveTheme: function() {
            localStorage.setItem('theme', this.current);
        }
    },
    // Show notification
    showNotification: function(message, type = 'info') {
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
        }, 5000);
    },

    // Format price
    formatPrice: function(price) {
        return new Intl.NumberFormat('ru-RU', {
            style: 'currency',
            currency: 'RUB',
            minimumFractionDigits: 2
        }).format(price);
    },

    // Debounce function for search
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },

    // Add to cart handler
    // Add to cart handler
    addToCart: async function(bouquetId, quantity = 1) {
        try {
            const response = await fetch('/api/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                },
                body: JSON.stringify({
                    bouquetId: parseInt(bouquetId),
                    quantity: quantity
                })
            });

            // Проверяем статус ответа
            if (response.status === 401) {
                // Пользователь не авторизован - показываем модальное окно
                this.showAuthRequiredModal();
                return;
            }

            if (response.status === 403) {
                this.showNotification('У вас нет прав для выполнения этого действия', 'error');
                return;
            }

            const result = await response.json();

            if (result.success) {
                this.showNotification('Товар добавлен в корзину!', 'success');
                // Обновляем счетчик через API
                await this.updateCartCounter();
            } else {
                this.showNotification('Ошибка: ' + result.message, 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            this.showNotification('Ошибка при добавлении в корзину', 'error');
        }
    },

    // Метод для показа модального окна с требованием авторизации
    showAuthRequiredModal: function() {
        // Создаем модальное окно
        const modalHtml = `
            <div class="modal fade" id="authRequiredModal" tabindex="-1" aria-labelledby="authRequiredModalLabel" aria-hidden="true" data-bs-backdrop="static">
                <div class="modal-dialog modal-dialog-centered">
                    <div class="modal-content">
                        <div class="modal-header border-0 pb-0">
                            <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                        </div>
                        <div class="modal-body text-center py-4">
                            <div class="mb-4">
                                <i class="fas fa-shopping-cart fa-4x text-warning mb-4"></i>
                                <h3 class="modal-title mb-3">Требуется авторизация</h3>
                                <p class="text-muted mb-0">
                                    Для добавления товаров в корзину необходимо войти в систему
                                </p>
                            </div>
                        </div>
                        <div class="modal-footer border-0 justify-content-center pt-0">
                            <div class="d-grid gap-2 w-100">
                                <a href="/login" class="btn btn-primary btn-lg">
                                    <i class="fas fa-sign-in-alt me-2"></i>Войти в аккаунт
                                </a>
                                <a href="/register" class="btn btn-outline-primary btn-lg">
                                    <i class="fas fa-user-plus me-2"></i>Зарегистрироваться
                                </a>
                                <button type="button" class="btn btn-link text-muted" data-bs-dismiss="modal">
                                    Продолжить покупки без регистрации
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        `;

        // Удаляем старую модалку если есть
        const oldModal = document.getElementById('authRequiredModal');
        if (oldModal) {
            oldModal.remove();
        }

        // Добавляем модалку в body
        document.body.insertAdjacentHTML('beforeend', modalHtml);

        // Показываем модалку
        const modalElement = document.getElementById('authRequiredModal');
        const modal = new bootstrap.Modal(modalElement);
        modal.show();

        // Очищаем модалку после закрытия
        modalElement.addEventListener('hidden.bs.modal', function () {
            modalElement.remove();
        });
    },

    // Remove from cart handler
    removeFromCart: async function(bouquetId) {
        try {
            const response = await fetch(`/api/cart/remove/${bouquetId}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            });

            const result = await response.json();

            if (result.success) {
                this.showNotification('Товар удален из корзины', 'info');
                await this.updateCartCounter();
            } else {
                this.showNotification('Ошибка: ' + result.message, 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            this.showNotification('Ошибка при удалении из корзины', 'error');
        }
    },

    // Метод обновления счетчика корзины через API
    updateCartCounter: async function() {
        try {
            const response = await fetch('/api/cart/count');
            const result = await response.json();

            if (result.success) {
                const counters = document.querySelectorAll('.cart-counter');
                counters.forEach(counter => {
                    counter.textContent = result.data;
                    counter.style.display = result.data > 0 ? 'inline' : 'none';
                });
            }
        } catch (error) {
            console.error('Error updating cart counter:', error);
        }
    },

    // Функции для работы с корзиной
    updateQuantity: async function(bouquetId, change) {
        const quantityInput = document.getElementById('quantity-' + bouquetId);
        if (!quantityInput) return;

        let newQuantity = parseInt(quantityInput.value) + change;

        if (newQuantity < 1) newQuantity = 1;
        if (newQuantity > 99) newQuantity = 99;

        await this.updateCartItem(bouquetId, newQuantity);
    },

    updateQuantityInput: async function(bouquetId) {
        const quantityInput = document.getElementById('quantity-' + bouquetId);
        if (!quantityInput) return;

        let newQuantity = parseInt(quantityInput.value);

        if (newQuantity < 1) newQuantity = 1;
        if (newQuantity > 99) newQuantity = 99;

        quantityInput.value = newQuantity;
        await this.updateCartItem(bouquetId, newQuantity);
    },

    updateCartItem: async function(bouquetId, quantity) {
        try {
            const response = await fetch(`/api/cart/update/${bouquetId}?quantity=${quantity}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            });

            const result = await response.json();

            if (result.success) {
                location.reload(); // Перезагружаем страницу для обновления данных
            } else {
                this.showNotification('Ошибка при обновлении корзины: ' + result.message, 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            this.showNotification('Ошибка при обновлении корзины', 'error');
        }
    },

    clearCart: async function() {
        if (!confirm('Очистить всю корзину?')) return;

        try {
            const response = await fetch('/api/cart/clear', {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
                }
            });

            const result = await response.json();

            if (result.success) {
                location.reload();
            } else {
                this.showNotification('Ошибка при очистке корзины: ' + result.message, 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            this.showNotification('Ошибка при очистке корзины', 'error');
        }
    },

    // Initialize image preview
    initImagePreview: function() {
        const imageInputs = document.querySelectorAll('input[type="file"][accept="image/*"]');
        imageInputs.forEach(input => {
            input.addEventListener('change', function(e) {
                const file = e.target.files[0];
                if (file) {
                    const reader = new FileReader();
                    const previewId = this.getAttribute('data-preview') || 'imagePreview';
                    const preview = document.getElementById(previewId);

                    reader.onload = function(e) {
                        if (preview) {
                            const img = preview.querySelector('img') || document.createElement('img');
                            img.src = e.target.result;
                            img.className = 'img-fluid';
                            img.style.maxHeight = '300px';
                            img.style.objectFit = 'cover';

                            if (!preview.querySelector('img')) {
                                preview.appendChild(img);
                            }
                            preview.style.display = 'block';
                        }
                    }

                    reader.readAsDataURL(file);
                }
            });
        });
    },

    // Initialize quantity controls
    initQuantityControls: function() {
        document.querySelectorAll('.quantity-control').forEach(control => {
            const input = control.querySelector('input[type="number"]');
            const minusBtn = control.querySelector('.btn-minus');
            const plusBtn = control.querySelector('.btn-plus');

            if (minusBtn && input) {
                minusBtn.addEventListener('click', () => {
                    const currentValue = parseInt(input.value) || 0;
                    if (currentValue > parseInt(input.min || 1)) {
                        input.value = currentValue - 1;
                        input.dispatchEvent(new Event('change'));
                    }
                });
            }

            if (plusBtn && input) {
                plusBtn.addEventListener('click', () => {
                    const currentValue = parseInt(input.value) || 0;
                    const maxValue = parseInt(input.max) || 999;
                    if (currentValue < maxValue) {
                        input.value = currentValue + 1;
                        input.dispatchEvent(new Event('change'));
                    }
                });
            }
        });
    },

    // Initialize filters for catalog page
    initCatalogFilters: function() {
        const filterForm = document.getElementById('filterForm');
        const categoryFilter = document.getElementById('categoryFilter');
        const sortFilter = document.getElementById('sortFilter');
        const directionFilter = document.getElementById('directionFilter');
        const searchInput = document.getElementById('searchInput');
        const pageInput = document.getElementById('pageInput');

        console.log('=== DEBUG: initCatalogFilters started ===');
        console.log('Current URL:', window.location.href);
        console.log('Filter form found:', !!filterForm);
        console.log('Category filter found:', !!categoryFilter);
        console.log('Form action:', filterForm ? filterForm.action : 'N/A');

        if (!filterForm) {
            console.log('DEBUG: No filter form found on this page');
            return;
        }

        // Проверяем, что форма отправляется на правильный URL
        console.log('Form action before:', filterForm.action);

        // Принудительно устанавливаем правильный action
        if (!filterForm.action.includes('/bouquets')) {
            filterForm.action = '/bouquets';
            console.log('Form action corrected to:', filterForm.action);
        }

        // Auto-submit on filter changes
        if (categoryFilter) {
            categoryFilter.addEventListener('change', function() {
                console.log('DEBUG: Category filter changed to:', this.value);
                if (pageInput) {
                    pageInput.value = 0;
                    console.log('DEBUG: Page reset to 0');
                }
                console.log('DEBUG: Submitting form to:', filterForm.action);
                filterForm.submit();
            });
        }

        if (sortFilter) {
            sortFilter.addEventListener('change', function() {
                console.log('DEBUG: Sort filter changed to:', this.value);
                if (pageInput) pageInput.value = 0;
                filterForm.submit();
            });
        }

        if (directionFilter) {
            directionFilter.addEventListener('change', function() {
                console.log('DEBUG: Direction filter changed to:', this.value);
                if (pageInput) pageInput.value = 0;
                filterForm.submit();
            });
        }

        // Search with debounce
        if (searchInput) {
            searchInput.addEventListener('input', this.debounce(function() {
                console.log('DEBUG: Search input changed to:', this.value);
                if (pageInput) pageInput.value = 0;
                filterForm.submit();
            }, 800));
        }

        // Handle pagination clicks
        this.initCatalogPagination();
        console.log('=== DEBUG: initCatalogFilters completed ===');
    },

    // Initialize pagination for catalog
    initCatalogPagination: function() {
        const paginationLinks = document.querySelectorAll('.pagination-link');
        const pageInput = document.getElementById('pageInput');
        const filterForm = document.getElementById('filterForm');

        if (!filterForm) return;

        paginationLinks.forEach(link => {
            link.addEventListener('click', function(e) {
                e.preventDefault();
                const url = new URL(this.href);
                const page = url.searchParams.get('page');

                if (pageInput && page !== null) {
                    pageInput.value = page;
                    filterForm.submit();
                }
            });
        });
    },

    // Initialize filters for admin manage page
    initAdminFilters: function() {
        const searchInput = document.getElementById('searchInput');
        const searchButton = document.getElementById('searchButton');
        const statusFilter = document.getElementById('statusFilter');
        const categoryFilter = document.getElementById('categoryFilter');
        const resetFilters = document.getElementById('resetFilters');

        console.log('=== DEBUG: initAdminFilters started ===');
        console.log('Current URL:', window.location.href);
        console.log('Search input found:', !!searchInput);
        console.log('Category filter found:', !!categoryFilter);

        if (!searchInput) {
            console.log('DEBUG: Not on admin manage page');
            return;
        }

        // Убедимся, что мы на странице управления
        const isManagePage = window.location.pathname.includes('/bouquets/manage');
        if (!isManagePage) {
            console.log('DEBUG: Not on manage page');
            return;
        }

        // Search functionality
        if (searchButton && searchInput) {
            searchButton.addEventListener('click', function() {
                const query = searchInput.value ? searchInput.value.trim() : '';
                console.log('DEBUG: Search button clicked, query:', query);
                if (query) {
                    window.location.href = `/bouquets/manage?search=${encodeURIComponent(query)}`;
                } else {
                    window.location.href = '/bouquets/manage';
                }
            });

            searchInput.addEventListener('keypress', function(e) {
                if (e.key === 'Enter') {
                    console.log('DEBUG: Enter pressed in search');
                    searchButton.click();
                }
            });
        }

        // Filter functionality
        if (statusFilter) {
            statusFilter.addEventListener('change', function() {
                console.log('DEBUG: Status filter changed to:', this.value);
                applyFilters();
            });
        }

        if (categoryFilter) {
            categoryFilter.addEventListener('change', function() {
                console.log('DEBUG: Category filter changed to:', this.value);
                applyFilters();
            });
        }

        if (resetFilters) {
            resetFilters.addEventListener('click', function() {
                console.log('DEBUG: Reset filters clicked');
                if (statusFilter) statusFilter.value = '';
                if (categoryFilter) categoryFilter.value = '';
                window.location.href = '/bouquets/manage';
            });
        }

        function applyFilters() {
            const status = statusFilter ? statusFilter.value : '';
            const category = categoryFilter ? categoryFilter.value : '';
            const search = searchInput ? searchInput.value.trim() : '';

            console.log('DEBUG: Applying filters - status:', status, 'category:', category, 'search:', search);

            let url = '/bouquets/manage?';
            const params = [];

            if (search) params.push(`search=${encodeURIComponent(search)}`);
            if (status) params.push(`status=${status}`);
            if (category) params.push(`category=${category}`);

            const finalUrl = url + params.join('&');
            console.log('DEBUG: Redirecting to:', finalUrl);
            window.location.href = finalUrl;
        }

        // Заполняем текущие значения фильтров из URL
        this.fillCurrentFilterValues();
        console.log('=== DEBUG: initAdminFilters completed ===');
    },

    // Заполнение текущих значений фильтров
    fillCurrentFilterValues: function() {
        const urlParams = new URLSearchParams(window.location.search);
        const statusFilter = document.getElementById('statusFilter');
        const categoryFilter = document.getElementById('categoryFilter');
        const searchInput = document.getElementById('searchInput');

        if (statusFilter && urlParams.has('status')) {
            statusFilter.value = urlParams.get('status');
            console.log('DEBUG: Set status filter to:', statusFilter.value);
        }

        if (categoryFilter && urlParams.has('category')) {
            categoryFilter.value = urlParams.get('category');
            console.log('DEBUG: Set category filter to:', categoryFilter.value);
        }

        if (searchInput && urlParams.has('search')) {
            searchInput.value = urlParams.get('search');
            console.log('DEBUG: Set search input to:', searchInput.value);
        }
    },

    // Инициализация обработчиков для корзины
    initCartHandlers: function() {
        // Обработчики для кнопок "+" и "-" в корзине
        document.addEventListener('click', (e) => {
            if (e.target.closest('.btn-outline-primary')) {
                const button = e.target.closest('.btn-outline-primary');
                const onclickAttr = button.getAttribute('onclick');

                if (onclickAttr && onclickAttr.includes('updateQuantity')) {
                    e.preventDefault();
                    const matches = onclickAttr.match(/updateQuantity\((\d+),\s*(-?\d+)\)/);
                    if (matches) {
                        const bouquetId = matches[1];
                        const change = parseInt(matches[2]);
                        this.updateQuantity(bouquetId, change);
                    }
                }
            }

            // Обработчики для кнопок удаления
            if (e.target.closest('.btn-outline-danger')) {
                const button = e.target.closest('.btn-outline-danger');
                const onclickAttr = button.getAttribute('onclick');

                if (onclickAttr && onclickAttr.includes('removeFromCart')) {
                    e.preventDefault();
                    const matches = onclickAttr.match(/removeFromCart\((\d+)\)/);
                    if (matches) {
                        const bouquetId = matches[1];
                        this.removeFromCart(bouquetId);
                    }
                }
            }

            // Обработчик для кнопки очистки корзины
            if (e.target.closest('button') && e.target.closest('button').onclick === 'clearCart()') {
                e.preventDefault();
                this.clearCart();
            }
        });

        // Обработка изменения количества через input
        document.addEventListener('change', (e) => {
            if (e.target.classList.contains('form-control') && e.target.id && e.target.id.startsWith('quantity-')) {
                const bouquetId = e.target.id.replace('quantity-', '');
                this.updateQuantityInput(bouquetId);
            }
        });
    }
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    // Initialize theme system
    FlowerShop.theme.init();
    
    // Initialize theme toggle button
    const themeToggle = document.getElementById('themeToggle');
    if (themeToggle) {
        themeToggle.addEventListener('click', function() {
            FlowerShop.theme.toggle();
        });
    }
    
    // Initialize common functionality
    FlowerShop.initImagePreview();
    FlowerShop.initQuantityControls();

    // Initialize specific page functionality
    FlowerShop.initCatalogFilters();      // For catalog page (/bouquets)
    FlowerShop.initAdminFilters();        // For admin manage page (/bouquets/manage)
    FlowerShop.initCartHandlers();        // For cart page

    // Обновляем счетчик корзины при загрузке страницы
    FlowerShop.updateCartCounter();

    // Add to cart buttons
    document.querySelectorAll('.add-to-cart').forEach(button => {
        button.addEventListener('click', function() {
            const bouquetId = this.getAttribute('data-bouquet-id');
            const quantity = parseInt(this.getAttribute('data-quantity')) || 1;
            FlowerShop.addToCart(bouquetId, quantity);
        });
    });

    // Initialize tooltips
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    const tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });

    // Initialize popovers
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    const popoverList = popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
});

// Export for global access
window.FlowerShop = FlowerShop;

// Глобальные функции для обратной совместимости с HTML onclick атрибутами
window.updateQuantity = function(bouquetId, change) {
    FlowerShop.updateQuantity(bouquetId, change);
};

window.updateQuantityInput = function(bouquetId) {
    FlowerShop.updateQuantityInput(bouquetId);
};

window.removeFromCart = function(bouquetId) {
    FlowerShop.removeFromCart(bouquetId);
};

window.clearCart = function() {
    FlowerShop.clearCart();
};