import SideBarIcon from "../component/SideBarIcon";
import { FaHome, FaUser } from "react-icons/fa";
import { MdOutlineRestaurant, MdTableRestaurant } from "react-icons/md";
import { IoFastFoodSharp } from "react-icons/io5";
import { CiLogout } from "react-icons/ci";

export default function NavBar({ username }) {
  return (
    <nav className="bg-main-navy fixed top-0 left-0 h-screen w-sidebar m-0 flex flex-col justify-between text-second-cream shadow py-8">
      <div className="text-center">
        <h1 className="mb-3 text-2xl">PiggyOinkOink</h1>
        <h3>Manager Page</h3>
      </div>

      <div className="flex flex-col gap-3">
        <SideBarIcon to="/admin" icon={<FaHome size={28} />} text="Dashboard" />
        <SideBarIcon to="/admin/staffs" icon={<FaUser size={28} />} text="Staff" />
        <SideBarIcon to="/admin/tables" icon={<MdTableRestaurant size={28} />} text="Table" />
        <SideBarIcon to="/admin/menus" icon={<MdOutlineRestaurant size={28} />} text="Menu" />
        <SideBarIcon to="/admin/foods" icon={<IoFastFoodSharp size={28} />} text="Food" />
      </div>

      <div>
        <SideBarIcon to="/login" icon={<CiLogout size={28} />} text="Logout" />
      </div>
    </nav>
  );
}