import { IoIosAdd } from "react-icons/io";
import Button from "../../../component/Button";
import FormWrapper from "../../../component/FormWrapper";
import InputField from "../../../component/InputField";
import { useEffect, useState } from "react";

import MenuCard from "../../../component/MenuCard";
import api from "../../../api";

export default function MenuManagementPage() {
    const [menuName,setMenuName] = useState("");
    const [menuDescription,setMenuDescription] = useState("");
    const [menuList,setMenuList] = useState([]);
    const [error,setError] = useState([]);

     const handleAddMenu = async () => {
      
        try {
            const response = await api.post(`/menus`, {
                
                menuName : menuName,
                menuDescription : menuDescription
            })
            handleGetMenu();
            setMenuName("");
            setMenuDescription("");
           
        } catch (error) {
            setError(error.response.data);
           
        }
    }
     const handleGetMenu = async () => {
      
        try {
            const response = await api.get(`/menus`)
            setMenuList(response.data);
            
           
        } catch (error) {
            console.log(error.response.data);
           
        }
    }

    useEffect(() => {
        handleGetMenu();
    },[])

    return (<>
        <div>
        <FormWrapper width ={"full"} title={"Add Menu"} submitFuntion={handleAddMenu}>
             <div className="grid md:grid-cols-2  grid-cols-1 gap-3">
                 <InputField inputType={"text"} label={"Menu Name"} value={menuName} setValue={setMenuName} error={error.menuName}> </InputField>                   
                 <InputField inputType={"text"} label={"Menu Description"} value={menuDescription} setValue={setMenuDescription} error={error.menuDescription}></InputField>   
              </div>
                                    
                                             
             <Button  variant={"navy"} width={"w-38"} type={"submit"}>
                  <div className="flex items-center gap-2 ">
                     <IoIosAdd size={24} />
                       <span>Add Menu</span>
                   </div>
               </Button>
                                                    
              </FormWrapper>
           </div>
        <div className="mt-5 grid md:grid-cols-3 gap-5">
                {menuList.map(menu => (
                    <MenuCard menuId={menu.menuId} 
                    menuName={menu.menuName}
                    menuDescription={menu.menuDescription}
                    isAvailable={menu.isAvailable}
                    numberOfFoods={menu.numberOfFoods}
                    handleGetMenu={handleGetMenu}
                    />
                ))}
        </div>
        </>
    );
}