import { IoIosAdd } from "react-icons/io";
import Button from "../../../component/Button";
import FormWrapper from "../../../component/FormWrapper";
import InputField from "../../../component/InputField";
import SelectInput from "../../../component/SelectInput";
import UploadImage from "../../../component/UploadImage";
import Switch from "../../../component/Switch";
import { useEffect, useState } from "react";
import axios from "axios";

export default function AddFoodPage() {
    const [foodName,setFoodName] = useState("");
    const [categoryId, setCategoryId] = useState("");
    const [price, setPrice] = useState("");
    const [quantity, setQuantity] = useState("");
    const [description, setDescription] = useState("");
    const [imageUrl, setImageUrl] = useState("");
    const [isAvailable, setIsAvailable] = useState(false);
    const [foodCategories,setFoodCategories] = useState([]);
   

    const handleAddFood = async () => {
        
        try {
            const response = await axios.post(`http://localhost:8080/foods`,{
                foodName,
                isAvailable,
                description,
                quantity,
                foodImageUrl : imageUrl,
                price,
                foodCategoryId: categoryId
            })
            console.log(response.data);
            setFoodName("");
           
            setPrice("");
            setQuantity("");
            setDescription("");
            setImageUrl("");
            setIsAvailable(false);
        } catch (error) {
            console.log(error);
        }
    }

        const fetchFoodCategory = async () =>{
        try {
            const response = await axios.get(`http://localhost:8080/food-categories`);
            
            setFoodCategories(response.data)
            
        } catch (error) {
            console.log(error);
        }
    }
        useEffect(() => {
            fetchFoodCategory();
        }, []);


    return (
        <>
          

                 <div>
                     <FormWrapper width ={"full"} title={"Add Food"} submitFuntion={handleAddFood}>
                           
                         <div className=" grid grid-cols-2 gap-2 mb-2">
                                    <InputField  label={"Food Name"} value={foodName} setValue={setFoodName}/>
                                    <SelectInput label={"Category"} value={categoryId} setValue={setCategoryId} itemList={foodCategories} itemName={"foodCategoryName"} itemValue={"foodCategoryId"} />
                                    <InputField inputType={"number"} step={0.01} label={"Price"} value={price} setValue={setPrice}/>
                                    <InputField inputType={"number"}  label={"Quantity"} value={quantity} setValue={setQuantity}/>
                                    
                                                                
                            
                                <div className=" col-span-2">
                                    <InputField  label={"Description"} value={description} setValue={setDescription}/>
                                </div>
                                
                                    <UploadImage label={"Food Image"} value={imageUrl} setValue={setImageUrl}/>
                                    <div className="flex justify-start items-start col-span-2 md:col-span-1 md:justify-end" >
                                    <Switch value={isAvailable} setValue={setIsAvailable} />
                                    </div>
                                
                            </div >

                            
                                <Button  variant={"navy"} width={"w-35"} type={"submit"}>
                                    
                                    <div className="flex items-center gap-2 ">
                                    <IoIosAdd size={24} />
                                    <span>Add Food</span>
                                    </div>
                                </Button>
                                
                            </FormWrapper>
                            </div>
                      
        </>
    );
}
