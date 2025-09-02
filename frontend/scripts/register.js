new Vue({
    el: '#app',
    data: {
        firstName: '',
        lastName: '',
        email: '',
        password: '',
        confirmPassword: '',
        errorMessage: ''
    },
    methods: {
        handleRegister() {
            this.errorMessage = '';

            if(this.password !== this.confirmPassword) {
                this.errorMessage = 'As senhas devem ser iguais.';
                return;
            }

            const signupData = {
                firstName: this.firstName,
                lastName: this.lastName,
                email: this.email,
                password: this.password
            };

            fetch('http://localhost:8081/api/users/register', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(signupData)
            })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Falha no registro. O e-mail pode já estar em uso.');
                }
                return response.json();
            })
            .then(data => {
                alert('Registro bem-sucedido! Você será redirecionado para a página de login.');
                window.location.href = 'login.html';
            })
            .catch(error => {
                this.errorMessage = error.message;
                console.error('Error no registro:', error);
            });
        }
    }
});