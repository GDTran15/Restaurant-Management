import { IoIosAdd } from "react-icons/io";
import Button from "../../../component/Button";
import FormWrapper from "../../../component/FormWrapper";
import InputField from "../../../component/InputField";
import { useState } from "react";
import axios from "axios";

export default function MenuManagementPage() {
    const [menuName,setMenuName] = useState("");
    const [menuDescription,setMenuDescription] = useState("");

     const handleAddMenu = async () => {
      
        try {
            const response = await axios.post(`http://localhost:8080/menus`, {
                
                menuName : menuName,
                menuDescription : menuDescription
            })
            setMenuName("");
            setMenuDescription("");
           
        } catch (error) {
            console.log(error.response);
           
        }
    }

    return (
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
    );
}