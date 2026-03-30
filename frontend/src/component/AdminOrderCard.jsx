import axios from "axios";
import Button from "./Button";

export default function AdminOrderCard({orderStatus,orderList}){
    const handleStartProgress = async (orderId) => {
        try {
            const response = await axios.patch(`http://localhost:8080/orders/${orderId}/processing`);
            console.log(response)
        } catch (error) {
            console.log(error);
        }
    }
    const handleComplete = async (orderId) => {
        try {
            const response = await axios.patch(`http://localhost:8080/orders/${orderId}/complete`);
            console.log(response)
        } catch (error) {
            console.log(error);
        }
    }



    return (<>
        <div className="mt-10 flex flex-col gap-3">
                    {orderList.map((order) => (
                        <div className="bg-white p-4 rounded-xl relative">

                        {orderStatus === "pending" ?
                          <Button className={"food-edit-button absolute right-5"} onClick={() => handleStartProgress(order.orderId)}>Start cooking</Button>

                        : ""}
                        {orderStatus === "in-progress" ?
                          <Button className={"food-edit-button absolute right-5"} onClick={() => handleComplete(order.orderId)}>Complete</Button>

                        : ""}
                        {orderStatus === "complete" ?
                          <Button className={" absolute right-5 "} disabled={"true"}>Completed</Button>

                        : ""}

                            <div>
                            <h5 className="font-bold text-lg text-main-navy">
                                Table {order.tableNumber}
                            </h5>
                            <span className="text-sm text-gray-500">
                                #{order.orderNumber}
                            </span>
                            </div>
                            <p className="text-gray-500 text-sm">
                                {new Date(order.orderTime).toLocaleString()}
                            </p>
                        <div className="flex flex-col gap-2 border-b border-gray-300 pb-7" >
                            {order.orderItems.map((orderItem) => (
                                <div className="flex justify-between ">
                                    <p>{orderItem.quantity}x {orderItem.foodName}</p>
                                    <p>${orderItem.foodPrice.toFixed(2)}</p>
                                </div>
                            ))}
                        </div>

                        <div className="flex justify-between">
                            <h5>Total Amount</h5>
                            <p>${order.orderTotalPrice.toFixed(2)}</p>
            </div>
                        </div>  
                        
                    ))}

                </div>
    </>)
}