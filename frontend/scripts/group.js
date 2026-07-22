new Vue({
    el: '#app',
    data: {
        userId: null,
        groupId: null,
        group: null,
        members: [],
        lists: [],
        newListName: '',
        copied: false,
        errorMessage: ''
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        const params = new URLSearchParams(window.location.search);
        this.groupId = params.get('groupId');
        if (!this.groupId) {
            window.location.href = 'groups.html';
            return;
        }
        this.loadGroup();
        this.loadMembers();
        this.loadLists();
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
        loadGroup() {
            fetch(`/api/groups/${this.groupId}?userId=${this.userId}`, { headers: this.getAuthHeaders() })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => { this.group = data; })
                .catch(err => { this.errorMessage = err.message; });
        },
        loadMembers() {
            fetch(`/api/groups/${this.groupId}/members?userId=${this.userId}`, { headers: this.getAuthHeaders() })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => { this.members = data; })
                .catch(err => { this.errorMessage = err.message; });
        },
        loadLists() {
            fetch(`/api/shared-lists/group/${this.groupId}?userId=${this.userId}`, { headers: this.getAuthHeaders() })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => { this.lists = data; })
                .catch(err => { this.errorMessage = err.message; });
        },
        createList() {
            if (!this.newListName.trim()) return;
            fetch('/api/shared-lists', {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify({
                    name: this.newListName.trim(),
                    groupId: parseInt(this.groupId),
                    userId: parseInt(this.userId)
                })
            })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(created => {
                    this.lists.push(created);
                    this.newListName = '';
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('addListModal')).hide();
                })
                .catch(err => { this.errorMessage = err.message; });
        },
        copyInvite() {
            if (!this.group) return;
            navigator.clipboard.writeText(this.group.inviteCode)
                .then(() => {
                    this.copied = true;
                    setTimeout(() => { this.copied = false; }, 2000);
                })
                .catch(() => { this.errorMessage = 'Não foi possível copiar o código.'; });
        },
        listUrl(list) {
            return `shared-list.html?groupId=${this.groupId}&listId=${list.id}`;
        }
    }
});

document.getElementById('logout-button').addEventListener('click', () => {
    localStorage.removeItem('jwtToken');
    localStorage.removeItem('userId');
    window.location.href = 'login.html';
});
