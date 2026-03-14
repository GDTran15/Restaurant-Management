import { RiDeleteBin5Fill } from "react-icons/ri";
import Button from "./Button";
import { FaEdit } from "react-icons/fa";

export default function MenuCard({menuName,menuId,menuDescription, isAvailable, numberOfFoods}){
    return(<>
        <div className="bg-white py-10 px-10 rounded-xl border-gray-300 border flex justify-center flex-col items-center gap-3 relative">
           
            {isAvailable === true?  <span className="absolute top-3 right-3 bg-green-400">Available</span> 
            :  <span className="absolute top-3 right-3 bg-red-400 py-1 px-2 rounded-2xl text-sm">Unavailable</span> }
            <div>
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
                 <div className="flex gap-2">
                    <Button variant={"warning"} ><FaEdit size={25}/></Button>    
                     <Button variant={"danger"} className={"text-white"}> <RiDeleteBin5Fill size={25}/></Button>
                 </div>
            </div>
             
        </div>

    </>)
}