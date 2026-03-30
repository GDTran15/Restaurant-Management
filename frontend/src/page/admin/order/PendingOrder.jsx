import axios from "axios";
import { useEffect, useState } from "react";
import Button from "../../../component/Button";
import AdminOrderCard from "../../../component/AdminOrderCard";

export default function PendingOrder(){
        const [pendingOrderList,setPendingOrderList] = useState([]);
    
     useEffect(() => {
         const handleGetPendingOrder = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/orders/pending`);
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