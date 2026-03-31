import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'

import { createBrowserRouter, RouterProvider } from 'react-router-dom'
import LoginPage from './page/LoginPage.jsx'

import AdminHomePage from './page/admin/AdminHomePage.jsx'
import AdminDashboardPage from './page/admin/AdminDashboardPage.jsx'
import FoodManagementPage from './page/admin/food_management/FoodManagementPage.jsx'
import MenuManagementPage from './page/admin/menu/MenuManagementPage.jsx'
import StaffManagementPage from './page/admin/staff/StaffManagementPage.jsx'
import TableManagementPage from './page/admin/table/TableManagementPage.jsx'
import MenuDetailPage from './page/admin/menu/MenuDetailPage.jsx'
import CustomerMenuPage from './page/customer/CustomerMenuPage.jsx'
import CustomerOrderPage from './page/customer/CustomerOrderPage.jsx'
import OrderManagementPage from './page/admin/order/OrderManagementPage.jsx'

const router = createBrowserRouter([
  { path: "/login", element: <LoginPage /> },
  {
    path: "/admin",
    element: <AdminHomePage />,
    children: [
      { index: true, element: <AdminDashboardPage /> },
      { path: "foods", element: <FoodManagementPage /> },
      { path: "menus", element: <MenuManagementPage /> },
      { path: "menus/:menuId", element: <MenuDetailPage /> },
      { path: "staffs", element: <StaffManagementPage /> },
      { path: "tables", element: <TableManagementPage /> },
      { path: "orders", element: <OrderManagementPage/>}
    ],
  },
  {
    path: "/customer/menu/:token", element: <CustomerMenuPage/>
    
  }, {
    path: "/customer/order/:token", element: <CustomerOrderPage/>
    
  }
]);

createRoot(document.getElementById('root')).render(
  <StrictMode>
    <RouterProvider router={router}/>
  </StrictMode>,
)
