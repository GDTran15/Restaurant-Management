import { RiDeleteBin5Fill } from "react-icons/ri";
import Button from "./Button";
import { FaEdit } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

export default function MenuCard({menuName,menuId,menuDescription, isAvailable, numberOfFoods}){
    const navigate = useNavigate();

    return(<>
        <div className="bg-white py-10 px-10 rounded-xl border-gray-300 border flex justify-center flex-col items-center gap-3 relative">
           
            {isAvailable === true?  <span className="absolute top-3 right-3 bg-green-400">Available</span> 
            :  <span className="absolute top-3 right-3 bg-red-400 py-1 px-2 rounded-2xl text-sm">Unavailable</span> }
            <div className="flex flex-col justify-center items-center">
                <h4>{menuName} menu</h4>
            <p>{menuDescription} description</p>
            </div>
            
            <div className=" bg-input-bg w-full py-3 border-gray-300 border rounded-2xl flex  justify-center">
                {numberOfFoods} menu items
            </div>
            <div className="flex justify-between w-full"> 
                <Button className={"food-edit-button font-semibold"} >
                                    {isAvailable ? "Disable" 
                                : "Enable" }
                                 
                 </Button>   
                <Button className={"food-edit-button font-semibold"} onClick={() => navigate(`/admin/menus/${menuId}`)} >
                    View Details           
                 </Button>   
                 
            </div>
             
        </div>

    </>)
}