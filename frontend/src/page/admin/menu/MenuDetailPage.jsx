
import { use, useEffect, useState } from "react";
import { FaEdit, FaTrash } from "react-icons/fa";
import { IoAdd } from "react-icons/io5";
import { MdRestaurantMenu } from "react-icons/md";
import { useParams } from "react-router-dom"
import Button from "../../../component/Button";
import { RiDeleteBin5Fill } from "react-icons/ri";
import AddFoodIntoMenu from "./AddFoodIntoMenu";
import api from "../../../api";

export default function MenuDetailPage(){
    const {menuId} = useParams();
    const [menuDetail,setMenuDetail] = useState({});
    const [isAddModalOpen,setIsAddModelOpen] = useState(false);
    const [page,setPage] = useState();
    const [search,setSearch] = useState();
    const [foodList,setFoodList] = useState([]);

     const handleGetMenu = async () => {
      
        try {
            const response = await api.get(`/menus/${menuId}`);
            console.log(response);
            setMenuDetail(response.data);
            
           
        } catch (error) {
            console.log(error.response);
           
        }
    }
     const handleGetMenuFood = async () => {
      
        try {
            const response = await api.get(`/menus/${menuId}/foods`,{
                params: {page,size : 9,search}
            });
            
            setFoodList(response.data.content);
           
        } catch (error) {
            console.log(error.response);
           
        }
    }

     const handleDeleteMenuItem = async (menuId,foodId) => {
       
      try {
        const response = await api.delete(`/menu-items`,{
            params:{
                     menuId : menuId,
                     foodId : foodId
            } 
        })
        handleGetMenuFood();
      } catch (error) {
        console.log(error)
      }
    }

    useEffect(() => {
        handleGetMenu();
        handleGetMenuFood();
    },[])

    return(<>
        <div className="bg-white py-10 px-10 rounded-xl border-gray-300 border flex justify-between">
              <div className="">
                    <div className="flex items-center gap-4 mb-4">
                        <h1 className="text-4xl font-bold text-slate-900">
                            {menuDetail.menuName }
                        </h1>

                        <span
                            className={`px-4 py-1 rounded-full text-sm font-medium ${
                                menuDetail.isAvailable
                                    ? "bg-green-100 text-green-700"
                                    : "bg-red-100 text-red-700"
                            }`}
                        >
                            {menuDetail.isAvailable ? "● Available" : "● Unavailable"}
                        </span>

                    </div>

                    <p className="text-gray-500 text-xl leading-relaxed max-w-4xl">
                        {menuDetail.menuDescription }
                    </p>
                </div>

                <div className="flex items-start gap-3">
                    <Button className={"food-edit-button font-semibold"} >
                   Edit
                  </Button>  
                      <Button  className={"food-edit-button"}>
                       <RiDeleteBin5Fill size={24}/>
                        </Button>
                </div>
            </div>

            <div className="flex justify-between items-center mb-5 mt-10">
                <div>
                    <h5 className="text-2xl font-bold text-slate-900">Menu Items</h5>
                    <p className="text-gray-500 mt-1">
                        You have {foodList.length} items in this menu
                    </p>
                </div>
                
                <div className="flex align-bottom">
                    <Button variant={"navy"} onClick={() => setIsAddModelOpen(true)}  >
                    <IoAdd size={22} />
                    Add New Item
                </Button>
                </div>

                
            </div>

            <div className="bg-white rounded-3xl border border-gray-200 overflow-hidden">
                <div className="grid grid-cols-12 px-8 py-5 border-b border-gray-200 text-gray-500 font-semibold text-sm uppercase ">
                    
                            <div className="col-span-6">Dish Name</div>
                            <div className="col-span-2">Price</div>
                         <div className="col-span-2">Status</div>
                            <div className="col-span-2 text-center">Actions</div>
                              
                </div>
                {foodList.map(food => (
                     <div key={food.foodId} className="grid grid-cols-12 px-8 py-5 border-b border-gray-200  font-semibold text-sm uppercase">
                    
                            <div className="col-span-6 flex items-center">{food.foodName}</div>
                            <div className="col-span-2 flex items-center">${food.foodPrice}</div>
                         <div className="col-span-2 flex items-center">
                            {food.isAvailable ? "Available" : "Unavailable"}
                         </div>
                            <div className="col-span-2 flex  justify-center items-center">
                                <Button className={"text-red-500 bg-red-200"}><RiDeleteBin5Fill size={25} onClick={() => handleDeleteMenuItem(menuId,food.foodId)} /></Button>
                            </div>
                              
                </div>
                ))}

               

               
            </div>

            {isAddModalOpen ? 
            <AddFoodIntoMenu menuName={menuDetail.menuName} menuId={menuDetail.menuId} setModalIsShow={setIsAddModelOpen} fetchMenuFood={handleGetMenuFood}/>   
            : ""    
        }
       
                         
      
        </>)
}