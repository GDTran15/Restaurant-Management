import axios from "axios";
import { useEffect, useState } from "react";
import AdminOrderCard from "../../../component/AdminOrderCard";

export default function CompleteOrder(){
        const [completeOrderList,setCompleteOrderList] = useState([]);
    
     useEffect(() => {
         const handleGetCompleteOrderList = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/orders/completed`);
            console.log(response);
            setCompleteOrderList(response.data)
        } catch (error) {
            console.log(error);
        }
    }
          handleGetCompleteOrderList();
    } 
        
  ,[]);
        return(<>
            <AdminOrderCard orderStatus={"complete"} orderList={completeOrderList}/>
        </>)
}