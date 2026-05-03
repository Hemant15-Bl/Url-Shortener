import axios from "axios";

export const axiosAPI = axios.create({
    baseURL:"http://localhost:9096",
    withCredentials: true
});

export const myAxios = axios.create({
    baseURL:"http://localhost:9096",
    withCredentials: true
});

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
