import axios from "axios";
import { axiosAPI, myAxios } from "./Auth";

export const loadUserByAuth = async () => {
    try {
        // Call a simple "Who am I" endpoint in the Gateway
        const response = await axiosAPI.get('/api/v1/auth/me');
        return response.data;
    } catch (err) {
        throw err;
    }
};

export const signIn = async (credentials) => {
    try {
        // Call a simple "Who am I" endpoint in the Gateway
        const response = await myAxios.post('/api/v1/auth/login', credentials);
        return response.data;
    } catch (err) {
        throw err;
    }
};

export const signup = async (formData) =>{
    try{
        const reps = await myAxios.post('/api/v1/auth/register', formData);
        return reps.data;
    }catch(err){
        throw err;
    }
}