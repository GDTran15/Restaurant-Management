
import { TbPackage } from "react-icons/tb";
import Button from "./Button";
import { FaArrowLeftLong } from "react-icons/fa6";
import { useNavigate } from "react-router-dom";

export default function CustomerMenuNavbar({page,token}) {
  const navigate = useNavigate();

  return (
    <nav className="bg-main-navy h-20 sticky py-2 border-b border-gray-300 top-0 text-second-cream">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center h-full">
        <div className="text-start">
          <h1 className="text-2xl">PiggyOinkOink</h1>
        </div>


        {page === "menu" ?
         <div>
          <Button onClick={() => navigate(`/customer/order/${token}`)} className={"food-edit-button flex gap-2 font-bold"}>
            <TbPackage size={20} />
            Orders
          </Button>
        </div>
        :
        <Button onClick={() => navigate(`/customer/menu/${token}`)} className={"food-edit-button flex gap-2 font-bold"}>
            <FaArrowLeftLong size={20}  />
            Menu
          </Button>

      }

       
      </div>
    </nav>
  );
}