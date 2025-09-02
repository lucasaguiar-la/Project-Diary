new Vue({
    el: '#app',
    data: {
        newNote: {
            text: '',
            mood: 'neutro'
        },
        notes: [
            {
                id: 1,
                text: 'Tive um dia produtivo e consegui resolver um bug complicado. Estou me sentindo ótimo!',
                mood: 'feliz',
                date: '2 de Setembro de 2025'
            },
            {
                id: 2,
                text: 'A reunião de hoje foi um pouco longa e cansativa. Nada de especial aconteceu.',
                mood: 'neutro',
                date: '1 de Setembro de 2025'
            }
        ],
        nextNoteId: 3
    },
    filters: {
        capitalize: function (value) {
            if (!value) return ''
            value = value.toString()
            return value.charAt(0).toUpperCase() + value.slice(1)
        }
    },
    computed: {
        sortedNotes() {
            return this.notes.slice().sort((a, b) => b.id - a.id);
        }
    },
    methods: {
        addNote() {
            if (!this.newNote.text) return;

            this.notes.push({
                id: this.nextNoteId++,
                text: this.newNote.text,
                mood: this.newNote.mood,
                date: new Date().toLocaleDateString('pt-BR', { day: 'numeric', month: 'long', year: 'numeric' })
            });

            this.newNote.text = '';
            this.newNote.mood = 'neutro';
        },
        getMoodIcon(mood) {
            switch (mood) {
                case 'feliz': return '😊';
                case 'neutro': return '😐';
                case 'triste': return '😢';
                case 'ansioso': return '😟';
                case 'calmo': return '😌';
                default: return '🤔';
            }
        }
    }
});

document.getElementById('logout-button').addEventListener('click', () => {
    alert('Você foi desconectado.');
    window.location.href = 'login.html';
});
