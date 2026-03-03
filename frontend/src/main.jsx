import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'

import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import LoginPage from './page/LoginPage.jsx'
import AdminDashBoardPage from './page/AdminDashboardPage.jsx'
import AdminHomePage from './page/AdminHomePage.jsx'

const router = createBrowserRouter([
  { path:"/login", element: <LoginPage/>},
  { path:"/admin/home", element: <AdminHomePage/>},
  { path:"/login", element: <LoginPage/>},
  
])

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RouterProvider router={router}/>
  </StrictMode>,
)
