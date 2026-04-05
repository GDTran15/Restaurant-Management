
import { useEffect, useState } from "react";
import AdminOrderCard from "../../../component/AdminOrderCard";
import api from "../../../api";

export default function CompleteOrder(){
        const [completeOrderList,setCompleteOrderList] = useState([]);
    
     useEffect(() => {
         const handleGetCompleteOrderList = async () => {
        try {
            const response = await api.get(`/orders/completed`);
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