import axios from "axios";
import { useEffect, useState } from "react";
import AdminOrderCard from "../../../component/AdminOrderCard";

export default function CancelledOrder(){
        const [cancelledOrderList,setCancelledOrderList] = useState([]);
    
     useEffect(() => {
         const handleGetCancelledOrderList = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/orders/cancelled`);
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