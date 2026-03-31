import { PiChairFill } from "react-icons/pi";
import Button from "./Button";
import { RiDeleteBin5Fill } from "react-icons/ri";
import { useState } from "react";
import { IoMdClose } from "react-icons/io";

export default function TableCard({restaurantTableId,restaurantTableNumber,restaurantTableStatus,capacity,deleteTable}){
    console.log(restaurantTableId)
    const[showQrCode,setShowQrCode] = useState(false);

    return(<>
        <div onClick={()=>setShowQrCode(true)} className="bg-white h-auto py-6 flex flex-col items-center rounded-xl px-10 border-gray-300 border gap-2 hover:opacity-60">
            <PiChairFill size={25} className="text-amber-700"/>
            <h5 className="font-semibold text-main-navy">Table {restaurantTableNumber}</h5>
            <p className="text-gray-500">{capacity} seats</p>
            <span className="bg-second-cream rounded-2xl px-2 py-1 text-xs font-semibold">{restaurantTableStatus ? "Available" : "Unavailable"}</span>
            <Button className={"bg-input-bg border border-gray-300 w-full hover:bg-main-navy hover:text-white"} onClick={() => deleteTable(restaurantTableId)} ><RiDeleteBin5Fill size={23}/></Button>
        </div>
       {showQrCode ? <div className="fixed inset-0 bg-black/40 flex items-center justify-center z-50 ">
            <IoMdClose onClick={() => setShowQrCode(false)} size={30} className="absolute top-28 right-130 text-white cursor-pointer"/>
             <img  src={`http://localhost:8080/tables/${restaurantTableId}/qrCode`}
  alt="QR Code" />
        </div> : ""}
        
    </>)
}