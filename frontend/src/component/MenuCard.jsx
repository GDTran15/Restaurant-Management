import { RiDeleteBin5Fill } from "react-icons/ri";
import Button from "./Button";
import { FaEdit } from "react-icons/fa";
import { useNavigate } from "react-router-dom";
import axios from "axios";

export default function MenuCard({menuName,menuId,menuDescription, isAvailable, numberOfFoods, handleGetMenu}){
    const navigate = useNavigate();

    const handleActivateMenu = async () => {
        try {
            await axios.patch(`http://localhost:8080/menus/${menuId}/activate`);
            await handleGetMenu();
        } catch (error) {
            console.log(error);
        }
    }
    const handleDeactivateMenu = async () => {
        try {
            await axios.patch(`http://localhost:8080/menus/${menuId}/deactivate`);
            await handleGetMenu();
        } catch (error) {
            console.log(error.response);
        }
    }

    return(<>
        <div className="bg-white py-10 px-10 rounded-xl border-gray-300 border flex justify-center flex-col items-center gap-3 relative">
           
            {isAvailable === true?  <span className="absolute top-3 right-3 bg-green-400  py-1 px-2 rounded-2xl text-sm">Available</span> 
            :  <span className="absolute top-3 right-3 bg-red-400 py-1 px-2 rounded-2xl text-sm">Unavailable</span> }
            <div className="flex flex-col justify-center items-center">
                <h4>{menuName} menu</h4>
            <p>{menuDescription}</p>
            </div>
            
            <div className=" bg-input-bg w-full py-3 border-gray-300 border rounded-2xl flex  justify-center">
                {numberOfFoods} menu items
            </div>
            <div className="flex justify-between w-full"> 
                <Button className={"food-edit-button font-semibold"} onClick={() => {
                isAvailable ? handleDeactivateMenu() : handleActivateMenu()

                }}>
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