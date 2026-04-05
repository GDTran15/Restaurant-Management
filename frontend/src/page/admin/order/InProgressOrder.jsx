import { useEffect, useState } from "react";
import AdminOrderCard from "../../../component/AdminOrderCard";
import api from "../../../api";

export default function InProgressOrder(){
        const [inProgressOrderList,setInProgressOrderList] = useState([]);
    
     useEffect(() => {
         const handleGetInProgressOrder = async () => {
        try {
            const response = await api.get(`/orders/in-progress`);
            console.log(response);
            setInProgressOrderList(response.data)
        } catch (error) {
            console.log(error);
        }
    }
          handleGetInProgressOrder();
    } 
        
  ,[]);
        return(<>
        
                    <AdminOrderCard orderStatus={"in-progress"} orderList={inProgressOrderList}/>
        </>)
}