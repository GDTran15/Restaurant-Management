
import SearchBar from "../../../component/SearchBar";
import { useEffect, useState } from "react";
import Button from "../../../component/Button";
import Pagination from "../../../component/Pagination";
import { CiCircleRemove } from "react-icons/ci";
import api from "../../../api";

export default function AddFoodIntoMenu({menuName, menuId, setModalIsShow,fetchMenuFood}){
    const [search,setSearch] = useState("");
    const [page,setPage] = useState(0);  
    const [foodList,setFoodList] = useState([])
    const [paginationInf,setPaginationInf] = useState({});
    const [foodsAddIntoMenuId, setFoodsAddIntoMenuId] = useState([]);
    const [foodsAddIntoMenu, setFoodsAddIntoMenu] = useState([]);

    //getFood
    const handleGetFoodToAdd = async () => {
        try {
            const response = await api.get(`/foods/for-menu`,{
              params: {page,size : 9,search,menuId}
            });
            
            setFoodList(response.data.content);
            setPaginationInf(response.data.page);
        } catch (error) {
            console.log(error);
        }
    }

    const handleAddFoodIntoMenu = async () => {
      try {
          const response = await api.post(`/menu-items`,{
            menuId: menuId,
            foodIdList: foodsAddIntoMenuId
          })
           console.log(response)
           fetchMenuFood();
           setModalIsShow(false);
      } catch (error) {
        console.log(error)        
      }
    }

    const handleAddFood = (food) => {
      setFoodsAddIntoMenu((prev) => {
        return [...prev,food]
      }),
      setFoodsAddIntoMenuId((prev) => {
        return [...prev,food.foodId]
      }
    );
    }

   

    const removeFood = (foodId) => {
      setFoodsAddIntoMenu(prev => prev.filter(food => food.foodId !== foodId))
      setFoodsAddIntoMenuId(prev => prev.filter(id => id !== foodId));
    };
    
    useEffect(() => {
      handleGetFoodToAdd();
    } , [search])

    return (<>
    <div className=" fixed inset-0 bg-black/40 flex items-center justify-center z-50 ">
                <div className="bg-white py-10 px-6 rounded-xl border-gray-300 border w-4xl">
                    <h5 className=" font-bold text-2xl mb-5">
                        Add items to {menuName}
                      </h5>
                    <div className="mb-7 flex flex-col gap-3.5">

                      <div className="flex gap-2 items-center">
                        <p className="">Items: </p>
                        {foodsAddIntoMenu.map(food => 
                          <span className="bg-gray-300 text-sm rounded-2xl px-1 items-center justify-center flex gap-2">
                            {food.foodName}
                            <CiCircleRemove size={17}  className="hover:text-white" onClick={() => removeFood(food.foodId)}/>
                          </span>
                        )}
                      </div>
                     <SearchBar placeholder={"Search for foods"} value={search} setValue={setSearch} />
                     </div>

                        <div className="grid sm:grid-cols-2 gap-5">
                            {foodList.map(food => 
                                (
                                  <>
                                  <div className="bg-white py-5 px-6 rounded-xl border-gray-300 border flex justify-between">
                                    <div>
                                      <h5>{food.foodName}</h5>
                                    <p>
                                      ${food.foodPrice}
                                    </p>
                                    </div>
                                   <Button
                                    className={`food-edit-button disabled:opacity-50 disabled:cursor-not-allowed disabled:bg-gray-50 disabled:hover:text-gray-500`}
                                    disabled={foodsAddIntoMenuId.includes(food.foodId)}
                                    onClick={() => handleAddFood(food)}
                                >
                                    {foodsAddIntoMenuId.includes(food.foodId) ? "Added" : "Add"}
                                </Button>
                                    
                                  </div>
                                  </>
                                )
                            )}
                      </div>

                        <div>
                            <div class="flex items-center justify-between border-t border-white/10 px-4 py-3 sm:px-6">
        <div class="flex flex-1 justify-between sm:hidden">
          <a href="#" class="relative inline-flex items-center rounded-md border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-gray-200 hover:bg-white/10">Previous</a>
          <a href="#" class="relative ml-3 inline-flex items-center rounded-md border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-gray-200 hover:bg-white/10">Next</a>
        </div>



          <Pagination pageInfo={paginationInf}/>                 
                           
                          </div>

                            <div className="flex justify-between">
                              <Button className={"bg-gray-200 text-gray-600 "}  onClick={() => setModalIsShow(false)}>Cancel</Button>
                              <Button className={"bg-blue-500 text-white"} onClick={() => {handleAddFoodIntoMenu();
                                
                              }}>Add</Button>
                            </div>
                        </div>
                </div>
            </div>
    </>)

}