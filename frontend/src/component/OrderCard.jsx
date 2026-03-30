export default function OrderCard({order}){
        const orderStatusConfig = {
            PENDING: {
                label: "Pending",
                className: "bg-yellow-100 text-yellow-700 ",
            },
            IN_PROGRESS: {
                label: "In Progress",
                className: "bg-blue-100 text-blue-700",
            },
            CANCELLED: {
                label: "Cancelled",
                className: "bg-red-100 text-red-700",
            },
            COMPLETED: {
                label: "Completed",
                className: "bg-green-100 text-green-700",
            },
            };
        const orderStatus = orderStatusConfig[order.orderStatus];
    return(<>
        <div className="bg-white py-10 px-6 rounded-xl border-gray-300 border h-full w-full ">
            <div className="flex gap-5 flex-col relative">
                  <span className={`${orderStatus.className} absolute right-0 py-1 px-2 rounded-2xl=- `}>
                  {orderStatus.label}
                </span>

                <div className="flex justify-between ">
                <div >
                    <h5 className="font-bold text-xl text-main-navy">Order-{order.orderNumber}</h5>
                    <p className="text-gray-500">{new Date(order.orderTime).toLocaleString()}</p>
                </div>
              
            </div>
            
            </div>
            
        </div> 
    </>)

}