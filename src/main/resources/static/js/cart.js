// Cart functionality for Flower Shop
class CartManager {
    constructor() {
        this.cart = JSON.parse(localStorage.getItem('flowerShopCart')) || [];
        this.init();
    }

    // Вынесите функцию getCSRFToken ВНЕ класса
    getCSRFToken() {
        return document.querySelector('meta[name="_csrf"]')?.getAttribute('content') || '';
    }

    init() {
        this.updateCartCounter();
        this.bindEvents();
    }

    bindEvents() {
        // Add to cart buttons
        document.addEventListener('click', (e) => {
            if (e.target.closest('.add-to-cart')) {
                const button = e.target.closest('.add-to-cart');
                const bouquetId = button.dataset.bouquetId;
                const quantity = parseInt(button.dataset.quantity) || 1;
                this.addToCart(bouquetId, quantity);
            }
        });

        // Remove from cart buttons
        document.addEventListener('click', (e) => {
            if (e.target.closest('.remove-from-cart')) {
                const button = e.target.closest('.remove-from-cart');
                const bouquetId = button.dataset.bouquetId;
                this.removeFromCart(bouquetId);
            }
        });

        // Update quantity
        document.addEventListener('change', (e) => {
            if (e.target.classList.contains('cart-quantity')) {
                const input = e.target;
                const bouquetId = input.dataset.bouquetId;
                const quantity = parseInt(input.value) || 1;
                this.updateQuantity(bouquetId, quantity);
            }
        });
    }

    // ИСПРАВЛЕННАЯ ФУНКЦИЯ addToCart
    async addToCart(bouquetId, quantity = 1) {
        try {
            const response = await fetch('/api/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'X-CSRF-TOKEN': this.getCSRFToken()
                },
                body: JSON.stringify({
                    bouquetId: parseInt(bouquetId),
                    quantity: quantity
                })
            });

            const result = await response.json();

            if (result.success) {
                this.showNotification('Товар добавлен в корзину!', 'success');
                this.updateCartCounter();
            } else {
                this.showNotification('Ошибка: ' + result.message, 'error');
            }
        } catch (error) {
            console.error('Error:', error);
            this.showNotification('Ошибка при добавлении в корзину', 'error');
        }
    }

    // Остальные методы остаются без изменений...
    removeFromCart(bouquetId) {
        this.cart = this.cart.filter(item => item.bouquetId !== bouquetId);
        this.saveCart();
        this.updateCartCounter();
        this.showNotification('Товар удален из корзины', 'info');

        if (window.location.pathname.includes('/cart')) {
            this.displayCartItems();
        }
    }

    updateQuantity(bouquetId, quantity) {
        if (quantity < 1) {
            this.removeFromCart(bouquetId);
            return;
        }

        const item = this.cart.find(item => item.bouquetId === bouquetId);
        if (item) {
            item.quantity = quantity;
            this.saveCart();

            if (window.location.pathname.includes('/cart')) {
                this.displayCartItems();
            }
        }
    }

    clearCart() {
        this.cart = [];
        this.saveCart();
        this.updateCartCounter();
        this.showNotification('Корзина очищена', 'info');
    }

    getCartItems() {
        return this.cart;
    }

    getItemCount() {
        return this.cart.reduce((total, item) => total + item.quantity, 0);
    }

    getTotalPrice(bouquets = []) {
        return this.cart.reduce((total, cartItem) => {
            const bouquet = bouquets.find(b => b.id == cartItem.bouquetId);
            if (bouquet) {
                return total + (bouquet.price * cartItem.quantity);
            }
            return total;
        }, 0);
    }

    saveCart() {
        localStorage.setItem('flowerShopCart', JSON.stringify(this.cart));
    }

    updateCartCounter() {
        const counters = document.querySelectorAll('.cart-counter');
        const count = this.getItemCount();

        counters.forEach(counter => {
            counter.textContent = count;
            counter.style.display = count > 0 ? 'inline' : 'none';
        });
    }

    displayCartItems() {
        const cartContainer = document.getElementById('cartItems');
        if (!cartContainer) return;

        if (this.cart.length === 0) {
            cartContainer.innerHTML = `
                <div class="text-center py-5">
                    <i class="fas fa-shopping-cart fa-3x text-muted mb-3"></i>
                    <h4>Корзина пуста</h4>
                    <p class="text-muted">Добавьте товары в корзину</p>
                    <a href="/bouquets" class="btn btn-primary">Перейти к букетам</a>
                </div>
            `;
            return;
        }

        cartContainer.innerHTML = `
            <div class="text-center py-3">
                <p class="text-muted">Загрузка корзины...</p>
            </div>
        `;

        this.fetchBouquetDetails().then(bouquets => {
            const totalPrice = this.getTotalPrice(bouquets);

            cartContainer.innerHTML = this.cart.map(cartItem => {
                const bouquet = bouquets.find(b => b.id == cartItem.bouquetId);
                if (!bouquet) return '';

                return `
                    <div class="card mb-3">
                        <div class="card-body">
                            <div class="row align-items-center">
                                <div class="col-md-2">
                                    <img src="${bouquet.imageUrl ? '/uploads/' + bouquet.imageUrl : '/images/default-bouquet.jpg'}"
                                         class="img-fluid rounded"
                                         alt="${bouquet.name}"
                                         style="max-height: 80px; object-fit: cover;">
                                </div>
                                <div class="col-md-4">
                                    <h6 class="mb-1">${bouquet.name}</h6>
                                    <small class="text-muted">${bouquet.category?.name || 'Без категории'}</small>
                                </div>
                                <div class="col-md-2">
                                    <span class="h6 mb-0">₽${bouquet.price.toFixed(2)}</span>
                                </div>
                                <div class="col-md-2">
                                    <div class="input-group input-group-sm">
                                        <button class="btn btn-outline-secondary minus-btn"
                                                type="button"
                                                data-bouquet-id="${bouquet.id}">-</button>
                                        <input type="number"
                                               class="form-control text-center cart-quantity"
                                               value="${cartItem.quantity}"
                                               min="1"
                                               data-bouquet-id="${bouquet.id}">
                                        <button class="btn btn-outline-secondary plus-btn"
                                                type="button"
                                                data-bouquet-id="${bouquet.id}">+</button>
                                    </div>
                                </div>
                                <div class="col-md-2">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="h6 mb-0">₽${(bouquet.price * cartItem.quantity).toFixed(2)}</span>
                                        <button class="btn btn-outline-danger btn-sm remove-from-cart"
                                                data-bouquet-id="${bouquet.id}">
                                            <i class="fas fa-trash"></i>
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                `;
            }).join('') + `
                <div class="card bg-light">
                    <div class="card-body">
                        <div class="row">
                            <div class="col-md-8">
                                <h5 class="mb-0">Итого:</h5>
                            </div>
                            <div class="col-md-4 text-end">
                                <h4 class="text-primary mb-0">₽${totalPrice.toFixed(2)}</h4>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="mt-4">
                    <button class="btn btn-primary btn-lg w-100" id="checkoutBtn">
                        <i class="fas fa-credit-card me-2"></i>Оформить заказ
                    </button>
                    <button class="btn btn-outline-secondary btn-sm w-100 mt-2" id="clearCartBtn">
                        <i class="fas fa-trash me-2"></i>Очистить корзину
                    </button>
                </div>
            `;

            this.bindCartEvents();
        });
    }

    fetchBouquetDetails() {
        return Promise.resolve([]);
    }

    bindCartEvents() {
        document.querySelectorAll('.minus-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const bouquetId = e.target.dataset.bouquetId;
                const input = document.querySelector(`.cart-quantity[data-bouquet-id="${bouquetId}"]`);
                if (input) {
                    input.value = parseInt(input.value) - 1;
                    input.dispatchEvent(new Event('change'));
                }
            });
        });

        document.querySelectorAll('.plus-btn').forEach(btn => {
            btn.addEventListener('click', (e) => {
                const bouquetId = e.target.dataset.bouquetId;
                const input = document.querySelector(`.cart-quantity[data-bouquet-id="${bouquetId}"]`);
                if (input) {
                    input.value = parseInt(input.value) + 1;
                    input.dispatchEvent(new Event('change'));
                }
            });
        });

        const checkoutBtn = document.getElementById('checkoutBtn');
        if (checkoutBtn) {
            checkoutBtn.addEventListener('click', () => {
                this.proceedToCheckout();
            });
        }

        const clearCartBtn = document.getElementById('clearCartBtn');
        if (clearCartBtn) {
            clearCartBtn.addEventListener('click', () => {
                this.clearCart();
            });
        }
    }

    proceedToCheckout() {
        if (this.cart.length === 0) {
            this.showNotification('Корзина пуста', 'warning');
            return;
        }
        window.location.href = '/orders/create';
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
}

// Initialize cart manager when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    window.cartManager = new CartManager();
});