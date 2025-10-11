// Main JavaScript file for Flower Shop

// Cart functionality
class Cart {
    constructor() {
        this.items = JSON.parse(localStorage.getItem('flowerShopCart')) || [];
        this.updateCartCounter();
    }

    addItem(bouquetId, quantity = 1) {
        const existingItem = this.items.find(item => item.bouquetId === bouquetId);

        if (existingItem) {
            existingItem.quantity += quantity;
        } else {
            this.items.push({
                bouquetId: bouquetId,
                quantity: quantity,
                addedAt: new Date().toISOString()
            });
        }

        this.save();
        this.updateCartCounter();
    }

    removeItem(bouquetId) {
        this.items = this.items.filter(item => item.bouquetId !== bouquetId);
        this.save();
        this.updateCartCounter();
    }

    updateQuantity(bouquetId, quantity) {
        const item = this.items.find(item => item.bouquetId === bouquetId);
        if (item) {
            item.quantity = quantity;
            this.save();
        }
    }

    clear() {
        this.items = [];
        this.save();
        this.updateCartCounter();
    }

    getItemCount() {
        return this.items.reduce((total, item) => total + item.quantity, 0);
    }

    save() {
        localStorage.setItem('flowerShopCart', JSON.stringify(this.items));
    }

    updateCartCounter() {
        const counter = document.getElementById('cartCounter');
        if (counter) {
            const count = this.getItemCount();
            counter.textContent = count;
            counter.style.display = count > 0 ? 'inline' : 'none';
        }
    }

    getItems() {
        return this.items;
    }
}

// Initialize cart
const cart = new Cart();

// Utility functions
const FlowerShop = {
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
    addToCart: function(bouquetId, quantity = 1) {
        cart.addItem(bouquetId, quantity);
        this.showNotification('Товар добавлен в корзину!', 'success');
    },

    // Remove from cart handler
    removeFromCart: function(bouquetId) {
        cart.removeItem(bouquetId);
        this.showNotification('Товар удален из корзины', 'info');
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

    // Initialize filters
    initFilters: function() {
        const searchInput = document.getElementById('searchInput');
        const categoryFilter = document.getElementById('categoryFilter');
        const priceFilter = document.getElementById('priceFilter');

        if (searchInput) {
            searchInput.addEventListener('input', this.debounce(function() {
                const searchValue = this.value.trim();
                if (searchValue.length >= 2 || searchValue.length === 0) {
                    // Trigger search - you can implement AJAX search here
                    console.log('Search:', searchValue);
                }
            }, 500));
        }

        if (categoryFilter) {
            categoryFilter.addEventListener('change', function() {
                // Trigger category filter - implement AJAX or form submission
                console.log('Category filter:', this.value);
            });
        }
    }
};

// Initialize when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    FlowerShop.initImagePreview();
    FlowerShop.initQuantityControls();
    FlowerShop.initFilters();

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
window.cart = cart;