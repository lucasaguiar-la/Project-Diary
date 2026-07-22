new Vue({
    el: '#app',
    data: {
        history: [],
        userId: null,
        errorMessage: ''
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
        },
        moodDistribution() {
            const counts = {};
            this.history.forEach(entry => {
                const mood = entry.moods && entry.moods[0];
                if (!mood) return;
                if (!counts[mood.title]) {
                    counts[mood.title] = { title: mood.title, emoji: mood.emoji, count: 0 };
                }
                counts[mood.title].count++;
            });
            const total = this.history.length;
            return Object.values(counts)
                .map(m => ({
                    ...m,
                    percent: total ? Math.round((m.count / total) * 100) : 0,
                    color: window.MOOD_COLORS[m.title] ? window.MOOD_COLORS[m.title].base : '#6C757D'
                }))
                .sort((a, b) => b.count - a.count);
        },
        dominantMood() {
            return this.moodDistribution.length ? this.moodDistribution[0] : null;
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
        loadHistory() {
            fetch(`/api/moods/user/${this.userId}`, {
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(data => {
                this.history = data;
            })
            .catch(err => { this.errorMessage = err.message; });
        },
        formatDate(dateStr) {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            return date.toLocaleDateString('pt-BR', { day: 'numeric', month: 'long', year: 'numeric' });
        },
        formatTime(dateStr) {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            return date.toLocaleTimeString('pt-BR', { hour: '2-digit', minute: '2-digit' });
        },
        cardStyle(entry) {
            const title = entry.moods && entry.moods[0] ? entry.moods[0].title : null;
            const c = title ? window.MOOD_COLORS[title] : null;
            return {
                backgroundColor: window.moodSoftBg(title, 0.14),
                borderLeft: c ? '4px solid ' + c.base : ''
            };
        }
    }
});

document.getElementById('logout-button').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
});
