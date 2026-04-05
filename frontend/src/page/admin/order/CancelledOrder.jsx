
import { useEffect, useState } from "react";
import AdminOrderCard from "../../../component/AdminOrderCard";
import api from "../../../api";

export default function CancelledOrder(){
        const [cancelledOrderList,setCancelledOrderList] = useState([]);
    
     useEffect(() => {
         const handleGetCancelledOrderList = async () => {
        try {
            const response = await api.get(`/orders/cancelled`);
            console.log(response);
            setCancelledOrderList(response.data)
        } catch (error) {
            console.log(error);
        }
    }
          handleGetCancelledOrderList();
    } 
        
  ,[]);
        return(<>
            <AdminOrderCard orderStatus={"cancelled"} orderList={cancelledOrderList}/>
        </>)
}