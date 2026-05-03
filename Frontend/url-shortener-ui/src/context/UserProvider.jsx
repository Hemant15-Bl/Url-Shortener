import React, { useEffect, useState } from 'react'
import UserContext from './UserContext';

const UserProvider = ({children}) => {
    const [user, setUser] = useState({
        data:{},
        login: false,
        loading: true
    });

    useEffect(() =>{
         if(!user.login){
            return;
        }

        loadUserByAuth().then(userData => {
            console.log(userData);

            setUser({ data: userData, login: true, loading: false });

        }).catch(err => {
            console.log("No Active Session Found!");
            // localStorage.removeItem("access_token");
            setUser({ data: null, login: false, loading: true });
            window.location.href = '/login';
        })
    },[])
  return (
    <UserContext.Provider value={{user, setUser}}>
        {children}
    </UserContext.Provider>
  )
}

export default UserProvider;