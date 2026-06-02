const BASE_URL = 'http://localhost:8081';

new Vue({
    el: '#app',
    data: {
        newNote: {
            text: '',
            mood: 'neutro'
        },
        notes: [],
        moodTagsMap: {},
        userId: null
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        this.loadMoods();
        this.loadNotes();
    },
    computed: {
        sortedNotes() {
            return this.notes.slice().sort((a, b) => b.id - a.id);
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
        loadMoods() {
            fetch(`${BASE_URL}/api/moods`, {
                headers: this.getAuthHeaders()
            })
            .then(res => res.json())
            .then(moods => {
                moods.forEach(m => {
                    this.moodTagsMap[m.title] = m.id;
                });
            })
            .catch(err => console.error('Erro ao carregar moods:', err));
        },
        loadNotes() {
            fetch(`${BASE_URL}/api/notes/user/${this.userId}`, {
                headers: this.getAuthHeaders()
            })
            .then(res => res.json())
            .then(notes => {
                this.notes = notes;
            })
            .catch(err => console.error('Erro ao carregar notas:', err));
        },
        addNote() {
            if (!this.newNote.text) return;

            const moodId = this.moodTagsMap[this.newNote.mood];
            const payload = {
                title: new Date().toLocaleDateString('pt-BR', { day: 'numeric', month: 'long', year: 'numeric' }),
                content: this.newNote.text,
                userId: parseInt(this.userId),
                moodIds: moodId ? [moodId] : []
            };

            fetch(`${BASE_URL}/api/notes`, {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify(payload)
            })
            .then(res => res.json())
            .then(savedNote => {
                this.notes.push(savedNote);
                this.newNote.text = '';
                this.newNote.mood = 'neutro';
            })
            .catch(err => console.error('Erro ao salvar nota:', err));
        },
        formatDate(dateStr) {
            if (!dateStr) return '';
            const date = new Date(dateStr);
            return date.toLocaleDateString('pt-BR', { day: 'numeric', month: 'long', year: 'numeric' });
        },
        getMoodDisplay(note) {
            if (note.moods && note.moods.length > 0) {
                return note.moods[0].emoji + ' ' + note.moods[0].title;
            }
            return '🤔 Sem humor';
        }
    }
});

document.getElementById('logout-button').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
});
