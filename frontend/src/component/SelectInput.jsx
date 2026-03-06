export default function SelectInput({label , value , setValue}){
    return(
        <>
        <div>
            <label className=" block text-base mb-2 font-semibold">
                {label}
            </label>
            <select  
             className={`border p-1.5 rounded w-full bg-input-bg focus:outline-none focus:ring-0 focus:border-grey-600 mb-2 `}
                value={value}
                onChange={(e) => setValue(e.target.value) }
             >
                <option value="1">Meat</option>
                <option value="1">M</option>
                <option value="1">A</option>
                <option value="1">A</option>
             </select>
        </div>
    
        
        </>
    )
}