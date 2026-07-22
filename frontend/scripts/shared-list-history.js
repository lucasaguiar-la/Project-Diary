new Vue({
    el: '#app',
    data: {
        userId: null,
        listId: null,
        history: [],
        errorMessage: ''
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        const params = new URLSearchParams(window.location.search);
        this.listId = params.get('listId');
        if (!this.listId) {
            window.location.href = 'groups.html';
            return;
        }
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
        loadHistory() {
            fetch(`/api/shared-lists/${this.listId}/history?userId=${this.userId}`, { headers: this.getAuthHeaders() })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => { this.history = data; })
                .catch(err => { this.errorMessage = err.message; });
        },
        formatDate(value) {
            if (!value) return '';
            return new Date(value).toLocaleDateString('pt-BR');
        },
        formatTime(value) {
            if (!value) return '';
            return new Date(value).toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
        },
        goBack() {
            window.history.back();
        }
    }
});

document.getElementById('logout-button').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
});
