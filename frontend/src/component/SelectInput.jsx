export default function SelectInput({label , value , setValue, itemList, itemValue, itemName,error}){
    return(
        <>
        <div>
           <div className="flex justify-between items-center mb-1">
      <label className="block text-base font-semibold ">{label}</label>
      {
        error ? <i className="text-red-500">*{error}</i> : ""
      }
      
      </div>
            <select  
             className={`border p-1.5 rounded w-full bg-input-bg focus:outline-none focus:ring-0 focus:border-grey-600 mb-2 `}
                value={value}
                onChange={(e) => {
                    setValue(e.target.value);
                    console.log(e.target.value);
                }}
             >
                <option value="">Select {label}</option>
                {itemList.map((item) => (
                 
                <option key={item[itemValue]} value={item[itemValue]}>
                    {item[itemName]}
                </option>
                ))}
             </select>
        </div>
    
        
        </>
    )
}