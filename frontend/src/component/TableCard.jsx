import { PiChairFill } from "react-icons/pi";
import Button from "./Button";
import { RiDeleteBin5Fill } from "react-icons/ri";

export default function TableCard({restaurantTableId,restaurantTableNumber,restaurantTableStatus,capacity,deleteTable}){
    return(<>
        <div className="bg-white h-auto py-6 flex flex-col items-center rounded-xl px-10 border-gray-300 border gap-2 hover:opacity-60">
            <PiChairFill size={25} className="text-amber-700"/>
            <h5 className="font-semibold text-main-navy">Table {restaurantTableNumber}</h5>
            <p className="text-gray-500">{capacity} seats</p>
            <span className="bg-second-cream rounded-2xl px-2 py-1 text-xs font-semibold">{restaurantTableStatus ? "Available" : "Unavailable"}</span>
            <Button className={"bg-input-bg border border-gray-300 w-full hover:bg-main-navy hover:text-white"} onClick={() => deleteTable(restaurantTableId)} ><RiDeleteBin5Fill size={23}/></Button>
        </div>
    </>)
}