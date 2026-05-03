import { axiosAPI } from "./Auth";

export const getAllLinkes = async () => {
    try {
        const resp = await axiosAPI.get('/api/v2/url/all');
        return resp.data;
    } catch (err) {
        throw err;
    }
};

export const createShortenerLink = async (url) => {
    try {
        const resp = await axiosAPI.post('http://localhost:9096/api/v2/url/shorten', url, {
        headers: { 'Content-Type': 'text/plain' }
      });
        return resp.data;
    } catch (err) {
        throw err;
    }
};

export const removeShortLink = async (shortCode) => {
    try {
        await axiosAPI.delete(`http://localhost:9096/api/v2/url/remove/${shortCode}`);
    } catch (err) {
        throw err;
    }
};