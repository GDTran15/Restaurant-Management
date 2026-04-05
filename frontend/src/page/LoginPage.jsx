import axios from "axios";
import { useState } from "react";
import { BACKEND_URL } from "../config";


import { useNavigate } from "react-router-dom";
import FormWrapper from "../component/FormWrapper";
import InputField from "../component/InputField";
import Button from "../component/Button";
import { useAuth } from "./authentication/AuthProvider";
import api from "../api";


export default function LoginPage(){
    const navigate = useNavigate();
    const [username,setUsername] = useState("");
    const [password,setPassword] = useState("");
    const { setToken } = useAuth();

    const loginForUser = async () =>{
        try {
            const response = await api.post(`/authenticate`,
                {
                    username : username,
                    password : password
                }
            )
            
            setToken(response.data.accessToken);
            api.defaults.headers.common["Authorization"] = `Bearer ${response.data.accessToken}`;

             navigate("/admin");
            

        } catch (error) {
            console.log(error.response);
        }
    }



    return(
        <>
        <div className="flex justify-center items-center h-screen">
            
            <FormWrapper width={96} title={"Login"} submitFuntion={loginForUser}>
                <InputField label={"Username"} inputType={"text"} placeholder={"Enter your Username"} value={username} setValue={setUsername}/>
                <InputField label={"Password"} inputType={"password"} placeholder={"Enter your Password"} value={password} setValue={setPassword}/> 
                <Button buttonTitle={"Login"} variant={"navy"} width={"w-full"} type={"submit"}>
                Login
                </Button>
            </FormWrapper>
        </div>
        </>
    )



}