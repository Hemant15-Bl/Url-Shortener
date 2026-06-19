import axios from "axios";

export const axiosAPI = axios.create({
    baseURL:"http://localhost:9096",
    withCredentials: true
});

export const myAxios = axios.create({
    baseURL:"http://localhost:9096",
    withCredentials: true
});

// Helper function to read a cookie value by its name
const getCookie = (name) => {
    const value = `; ${document.cookie}`;
    const parts = value.split(`; ${name}=`);
    if (parts.length === 2) return parts.pop().split(';').shift();
    return null;
};

// // ==========================================
// // REQUEST INTERCEPTOR FOR COOKIE AUTH
// // ==========================================
// axiosAPI.interceptors.request.use(
//     (config) => {
//         // 1. Try to read the AUTH_TOKEN directly from your cookies
//         const token = getCookie("AUTH_TOKEN"); 
        
//         // 2. If your backend needs it as a header, inject it here
//         if (token) {
//             config.headers["Authorization"] = `Bearer ${token}`;
//         }
        
//         return config;
//     },
//     (error) => {
//         return Promise.reject(error);
//     }
// );

// ==========================================
// RESPONSE INTERCEPTOR
// ==========================================
axiosAPI.interceptors.response.use(
    (response) => response,
    (error) =>{
        if (error.response && error.response.status === 401) {
            console.error("Session is expired, Redirect to Login page...");
            window.location.href = "/login"
        }

        return Promise.reject(error);
    }
);
