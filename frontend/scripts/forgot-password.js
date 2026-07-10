new Vue({
    el: '#app',
    data: {
        email: '',
        successMessage: '',
        errorMessage: ''
    },
    methods: {
        handleForgotPassword() {
            this.successMessage = '';
            this.errorMessage = '';
            fetch('/api/users/forgot-password', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: this.email })
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Falha ao solicitar redefinição.');
                }
                return response.json();
            })
            .then(data => {
                this.successMessage = data.message;
            })
            .catch(() => {
                this.errorMessage = 'Não foi possível processar sua solicitação. Tente novamente.';
            });
        }
    }
});
