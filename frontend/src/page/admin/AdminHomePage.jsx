
import { use, useState } from "react";
import NavBar from "../../component/NavBar";
import FoodManagementPage from "./FoodManagementPage";
import MenuManagementPage from "./MenuManagementPage";
import StaffManagementPage from "./StaffManagementPage";
import TableManagementPage from "./table/TableManagementPage";
import AdminDashboardPage from "./AdminDashboardPage";

export default function AdminHomePage(){
    const adminName = localStorage.getItem("username");
    
    let mainContent;
    const [currentPage,setCurrentPage] = useState("dashboard")


    switch(currentPage){
        case "dashboard":
            mainContent = <AdminDashboardPage/>
            break;
        case "foodManagement":
            mainContent = <FoodManagementPage/>
            break;
        case "menuManagement":
            mainContent = <MenuManagementPage/>
            break;
        case "staffManagement":
            mainContent = <StaffManagementPage/>
            break;
        case "tableManagement":
            mainContent = <TableManagementPage/>
            break;

    }


    return(<>
        <NavBar currentPage={currentPage} username={adminName} setCurrentPage={setCurrentPage}/>
        <main className="ml-sidebar max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
            {mainContent}
        </main>
    </>)
}