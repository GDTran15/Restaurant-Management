import { FaFire, FaHome, FaUser } from "react-icons/fa";
import SideBarIcon from "../component/SideBarIcon";
import { BsPlus } from "react-icons/bs";
import { MdOutlineRestaurant, MdTableRestaurant } from "react-icons/md";
import { IoFastFoodSharp } from "react-icons/io5";

export default function NavBar({currentPage,setCurrentPage}){
    
    return (
        <>
         <nav className=" bg-main-navy fixed top-0 left-0 h-screen 
         w-sidebar m-0 flex flex-col text-second-cream shadow py-8 ">
            <h1 className="text-center">PiggyOinkOink Restaurant</h1>

            
            <SideBarIcon pageKey="dashboard" icon={<FaHome size="28"/>} currentPage={currentPage} text={"Dashboard"} onClick={() => setCurrentPage("dashboard")}/>
            <SideBarIcon pageKey="staffManagement" icon={<FaUser size="28"/>} currentPage={currentPage} text={"Staff"} onClick={() => setCurrentPage("staffManagement")}/>
            <SideBarIcon pageKey="tableManagement" icon={<MdTableRestaurant size="28"/>} currentPage={currentPage} text={"Table"} onClick={() => setCurrentPage("tableManagement")}/>
            <SideBarIcon pageKey="menuManagement" icon={<MdOutlineRestaurant size="28"/>} currentPage={currentPage} text={"Menu"} onClick={() => setCurrentPage("menuManagement")}/>
            <SideBarIcon pageKey="foodManagement" icon={<IoFastFoodSharp  size="28"/>} currentPage={currentPage} text={"Food"} onClick={() => setCurrentPage("foodManagement")}/>
            
        </nav>
        </>
    )
}