import { FaEdit } from "react-icons/fa";
import Button from "./Button";
import { RiDeleteBin5Fill } from "react-icons/ri";
import axios from "axios";

export default function FoodCard({foodId,foodName,isAvailable,description,foodImageUrl,quantity,price,foodCategoryName ,deleteFood,handleUpdateFoodAvailable}){

   

    
    


    return(<>
        <div className="bg-white py-10 px-6 rounded-xl border-gray-300 border ">
            <div className="flex flex-col gap-5">
                <div className="flex-1 flex">
                    
                    <img src={`/${foodImageUrl}`} className="w-32 h-32 object-cover rounded mr-4" alt="" />
                    <div>
                    <h5 className=" text-2xl text-main-navy font-bold">{foodName}</h5>
                    <h5 className=" text-gray-500">Category: {foodCategoryName}</h5>
                    <p className="text-gray-500">{description}</p>
                    </div>
                    
                    <div className="h-10">
                        <span className={`mt-2 px-2 py-1 rounded-xl w-max ${
                        isAvailable ? "bg-green-400" : "bg-red-400"
                    }`}>
                        {isAvailable ? "Available" : "Unavailable"}
                    </span>
                    </div>
                    
                    

                </div>
                <div className="flex-1 flex items-center justify-between">
                    <p className=" text-2xl font-semibold text-gray-600 mt-2">${price}</p>
                    <p className=" text-2xl border mt-2 border-gray-400 px-2 rounded-xl">Qty: {quantity}</p>
                    <div className="flex gap-2 items-center">
                    
                    <Button  className={"food-edit-button"}><FaEdit size={24}/></Button>  
                    <Button className={"food-edit-button font-semibold"} onClick={handleUpdateFoodAvailable}>
                        {isAvailable ? "Disable" 
                    : "Enable" }
                     
                        </Button>    
                    
                     <Button  onClick={() => deleteFood(foodId)}  className={"food-edit-button"}>
                     <RiDeleteBin5Fill size={24}/>
                       </Button>
                    </div>
                    
                </div>
            
            </div>
        </div>
    </>)
}