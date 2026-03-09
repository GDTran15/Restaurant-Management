import { FaSearch } from "react-icons/fa";

export default function SearchBar({placeholder, handleSearch, value,setValue}){
    return(<>
    
    <div className="relative w-full max-w-md ">
     
      <div className="absolute inset-y-0 left-0 flex items-center pl-3 pointer-events-none">
        <FaSearch className="text-gray-400" />
      </div>
        <input
        type="text"
        placeholder={placeholder}
        value={value}
        onChange={(e) => {
            setValue(e.target.value)
            handleSearch(e.target.value); }}
        className="w-full pl-10 pr-3 py-2 rounded-lg border border-gray-300 bg-white text-gray-900 placeholder-gray-400 "
      />
      
    </div>
    
        
       
        
    </>)
}