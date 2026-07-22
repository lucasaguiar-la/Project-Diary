new Vue({
    el: '#app',
    data: {
        groups: [],
        userId: null,
        newGroupName: '',
        joinCode: '',
        errorMessage: ''
    },
    created() {
        this.userId = localStorage.getItem('userId');
        if (!this.userId) {
            window.location.href = 'login.html';
            return;
        }
        this.loadGroups();
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
        loadGroups() {
            fetch(`/api/groups/user/${this.userId}`, { headers: this.getAuthHeaders() })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(data => { this.groups = data; })
                .catch(err => { this.errorMessage = err.message; });
        },
        createGroup() {
            if (!this.newGroupName.trim()) return;
            fetch('/api/groups', {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify({ name: this.newGroupName.trim(), userId: parseInt(this.userId) })
            })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(created => {
                    this.groups.push(created);
                    this.newGroupName = '';
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('createGroupModal')).hide();
                })
                .catch(err => { this.errorMessage = err.message; });
        },
        joinGroup() {
            if (!this.joinCode.trim()) return;
            fetch('/api/groups/join', {
                method: 'POST',
                headers: this.getAuthHeaders(),
                body: JSON.stringify({ inviteCode: this.joinCode.trim(), userId: parseInt(this.userId) })
            })
                .then(res => this.handleFetch(res))
                .then(res => res.json())
                .then(joined => {
                    if (!this.groups.some(g => g.id === joined.id)) {
                        this.groups.push(joined);
                    }
                    this.joinCode = '';
                    bootstrap.Modal.getOrCreateInstance(document.getElementById('joinGroupModal')).hide();
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
