import { createContext, useContext, useEffect, useState } from "react";
import api from "../../api";


const AuthContext = createContext(null);

export const useAuth = () => {
  const authContext = useContext(AuthContext);

  if (!authContext) {
    throw new Error("useAuth must be used within AuthProvider");
  }

  return authContext;
};

export const AuthProvider = ({ children }) => {
  const [token, setToken] = useState(null);
  const [isLoading, setIsLoading] = useState(true);


 

  useEffect(() => {
    
  const refreshAccessToken = async () => {
   try {
      const response = await api.post(`/refresh-token`);
      console.
      setToken(response.data.accessToken)
   } catch  {
    setToken(null);
   }
  };

  refreshAccessToken();
  },[])

   

  useEffect(() => {
    const authInterceptor = api.interceptors.request.use(
      (config) => {
        config.headers.Authorization = !config._retry && token &&   !config.url.includes("/refresh-token") ? `Bearer ${token}` : config.headers.Authorization;
        return config;
      },
      (error) => Promise.reject(error)
    );

   

    return () => {
      api.interceptors.request.eject(authInterceptor);
      
    };
  }, [token]);

  useEffect(() => {
     const refreshInterceptor = api.interceptors.response.use(
      (response) => response,
      async (error) => {
        const originalRequest = error.config;
        if (
             error.response?.status === 403 
            
                ){
                    try {
                        const response = await api.post(`/refresh-token`);
                        setToken(response.data.accessToken);
                        originalRequest.headers.Authorization = `Bearer ${response.data.accessToken}`;
                        originalRequest._retry = true;
                        return api(originalRequest)
                    } catch {
                    
                        setToken(null)
                    }
                }
                return Promise.reject(error);

      });
    return () => {
        api.interceptors.response.eject(refreshInterceptor);
    };
  },[])


  const value = {
    token,
    setToken,
    isAuthenticated: !!token,
    isLoading,
  };





  return <AuthContext.Provider value={value}>
    {children}
    </AuthContext.Provider>;
};