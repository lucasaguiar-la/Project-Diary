new Vue({
    el: '#app',
    data: {
        newNote: {
            text: '',
            mood: 'neutro'
        },
        notes: [],
        moodTagsMap: {},
        userId: null,
        editingId: null,
        editContent: '',
        errorMessage: ''
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
        loadMoods() {
            fetch(`/api/moods`, {
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(moods => {
                moods.forEach(m => {
                    this.moodTagsMap[m.title] = m.id;
                });
            })
            .catch(err => { this.errorMessage = err.message; });
        },
        loadNotes() {
            fetch(`/api/notes/user/${this.userId}`, {
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(notes => {
                this.notes = notes;
            })
            .catch(err => { this.errorMessage = err.message; });
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

            fetch(`/api/notes`, {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify(payload)
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(savedNote => {
                this.notes.push(savedNote);
                this.newNote.text = '';
                this.newNote.mood = 'neutro';
            })
            .catch(err => { this.errorMessage = err.message; });
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
        },
        startEdit(note) {
            this.editingId = note.id;
            this.editContent = note.content;
        },
        cancelEdit() {
            this.editingId = null;
            this.editContent = '';
        },
        saveEdit(note) {
            fetch(`/api/notes/${note.id}`, {
                method: 'PUT',
                headers: this.getAuthHeaders(),
                body: JSON.stringify({ title: note.title, content: this.editContent })
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(updated => {
                note.content = updated.content;
                this.cancelEdit();
            })
            .catch(err => { this.errorMessage = err.message; });
        },
        deleteNote(id) {
            if (!confirm('Deseja excluir esta nota?')) return;
            fetch(`/api/notes/${id}`, {
                method: 'DELETE',
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(() => {
                this.notes = this.notes.filter(n => n.id !== id);
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
