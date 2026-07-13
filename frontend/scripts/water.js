const BOTTLE_LITERS = 0.5;

new Vue({
    el: '#app',
    data: {
        userId: null,
        todayQuantity: 0,
        history: [],
        errorMessage: '',
        dailyGoalLiters: 2,
        goalUnit: 'litros',
        goalInput: 2
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        const savedGoal = parseFloat(localStorage.getItem('waterDailyGoalLiters'));
        this.dailyGoalLiters = isNaN(savedGoal) ? 2 : savedGoal;
        this.loadToday();
        this.loadHistory();
    },
    computed: {
        todayLiters() {
            return this.todayQuantity * BOTTLE_LITERS;
        },
        fillPercent() {
            if (this.dailyGoalLiters <= 0) return 0;
            return Math.min(100, Math.round((this.todayLiters / this.dailyGoalLiters) * 100));
        }
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
        },
        toLiters(quantity) {
            return (quantity * BOTTLE_LITERS);
        },
        openGoalModal() {
            this.goalInput = this.dailyGoalLiters;
            this.goalUnit = 'litros';
        },
        saveGoal() {
            const value = parseFloat(this.goalInput);
            if (!value || value <= 0) return;
            this.dailyGoalLiters = this.goalUnit === 'garrafas' ? value * BOTTLE_LITERS : value;
            localStorage.setItem('waterDailyGoalLiters', this.dailyGoalLiters);
            bootstrap.Modal.getOrCreateInstance(document.getElementById('waterGoalModal')).hide();
        }
    }
});

document.getElementById('logout-button').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
});
