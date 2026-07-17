import React, { useContext, useEffect} from 'react'
import { useNavigate } from 'react-router-dom';
import { loadUserByAuth } from '../Services/User-service';
import UserContext from '../context/UserContext';

const OAuth2Callback = () => {
    const navigate = useNavigate();
    const { setUser } = useContext(UserContext);


    useEffect(() => {
        const checkSession = async () => {
            try {
                // Call a simple "Who am I" endpoint in the Gateway
                const userData = await loadUserByAuth();
                setUser({ data: userData, login: true, loading: false });

                if (userData.login) {
                    navigate("/dashboard");
                }
            } catch (err) {
                console.error("Failed to load user! from Oauth2 callback !!", err);
                window.location.href = '/login';
                setUser({ userData: null, login: false, loading: true });
            }
        };
        checkSession();
    }, []);
    return (
        <div className='mt-4 d-flex justify-content-center'><h3>Loading Session...</h3></div>
    )
}

export default OAuth2Callback;