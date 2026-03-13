import { IoIosAdd } from "react-icons/io";
import Button from "../../../component/Button";
import FormWrapper from "../../../component/FormWrapper";
import InputField from "../../../component/InputField";

export default function MenuManagementPage() {
    return (
        <div>
        <FormWrapper width ={"full"} title={"Add Menu"} >
             <div className="grid md:grid-cols-2  grid-cols-1 gap-3">
                 <InputField inputType={"text"} label={"Menu Name"}> </InputField>                   
                 <InputField inputType={"text"} label={"Menu Description"}></InputField>   
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