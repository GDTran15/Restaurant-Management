import axios from "axios";
import { useState } from "react";
import { BACKEND_URL } from "../config";


import { useNavigate } from "react-router-dom";
import FormWrapper from "../component/FormWrapper";
import InputField from "../component/InputField";
import Button from "../component/Button";

export default function LoginPage(){
    const navigate = useNavigate();
    const [username,setUsername] = useState("");
    const [password,setPassword] = useState("");

    const loginForUser = async () =>{
        try {
            const response = await axios.post(`http://localhost:8080/login`,
                {
                    username : username,
                    password : password
                }
            )
            const role = response.data.role;
            if(role === "ADMIN"){
                localStorage.setItem("username", response.data.username);
                navigate("/admin/dashboard");
            }

        } catch (error) {
            console.log(error);
        }
    }



    return(
        <>
        <div className="flex justify-center items-center h-screen">
            
            <FormWrapper title={"Login"} submitFuntion={loginForUser}>
                <InputField label={"Username"} inputType={"text"} placeholder={"Enter your Username"} value={username} setValue={setUsername}/>
                <InputField label={"Password"} inputType={"password"} placeholder={"Enter your Password"} value={password} setValue={setPassword}/> 
                <Button buttonTitle={"Login"} variant={"navy"} width={"w-full"} type={"submit"}/>
            </FormWrapper>
        </div>
        </>
    )



}