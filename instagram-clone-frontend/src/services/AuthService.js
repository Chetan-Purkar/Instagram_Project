// services/AuthService.js
import { login as loginApi, signup as signupApi } from '../api/AuthApi';

export const AuthService = {
    login: loginApi,
    signup: signupApi,
    logout: () => {
        // Optional: call server to clear cookie/session if needed
        localStorage.removeItem('token');
        localStorage.removeItem('username');
    }
};
