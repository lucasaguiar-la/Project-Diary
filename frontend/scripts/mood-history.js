new Vue({
    el: '#app',
    data: {
        history: [],
        userId: null
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        this.loadHistory();
    },
    computed: {
        sortedHistory() {
            return this.history.slice().sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt));
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
        loadHistory() {
            fetch(`/api/moods/user/${this.userId}`, {
                headers: this.getAuthHeaders()
            })
            .then(res => res.json())
            .then(data => {
                this.history = data;
            })
            .catch(err => console.error('Erro ao carregar histórico:', err));
        },
        formatDate(dateStr) {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            return date.toLocaleDateString('pt-BR', { day: 'numeric', month: 'long', year: 'numeric' });
        }
    }
});

document.getElementById('logout-button').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
});
