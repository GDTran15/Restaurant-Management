import { IoIosAdd } from "react-icons/io";
import Button from "../../../component/Button";
import FormWrapper from "../../../component/FormWrapper";
import { useEffect, useState } from "react";
import InputField from "../../../component/InputField";
import axios from "axios";
import TableCard from "../../../component/TableCard";

export default function TableManagementPage() {
    
    const [restaurantTableNumber, setRestaurantTableNumber] = useState();
    const [capacity,setCapacity] = useState();
    
    const [restaurantTableNumberError, setRestaurantTableNumberError] = useState("");
    const [capacityError,setCapacityError] = useState("");

    const [tableList,setTableList] =useState([]);

    const handleAddTable = async () => {
      
        try {
            const response = await axios.post(`http://localhost:8080/tables`, {
                
                tableNumber : restaurantTableNumber,
                capacity : capacity
            })
            setRestaurantTableNumber(null);
            setCapacity(null);
            handleGetTable();
        } catch (error) {
            console.log(error.response);
            if(error.response.data.message){
                setRestaurantTableNumberError(error.response.data.message);
                
            } else {
                setCapacityError(error.response.data.capacity);
                setRestaurantTableNumberError(error.response.data.tableNumber);

            }
        }
    }


    const handleGetTable = async () => {
        try {
            const response = await axios.get(`http://localhost:8080/tables`)
            console.log(response);
            setTableList(response.data)
        } catch (error) {
            console.log(error.response);
        
        }
    }

    const handleDeleteTable = async (restaurantTableId) => {
        try {
            await axios.delete(`http://localhost:8080/tables/${restaurantTableId}`);
            handleGetTable();
        } catch (error) {
            console.log(error);
        }
    }

    useEffect(() => {
        handleGetTable();
    },[])

    return (<>
        <div>
            <FormWrapper width ={"full"} title={"Add Table"} submitFuntion={handleAddTable}>
                <div className="grid md:grid-cols-2  grid-cols-1 gap-3">
                    <InputField inputType={"number"} value={restaurantTableNumber} setValue={setRestaurantTableNumber} label={"Table Number"} error={restaurantTableNumberError}></InputField>                   
                    <InputField inputType={"number"} value={capacity} setValue={setCapacity} label={"Table Capacity"} error={capacityError}></InputField>   
                </div>
                            
                                     
         <Button  variant={"navy"} width={"w-35"} type={"submit"}>
               <div className="flex items-center gap-2 ">
                       <IoIosAdd size={24} />
                       <span>Add Table</span>
                  </div>
             </Button>
                                            
                </FormWrapper>
        </div>

        <div className="mt-6 grid md:grid-cols-4 gap-3">
            {tableList.map(table =>(
                <TableCard 
                restaurantTableId= {table.restaurantTableId}
                restaurantTableNumber={table.restaurantTableNumber}
                capacity={table.capacity}
                restaurantTableStatus={table.restaurantTableStatus}
                deleteTable={handleDeleteTable}
                />
            )
            )}
        </div>
        </>
    );
}