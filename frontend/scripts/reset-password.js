new Vue({
    el: '#app',
    data: {
        token: '',
        newPassword: '',
        confirmPassword: '',
        successMessage: '',
        errorMessage: ''
    },
    created() {
        const params = new URLSearchParams(window.location.search);
        this.token = params.get('token') || '';
        if (!this.token) {
            this.errorMessage = 'Link inválido ou incompleto.';
        }
    },
    methods: {
        handleResetPassword() {
            this.successMessage = '';
            this.errorMessage = '';
            if (this.newPassword !== this.confirmPassword) {
                this.errorMessage = 'As senhas não coincidem.';
                return;
            }
            fetch('/api/users/reset-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ token: this.token, newPassword: this.newPassword })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Falha ao redefinir senha.');
                }
                return response.json();
            })
            .then(data => {
                this.successMessage = data.message + ' Redirecionando para o login...';
                setTimeout(() => { window.location.href = 'login.html'; }, 2000);
            })
            .catch(() => {
                this.errorMessage = 'Não foi possível redefinir a senha. O link pode ter expirado.';
            });
        }
    }
});
