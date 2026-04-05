
import { useEffect, useState } from "react";

import AdminOrderCard from "../../../component/AdminOrderCard";
import api from "../../../api";

export default function PendingOrder(){
        const [pendingOrderList,setPendingOrderList] = useState([]);
    
     useEffect(() => {
         const handleGetPendingOrder = async () => {
        try {
            const response = await api.get(`/orders/pending`);
            console.log(response);
            setPendingOrderList(response.data)
        } catch (error) {
            console.log(error);
        }
    }
          handleGetPendingOrder();
    } 
        
  ,[]);
        return(<>
            <AdminOrderCard orderStatus={"pending"} orderList={pendingOrderList}/>
        </>)
}