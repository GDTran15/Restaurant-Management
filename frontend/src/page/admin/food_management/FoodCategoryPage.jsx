import { IoIosAdd } from "react-icons/io";
import Button from "../../../component/Button";
import FormWrapper from "../../../component/FormWrapper";
import InputField from "../../../component/InputField";
import { useEffect, useState } from "react";
import { FaEdit } from "react-icons/fa";
import { RiDeleteBin5Fill } from "react-icons/ri";

import api from "../../../api";

export default function FoodCategoryPage(){
    const[foodCategoryName,setFoodCategoryName] = useState("");
    const[foodCategories,setFoodCategory] = useState([]);
    const[updateVerse,setUpdateVerse] = useState(false);
    const[updateCategoryId,setUpdateCategoryId] = useState(0);

    const fetchFoodCategory = async () =>{
        try {
            const response = await api.get(`/food-categories/food-count`);
            console.log(response.data);
            setFoodCategory(response.data);
        } catch (error) {
            console.log(error);
        }
    }
        useEffect(() => {
            fetchFoodCategory();
        }, []);


    const createNewCategory = async () => {
            try {
            const response = await api.post(
                `/food-categories`,
                null, // no request body
                {
                    params: {
                        foodCategoryName: foodCategoryName // sent as query param
                    }
                }
            );
            setFoodCategoryName("");
            fetchFoodCategory();
            
            } catch (error) {
            console.error("Error creating category:", error);
        }
        };
        
    const updateCategoryName = async () => {
            try {
            const response = await api.put(
                `/food-categories/${updateCategoryId}`,
                null, // no request body
                {
                    params: {
                        foodCategoryName: foodCategoryName // sent as query param
                    }
                }
            );
            setFoodCategoryName("");
            fetchFoodCategory();
            setUpdateVerse(false);
            
            } catch (error) {
            console.error("Error creating category:", error);
        }
        };

    const deleteFoodCategory = async (foodCategoryId) => {
            try {
            const response = await api.delete(
                `/food-categories/${foodCategoryId}`
            );
            fetchFoodCategory();
            console.log(response);
            
            } catch (error) {
                alert(error.response.data)  // fix this later make better ui
                
        }
        };

    
       
    


    return (<>
            {updateVerse  ?  <FormWrapper width ={"full"} title={"Update Category"} submitFuntion={updateCategoryName} >
                    <InputField label={"Food Category Name"} value={foodCategoryName} setValue={setFoodCategoryName}/>
                    <div className="flex items-center gap-2">
                    <Button  variant={"navy"} width={"w-50"} type={"submit"}>
                                                    
                    <div className="flex items-center gap-2 ">
                       <IoIosAdd size={24} />
                        <span>Update Category</span>
                        </div>
                     </Button>
                    <Button  variant={"secondary"} width={"w-45"} type={"submit"} className={"border-gray-400"} 
                    onClick={() => {setUpdateVerse(false)
                        setFoodCategoryName("");
                    }}
                    >
                        Cancel
                     </Button>
                     </div>
                    </FormWrapper> : <FormWrapper width ={"full"} title={"Add Category"} submitFuntion={createNewCategory} >
                    <InputField label={"Food Category Name"} value={foodCategoryName} setValue={setFoodCategoryName}/>

                    <Button  variant={"navy"} width={"w-45"} type={"submit"}>
                                                    
                    <div className="flex items-center gap-2 ">
                       <IoIosAdd size={24} />
                        <span>Add Category</span>
                        </div>
                     </Button>
                    </FormWrapper>}

                  

                   <div className="mt-5 bg-white py-10 px-6 rounded-xl border-gray-300 border">
                    <table className="table-fixed w-full border border-gray-300">
                        <thead>
                        <tr className="border-b border-gray-300">
                            <th className="border px-4 py-2 w-1/2">Category</th>
                            <th className="border px-4 py-2 w-1/4">Number of food from this category</th>
                            <th className="border px-4 py-2 w-1/4">Action</th>
                        </tr>
                        </thead>
                        <tbody>
                            {foodCategories.map(foodCategory => (
                                    <tr className="border-b border-gray-300 text-center">
                                    <td className="border px-4 py-2 w-1/2">{foodCategory.foodCategoryName}</td>
                                    <td className="border px-4 py-2 w-1/4">{foodCategory.numberOfFoodBelongToThisCategory}</td>
                                    <td className="border px-4 py-2 w-1/4 ">
                                    <div className="flex items-center justify-center gap-2">
                                    <Button variant={"warning"}
                                        onClick={() => {
                                            setUpdateVerse(true);
                                            setUpdateCategoryId(foodCategory.foodCategoryId);
                                            setFoodCategoryName(foodCategory.foodCategoryName);
                                        }}
                                    ><FaEdit size={25}/></Button>    
                                    <Button variant={"danger"} className={"text-white"}
                                        onClick={() => deleteFoodCategory(foodCategory.foodCategoryId)}
                                    >
                                        <RiDeleteBin5Fill size={25}/>
                                    </Button>
                                    </div>
                                    
                                    </td>
                                </tr>
                            ))}
                        
                        </tbody>
                    </table>
                    </div>

    </>)
}