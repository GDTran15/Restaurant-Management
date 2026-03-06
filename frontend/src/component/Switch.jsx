

export default function Switch({value,setValue}){
   
    
    const handleChange = () => {
        if(value === true){
            setValue(false);
        } else {
            setValue(true);
        }
    }
    

    return(<>
    <div className=" flex gap-3 items-center mt-2">
    
        <label class="relative inline-flex items-center cursor-pointer">
        <input type="checkbox" value="" class="sr-only peer" defaultValue={value}/>
        <div class="group peer ring-0 bg-rose-400  rounded-full outline-none duration-300 after:duration-300 w-14 h-7
         shadow-md peer-checked:bg-emerald-500  peer-focus:outline-none  after:content-[''] after:rounded-full after:absolute
          after:bg-gray-50 after:outline-none 
        after:h-5 after:w-5 after:top-1 after:left-1 after:flex after:justify-center after:items-center peer-checked:after:translate-x-7 peer-hover:after:scale-95"
        onClick={handleChange}
        >        
        </div>
        </label>
        <label className="text-center text-weight font-semibold ">
            {value === true ? "Available" : "Unavailable"
            }
        </label>
    </div>
    

    </>)
}