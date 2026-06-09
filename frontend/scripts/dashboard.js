new Vue({
    el: '#app',
    data: {
        activities: [],
        streak: 0,
        newActivityTitle: '',
        userId: null,
        errorMessage: ''
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        this.loadActivities();
        this.loadStreak();
    },
    computed: {
        completedToday() {
            return this.activities.filter(a => a.completedToday).length;
        },
        dayProgress() {
            if (this.activities.length === 0) return 0;
            return Math.round((this.completedToday / this.activities.length) * 100);
        },
        incentiveMessage() {
            if (this.streak === 0) return 'Comece hoje e inicie seu ofensivo!';
            if (this.streak < 7) return 'Ótimo começo! Continue assim todos os dias.';
            if (this.streak < 30) return 'Incrível! Você está construindo um hábito.';
            return 'Parabéns! Você é um exemplo de consistência!';
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
        loadActivities() {
            fetch(`/api/activities/user/${this.userId}`, {
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(data => { this.activities = data; })
            .catch(err => { this.errorMessage = err.message; });
        },
        loadStreak() {
            fetch(`/api/activities/user/${this.userId}/streak`, {
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(data => { this.streak = data.streak; })
            .catch(err => { this.errorMessage = err.message; });
        },
        addActivity() {
            if (!this.newActivityTitle.trim()) return;
            fetch('/api/activities', {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify({ title: this.newActivityTitle.trim(), userId: parseInt(this.userId) })
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(created => {
                this.activities.push({ id: created.id, title: created.title, createdAt: created.createdAt, completedToday: false });
                this.newActivityTitle = '';
            })
            .catch(err => { this.errorMessage = err.message; });
        },
        toggleActivity(activity) {
            if (activity.completedToday) {
                fetch(`/api/activities/${activity.id}/complete/today?userId=${this.userId}`, {
                    method: 'DELETE',
                    headers: this.getAuthHeaders()
                })
                .then(res => this.handleFetch(res))
                .then(() => {
                    activity.completedToday = false;
                    this.loadStreak();
                })
                .catch(err => { this.errorMessage = err.message; });
            } else {
                fetch(`/api/activities/${activity.id}/complete?userId=${this.userId}`, {
                    method: 'POST',
                    headers: this.getAuthHeaders()
                })
                .then(res => this.handleFetch(res))
                .then(() => {
                    activity.completedToday = true;
                    this.loadStreak();
                })
                .catch(err => { this.errorMessage = err.message; });
            }
        },
        deleteActivity(id) {
            if (!confirm('Deseja excluir esta atividade?')) return;
            fetch(`/api/activities/${id}`, {
                method: 'DELETE',
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(() => {
                this.activities = this.activities.filter(a => a.id !== id);
                this.loadStreak();
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
