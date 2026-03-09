import { useEffect, useState } from "react";
import SearchBar from "../../../component/SearchBar";
import axios from "axios";
import FoodCard from "../../../component/FoodCard";

export default function ViewFoodPage(){
    const [search,setSearch] = useState("");
    const [page,setPage] = useState(0);  
      
    const [foodList,setFoodList] = useState([]);
  
    const searchForFood = async (search) => {
        try {
            const response = await axios.get(`http://localhost:8080/foods`,{
                params: {page,size : 9,search}
            })
            console.log(response);
            setFoodList(response.data.content)
        } catch (error) {
            console.log(error)
        }
    }
    const deleteFood = async (foodId) => {
        try {
            const response = await axios.delete(`http://localhost:8080/foods/${foodId}`)
            console.log(response);
            searchForFood(search);
        } catch (error) {
            console.log(error)
        }
    }

    useEffect(() => {
        searchForFood();
    },[])
        
    return (<>
        
        <div className=" bg-white py-10 px-6 rounded-xl border-gray-300 border ">
                <SearchBar placeholder={"Search for food"} value={search} setValue={setSearch} handleSearch={searchForFood}/>
        </div>

        <div className=" mt-5">
             <div className=" grid grid-cols-1 md:grid-cols-2 gap-5 mb-2 ">
                {foodList.map(food =>(
                   
                    <FoodCard foodId={food.foodId} 
                    foodName={food.foodName}
                     foodCategoryName={food.foodCategoryName}
                     foodImageUrl={food.foodImageUrl}
                     isAvailable={food.isAvailable}
                     quantity={food.quantity}
                     price={food.price}
                     description={food.description}
                     deleteFood={deleteFood}
                     />
                   
                ))}
            
            
        </div>
        </div>
        
       

    </>)
}