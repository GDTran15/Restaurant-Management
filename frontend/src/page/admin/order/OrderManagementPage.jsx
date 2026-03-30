import axios from "axios"
import { useEffect, useState } from "react";
import PendingOrder from "./PendingOrder";
import InProgressOrder from "./InProgressOrder";
import CompleteOrder from "./CompleteOrder";

export default function OrderManagementPage(){
   
    const [orderStatusCount,setOrderStatusCount] = useState([]);
    const [currentPage,setCurrentPage] = useState("pending");

    let mainContent;
    
    switch(currentPage){
        case "pending":
            mainContent = <PendingOrder/>;
            break;
        case "inprogress":
            mainContent = <InProgressOrder/>;
            break;
        case "complete": 
            mainContent = <CompleteOrder/>;
            break;

    }

    const orderStatusToString = (orderStatus) => {
        switch(orderStatus){
            case "PENDING":
                return "Pending"
            case "IN_PROGRESS":
                return "In Progress"
            case "COMPLETED":
                return "Complete"
            case "CANCELLED":
                return "Cancelled"
        }
    }
    
   
    





   useEffect(() => {
         const handleGetOrderStatusCount = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/orders/count/session`);
            console.log(response);
            setOrderStatusCount(response.data);
        } catch (error) {
            console.log(error);
        }
    }
          handleGetOrderStatusCount();
    } 
        
  ,[]);

    


    return(<>
            <div className="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
                
                <div className="grid md:grid-cols-4  gap-3">
                    
                    {orderStatusCount.map((orderStatus) => (
                        <div className="bg-white p-4 rounded-xl">
                            <h6 className="text-gray-500">{orderStatusToString(orderStatus.orderStatus)}</h6>
                            <p>{orderStatus.count}</p>
                    </div>
                    ))}
                    
                </div>


          
            
                
                    <nav className="bg-dark-cream mt-7  font-bold inline-flex space-x-1 p-1  rounded-2xl">
                        <ul onClick={() => setCurrentPage("pending")} className={`horizontal-nav-bar ${currentPage === "pending" ? "horizontal-nav-bar-active" : ""}`}>Pending</ul>
                        <ul onClick={() => setCurrentPage("inprogress")}  className={`horizontal-nav-bar ${currentPage === "inprogress" ? "horizontal-nav-bar-active" : ""}`}>InProgress</ul>
                        <ul onClick={() => setCurrentPage("complete")}  className={`horizontal-nav-bar ${currentPage === "complete" ? "horizontal-nav-bar-active" : ""}`}>Complete</ul>
                    </nav>
                    <div>
                        {mainContent}
                    </div>
             
         
      

                
                
            </div>
            
    </>)
}