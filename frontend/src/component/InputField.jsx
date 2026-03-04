export default function InputField({label,placeholder, inputType,value , setValue}){
    return(<>
        <div>
            <label className=" block text-base mb-2">
                {label}
            </label>
            <input type={inputType}  value={value} placeholder={placeholder}
             className={`border p-1.5 rounded w-full bg-input-bg focus:outline-none focus:ring-0 focus:border-grey-600 mb-2 `}
             onChange={(e) => setValue(e.target.value)  }
             />
        </div>
    </>)
}