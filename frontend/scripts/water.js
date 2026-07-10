new Vue({
    el: '#app',
    data: {
        userId: null,
        todayQuantity: 0,
        history: [],
        errorMessage: ''
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        this.loadToday();
        this.loadHistory();
    },
    methods: {
        getAuthHeaders() {
            const token = localStorage.getItem('jwtToken');
            return {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            };
        },
        handleFetch(res) {
            if (res.status === 401) {
                localStorage.removeItem('jwtToken');
                localStorage.removeItem('userId');
                window.location.href = 'login.html';
                throw new Error('Sessao expirada.');
            }
            if (!res.ok) {
                throw new Error('Erro no servidor. Tente novamente.');
            }
            return res;
        },
        loadToday() {
            fetch(`/api/water/user/${this.userId}`, { headers: this.getAuthHeaders() })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => { this.todayQuantity = data.quantity; })
                .catch(err => { this.errorMessage = err.message; });
        },
        loadHistory() {
            fetch(`/api/water/user/${this.userId}/history?days=7`, { headers: this.getAuthHeaders() })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => { this.history = data; })
                .catch(err => { this.errorMessage = err.message; });
        },
        increment() {
            fetch(`/api/water/increment?userId=${this.userId}`, {
                method: 'POST',
                headers: this.getAuthHeaders()
            })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => {
                    this.todayQuantity = data.quantity;
                    this.loadHistory();
                })
                .catch(err => { this.errorMessage = err.message; });
        },
        decrement() {
            fetch(`/api/water/decrement?userId=${this.userId}`, {
                method: 'POST',
                headers: this.getAuthHeaders()
            })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => {
                    this.todayQuantity = data.quantity;
                    this.loadHistory();
                })
                .catch(err => { this.errorMessage = err.message; });
        }
    }
});

document.getElementById('logout-button').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
});
