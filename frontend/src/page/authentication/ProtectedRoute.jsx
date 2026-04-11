import { Navigate, Outlet } from "react-router-dom";
import { useAuth } from "./AuthProvider";



export default function ProtectedRoute(){
    
   const { isLoading, isAuthenticated } = useAuth();

  if (isLoading) {
    return <div>Loading...</div>;
  }

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}