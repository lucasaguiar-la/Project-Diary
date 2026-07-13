new Vue({
    el: '#app',
    data: {
        currentYear: new Date().getFullYear(),
        currentMonth: new Date().getMonth() + 1,
        moodsByDate: {},
        eventsByDate: {},
        streakDates: [],
        selectedDay: null,
        newEventTitle: '',
        newEventTime: '',
        userId: null,
        errorMessage: ''
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        this.loadMonthData();
    },
    computed: {
        monthName() {
            const names = ['Janeiro','Fevereiro','Março','Abril','Maio','Junho',
                           'Julho','Agosto','Setembro','Outubro','Novembro','Dezembro'];
            return names[this.currentMonth - 1];
        },
        calendarDays() {
            const year = this.currentYear;
            const month = this.currentMonth;
            const firstDay = new Date(year, month - 1, 1).getDay();
            const daysInMonth = new Date(year, month, 0).getDate();
            const daysInPrevMonth = new Date(year, month - 1, 0).getDate();
            const today = new Date();
            const todayStr = `${today.getFullYear()}-${String(today.getMonth() + 1).padStart(2, '0')}-${String(today.getDate()).padStart(2, '0')}`;

            const cells = [];

            // Days from previous month
            for (let i = firstDay - 1; i >= 0; i--) {
                const day = daysInPrevMonth - i;
                const prevMonth = month - 1 === 0 ? 12 : month - 1;
                const prevYear = month - 1 === 0 ? year - 1 : year;
                const dateStr = `${prevYear}-${String(prevMonth).padStart(2, '0')}-${String(day).padStart(2, '0')}`;
                cells.push({ day, dateStr, isCurrentMonth: false, isToday: false, mood: null, events: [], isStreakDay: false });
            }

            // Days of current month
            for (let d = 1; d <= daysInMonth; d++) {
                const dateStr = `${year}-${String(month).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
                cells.push({
                    day: d,
                    dateStr,
                    isCurrentMonth: true,
                    isToday: dateStr === todayStr,
                    mood: this.moodsByDate[dateStr] || null,
                    events: this.eventsByDate[dateStr] || [],
                    isStreakDay: this.streakDates.includes(dateStr)
                });
            }

            // Days from next month to fill last row
            const remaining = cells.length % 7 === 0 ? 0 : 7 - (cells.length % 7);
            for (let d = 1; d <= remaining; d++) {
                const nextMonth = month + 1 === 13 ? 1 : month + 1;
                const nextYear = month + 1 === 13 ? year + 1 : year;
                const dateStr = `${nextYear}-${String(nextMonth).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
                cells.push({ day: d, dateStr, isCurrentMonth: false, isToday: false, mood: null, events: [], isStreakDay: false });
            }

            return cells;
        },
        selectedDayLabel() {
            if (!this.selectedDay) return '';
            const [y, m, d] = this.selectedDay.dateStr.split('-');
            const date = new Date(parseInt(y), parseInt(m) - 1, parseInt(d));
            return date.toLocaleDateString('pt-BR', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' });
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
        loadMonthData() {
            const y = this.currentYear;
            const m = this.currentMonth;

            const moodsPromise = fetch(`/api/moods/user/${this.userId}?year=${y}&month=${m}`, {
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(data => {
                const map = {};
                data.forEach(entry => {
                    const dateStr = entry.createdAt.substring(0, 10);
                    if (!map[dateStr] && entry.moods && entry.moods.length > 0) {
                        map[dateStr] = entry.moods[0];
                    }
                });
                this.moodsByDate = map;
            });

            const eventsPromise = fetch(`/api/events/user/${this.userId}?year=${y}&month=${m}`, {
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(data => {
                const map = {};
                data.forEach(event => {
                    const dateStr = event.eventDate;
                    if (!map[dateStr]) map[dateStr] = [];
                    map[dateStr].push(event);
                });
                this.eventsByDate = map;
            });

            const streakPromise = fetch(`/api/activities/user/${this.userId}/completed-dates?year=${y}&month=${m}`, {
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(dates => { this.streakDates = dates; });

            Promise.all([moodsPromise, eventsPromise, streakPromise]).catch(err => {
                this.errorMessage = err.message;
            });
        },
        prevMonth() {
            if (this.currentMonth === 1) {
                this.currentMonth = 12;
                this.currentYear--;
            } else {
                this.currentMonth--;
            }
            this.selectedDay = null;
            this.moodsByDate = {};
            this.eventsByDate = {};
            this.streakDates = [];
            this.loadMonthData();
        },
        nextMonth() {
            if (this.currentMonth === 12) {
                this.currentMonth = 1;
                this.currentYear++;
            } else {
                this.currentMonth++;
            }
            this.selectedDay = null;
            this.moodsByDate = {};
            this.eventsByDate = {};
            this.streakDates = [];
            this.loadMonthData();
        },
        selectDay(cell) {
            this.selectedDay = cell;
            this.newEventTitle = '';
            this.newEventTime = '';
        },
        addEvent() {
            if (!this.selectedDay || !this.newEventTitle.trim()) return;
            const payload = {
                title: this.newEventTitle.trim(),
                eventDate: this.selectedDay.dateStr,
                eventTime: this.newEventTime || null,
                userId: parseInt(this.userId)
            };
            fetch('/api/events', {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify(payload)
            })
            .then(res => this.handleFetch(res))
            .then(res => res.json())
            .then(created => {
                const dateStr = created.eventDate;
                if (!this.eventsByDate[dateStr]) {
                    this.$set(this.eventsByDate, dateStr, []);
                }
                this.eventsByDate[dateStr].push(created);
                // Refresh selected day to reflect new event
                const updatedCell = this.calendarDays.find(c => c.dateStr === dateStr);
                if (updatedCell) this.selectedDay = updatedCell;
                this.newEventTitle = '';
                this.newEventTime = '';
                bootstrap.Modal.getOrCreateInstance(document.getElementById('addEventModal')).hide();
            })
            .catch(err => { this.errorMessage = err.message; });
        },
        deleteEvent(id) {
            if (!confirm('Deseja excluir este compromisso?')) return;
            fetch(`/api/events/${id}`, {
                method: 'DELETE',
                headers: this.getAuthHeaders()
            })
            .then(res => this.handleFetch(res))
            .then(() => {
                for (const dateStr in this.eventsByDate) {
                    this.eventsByDate[dateStr] = this.eventsByDate[dateStr].filter(e => e.id !== id);
                }
                const updatedCell = this.calendarDays.find(c => c.dateStr === this.selectedDay.dateStr);
                if (updatedCell) this.selectedDay = updatedCell;
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
