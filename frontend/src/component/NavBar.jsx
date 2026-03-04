import { FaFire, FaHome, FaUser } from "react-icons/fa";
import SideBarIcon from "../component/SideBarIcon";
import { BsPlus } from "react-icons/bs";
import { MdOutlineRestaurant, MdTableRestaurant } from "react-icons/md";
import { IoFastFoodSharp } from "react-icons/io5";
import { CiLogout } from "react-icons/ci";

export default function NavBar({currentPage,setCurrentPage,username}){
    
    return (
        <>
         <nav className=" bg-main-navy fixed top-0 left-0 h-screen 
         w-sidebar m-0 flex flex-col justify-between text-second-cream shadow py-8 ">
            <div className="text-center ">
                <h1 className="mb-3 text-2xl">PiggyOinkOink</h1>
                <h3>Manager Page</h3>
                
            </div>
            

            <div className="flex flex-col gap-3">
            <SideBarIcon pageKey="dashboard" icon={<FaHome size="28"/>} currentPage={currentPage} text={"Dashboard"} onClick={() => setCurrentPage("dashboard")}/>
            <SideBarIcon pageKey="staffManagement" icon={<FaUser size="28"/>} currentPage={currentPage} text={"Staff"} onClick={() => setCurrentPage("staffManagement")}/>
            <SideBarIcon pageKey="tableManagement" icon={<MdTableRestaurant size="28"/>} currentPage={currentPage} text={"Table"} onClick={() => setCurrentPage("tableManagement")}/>
            <SideBarIcon pageKey="menuManagement" icon={<MdOutlineRestaurant size="28"/>} currentPage={currentPage} text={"Menu"} onClick={() => setCurrentPage("menuManagement")}/>
            <SideBarIcon pageKey="foodManagement" icon={<IoFastFoodSharp  size="28"/>} currentPage={currentPage} text={"Food"} onClick={() => setCurrentPage("foodManagement")}/>
            </div>
            
            <div>
                {/* <h2 className="text-center">username</h2> */}
                <SideBarIcon pageKey={"logout"} icon={<CiLogout size={28} />} text={"Logout"}/>
            </div>
        </nav>
        </>
    )
}