import { useParams } from "react-router-dom";
import CustomerMenuNavbar from "../../component/CustomerMenuNavBar";
import { useEffect, useState } from "react";
import OrderCard from "../../component/OrderCard";
import { publicApi } from "../../api";

export default  function CustomerOrderPage(){
    const { token } = useParams();
    const [diningSessionId,setDiningSessionId] = useState(null);
    const [orderList,setOrderList] = useState([]);

    console.log({token})

  useEffect(() => {
  const authenticateDiningSession = async () => {
    try {
      const response = await publicApi.get("/dining-sessions", {
        params: { tableQrToken: token }
      });

      
      setDiningSessionId(response.data.dinningSessionId); 
    } catch (error) {
      console.log(error.response);
    }
  };

  if (token) {
    authenticateDiningSession();
  }
}, [token]);

useEffect(() => {
  if (!diningSessionId) return;

  const handleGetOrder = async () => {
    try {
      const response = await publicApi.get("/orders", {
        params: { diningSessionId }
      });
      setOrderList(response.data);
    } catch (error) {
      console.log(error.response);
    }
  };

  handleGetOrder();
}, [diningSessionId]);

    
    
    return(<>
          <CustomerMenuNavbar page={"order"} token={token}/>
            <div className="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
                <div className="flex flex-col gap-3">
                    {orderList.map((order) => (
                
                    <OrderCard order={order}/>
                ))}
                </div>
                
                
                
            </div>

    </>)
    
}
