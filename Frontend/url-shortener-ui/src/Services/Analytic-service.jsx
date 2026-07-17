import { axiosAPI } from "./Auth";

export const loadLinkStats = async (shortCode) => {
    try {
        const resp = await axiosAPI.get(`/api/v3/analytics/${shortCode}`);
        return resp.data;
    } catch (err) {
        throw err;
    }
};

export const loadLinkHistory = async (pageToFetch, shortCode) => {
    try {
        const resp = await axiosAPI.get(`/api/v3/analytics/${shortCode}/history?page=${pageToFetch}&size=5`);
        return resp.data;
    } catch (err) {
        throw err;
    }
};