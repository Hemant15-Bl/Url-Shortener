import logo from './logo.svg';
import Login from './Components/Login';
import Home from './Components/Home';
import OAuth2Callback from './auth/OAuth2Callback';
import DashBoard from './Components/DashBoard';
import Signup from './Components/Signup';
import { Toaster } from 'sonner';
import { useEffect } from 'react';
import { createBrowserRouter, RouterProvider } from 'react-router-dom';
import 'bootstrap/dist/css/bootstrap.min.css';


function App() {

  const router = createBrowserRouter([
    { path: "/dashboard", element: <DashBoard /> },
    { path: "/login", element: <Login /> },
    { path: "/", element: <Home /> },
    { path: "/signup", element: <Signup /> },
    { path: "/oauth2/callback", element: <OAuth2Callback /> },

  ]);

  useEffect(()=>{
    document.title = "SwiftLink";
  },[]);
  return (
    <div>
      <RouterProvider router={router} />
      <Toaster />
    </div>
  );
}

export default App;
