new Vue({
    el: '#app',
    data: {
        email: '',
        password: '',
        errorMessage: ''
    },
    methods: {
        handleLogin() {
            this.errorMessage = '';
            const loginData = {
                email: this.email,
                password: this.password
            };

            fetch('http://localhost:8081/api/auth/signin', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginData)
            })
            .then(response => {
                if(!response.ok) {
                    throw new Error('Credenciais inválidas.');
                }
                return response.json();
            })
            .then(data => {
                localStorage.setItem('jwtToken', data.token);
                window.location.href = 'dashboard.html';
            })
            .catch(error => {
                this.errorMessage = 'Falha no login. Verifique seu e-mail e senha.';
                console.error('Erro no login:', error);
            });
        }
    }
});