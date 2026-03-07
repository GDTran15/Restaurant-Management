import { IoIosAdd } from "react-icons/io";
import Button from "../../component/Button";
import FormWrapper from "../../component/FormWrapper";
import InputField from "../../component/InputField";
import SelectInput from "../../component/SelectInput";

export default function StaffManagementPage() {
    return (
        <div>
            <div className="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
                <div>
                  <FormWrapper width ={"full"}>
                <div className=" grid grid-cols-2 gap-2">
                    <div>
                        <InputField  label={"Username"}/>
                        <InputField label={"Phone"}/>
                    </div>
                    <div>
                        <InputField label={"Password"}/>
                        
                       <SelectInput label={"Role"}/>
                    </div>
                    <div className=" col-span-2">
                        
                        <InputField   label={"Email"}/>
                    </div>
                </div>
                    <Button buttonTitle={"Login"} variant={"navy"} width={"w-35"} type={"submit"}>
                       
                      <div className="flex items-center gap-2">
                        <IoIosAdd size={24} />
                        <span>Add Staff</span>
                        </div>
                    </Button>
                  </FormWrapper>
                </div>
            </div>
        </div>
    );
}