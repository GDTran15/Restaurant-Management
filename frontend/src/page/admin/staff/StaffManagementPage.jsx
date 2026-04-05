import { IoIosAdd } from "react-icons/io";
import Button from "../../../component/Button";
import FormWrapper from "../../../component/FormWrapper";
import InputField from "../../../component/InputField";
import SelectInput from "../../../component/SelectInput";
import { useEffect, useState } from "react";
import api from "../../../api";

export default function StaffManagementPage() {
    const [roleList,setRoleList] = useState([]);
    const [role,setRole] =useState(null);
    const [username,setUsername] = useState("");
    const [password,setPassword] = useState("");
    const [phone,setPhone] = useState("");
    const [email,setEmail] = useState("");
    const [error,setError] = useState({});

    useEffect(() => {
         const handleGetRoles = async () => {
        try {
            const response = await api.get(`/roles`)
           
            setRoleList(response.data)
        } catch (error) {
            console.log(error)
        
        }
    };
    handleGetRoles();
    },[])

    const handleAddUser = async () => {
        try {
            const response = await api.post(`/register`,{
                "username": username,
                "password": password,
                "email": email,
                "phone": phone,
                "role": role
            })
            console.log(response)
            setUsername("");
            setPassword("");
            setEmail("");
            setPhone("");
            setRole(null);
            setError({});

        } catch (error) {
            setError(error.response.data);
        }
    }

    return (
        <div>
            <div className="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
                <div>
                  <FormWrapper width ={"full"} submitFuntion={handleAddUser} title={"Add Staff"}>
                <div className=" grid grid-cols-2 gap-2">
                    <div>
                        <InputField  
                        error={error.username}
                        label={"Username"}
                        placeholder={"Enter user name"}
                        value={username}
                        setValue={setUsername}
                        />
                        <InputField 
                        error={error.phone}
                        label={"Phone"}
                        placeholder={"Enter user phone number"}
                        value={phone}
                        setValue={setPhone}
                        />
                    </div>
                    <div>
                        <InputField 
                        error={error.password}
                        label={"Password"}
                         placeholder={"Enter user password"}
                        value={password}
                        setValue={setPassword}
                        />
                        
                       <SelectInput error={error.role} label={"Role"} value={role} setValue={setRole} itemList={roleList} itemName={"label"} itemValue={"value"} />
                    </div>
                    <div className=" col-span-2">
                        
                        <InputField   
                        error={error.email}
                        label={"Email"}
                         placeholder={"Enter user email"}
                        value={email}
                        setValue={setEmail}
                        />
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