import { useState } from "react"
import { GoUpload } from "react-icons/go";


export default function UploadImage({label, value, setValue}){
    


    const handleFileChange = (e) => {
        const file = e.target.files[0].name;
         setValue(file);
           
    };

    return(
        <>
        <div>
            <label className=" block text-base mb-2 font-semibold">
                {label}
            </label>
            
            <div className="flex flex-col">
                <label htmlFor="inputFile" className=" bg-input-bg inline-flex h-10 w-40 items-center gap-2 p-1.5 border rounded-xl
              hover:bg-main-navy hover:text-input-bg transition-all  
             ">
                <GoUpload size={28}/>
                Add Image
             </label>
            
            <input type="file" id="inputFile" className=" hidden" onChange={handleFileChange} />
            
            {value &&  
            (
                <img src={`/${value}`} alt="preview" className="mt-2 w-40 h-40  rounded-lg" />
            )
            }
            </div>
             
         </div>
             
            
       

        
        </>
    )
}