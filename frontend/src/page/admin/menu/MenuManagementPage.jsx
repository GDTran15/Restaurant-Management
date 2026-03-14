import { IoIosAdd } from "react-icons/io";
import Button from "../../../component/Button";
import FormWrapper from "../../../component/FormWrapper";
import InputField from "../../../component/InputField";
import { useEffect, useState } from "react";
import axios from "axios";
import MenuCard from "../../../component/MenuCard";

export default function MenuManagementPage() {
    const [menuName,setMenuName] = useState("");
    const [menuDescription,setMenuDescription] = useState("");
    const [menuList,setMenuList] = useState([]);

     const handleAddMenu = async () => {
      
        try {
            const response = await axios.post(`http://localhost:8080/menus`, {
                
                menuName : menuName,
                menuDescription : menuDescription
            })
            handleGetMenu();
            setMenuName("");
            setMenuDescription("");
           
        } catch (error) {
            console.log(error.response);
           
        }
    }
     const handleGetMenu = async () => {
      
        try {
            const response = await axios.get(`http://localhost:8080/menus`)
            setMenuList(response.data);
            
           
        } catch (error) {
            console.log(error.response);
           
        }
    }

    useEffect(() => {
        handleGetMenu();
    },[])

    return (<>
        <div>
        <FormWrapper width ={"full"} title={"Add Menu"} submitFuntion={handleAddMenu}>
             <div className="grid md:grid-cols-2  grid-cols-1 gap-3">
                 <InputField inputType={"text"} label={"Menu Name"} value={menuName} setValue={setMenuName}> </InputField>                   
                 <InputField inputType={"text"} label={"Menu Description"} value={menuDescription} setValue={setMenuDescription}></InputField>   
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
                    />
                ))}
        </div>
        </>
    );
}