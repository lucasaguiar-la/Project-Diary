new Vue({
    el: '#app',
    data: {
        userId: null,
        groupId: null,
        listId: null,
        items: [],
        newItemTitle: '',
        errorMessage: ''
    },
    computed: {
        historyUrl() {
            return `shared-list-history.html?listId=${this.listId}`;
        },
        backUrl() {
            return this.groupId ? `group.html?groupId=${this.groupId}` : 'groups.html';
        }
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        const params = new URLSearchParams(window.location.search);
        this.groupId = params.get('groupId');
        this.listId = params.get('listId');
        if (!this.listId) {
            window.location.href = 'groups.html';
            return;
        }
        this.loadItems();
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
        loadItems() {
            fetch(`/api/shared-lists/${this.listId}/items?userId=${this.userId}`, { headers: this.getAuthHeaders() })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => { this.items = data; })
                .catch(err => { this.errorMessage = err.message; });
        },
        addItem() {
            if (!this.newItemTitle.trim()) return;
            fetch('/api/shared-lists/items', {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify({
                    title: this.newItemTitle.trim(),
                    listId: parseInt(this.listId),
                    userId: parseInt(this.userId)
                })
            })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(created => {
                    this.items.push(created);
                    this.newItemTitle = '';
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('addItemModal')).hide();
                })
                .catch(err => { this.errorMessage = err.message; });
        },
        toggleItem(item) {
            if (item.completedToday) {
                fetch(`/api/shared-lists/items/${item.id}/complete/today?userId=${this.userId}`, {
                    method: 'DELETE',
                    headers: this.getAuthHeaders()
                })
                    .then(res => this.handleFetch(res))
                    .then(() => { item.completedToday = false; })
                    .catch(err => { this.errorMessage = err.message; });
            } else {
                fetch(`/api/shared-lists/items/${item.id}/complete?userId=${this.userId}`, {
                    method: 'POST',
                    headers: this.getAuthHeaders()
                })
                    .then(res => this.handleFetch(res))
                    .then(() => { item.completedToday = true; })
                    .catch(err => { this.errorMessage = err.message; });
            }
        },
        deleteItem(id) {
            if (!confirm('Deseja excluir este item? O histórico dele também será removido.')) return;
            fetch(`/api/shared-lists/items/${id}?userId=${this.userId}`, {
                method: 'DELETE',
                headers: this.getAuthHeaders()
            })
                .then(res => this.handleFetch(res))
                .then(() => { this.items = this.items.filter(i => i.id !== id); })
                .catch(err => { this.errorMessage = err.message; });
        }
    }
});

document.getElementById('logout-button').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
});
