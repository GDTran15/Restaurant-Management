
import { useState } from "react";
import AddFoodPage from "./AddFoodPage";
import ViewFoodPage from "./ViewFoodPage";
import FoodCategoryPage from "./FoodCategoryPage";

export default function FoodManagementPage() {
    const [currentPage,setCurrentPage] = useState("add");

    let mainContent;
    
    switch(currentPage){
        case "add":
            mainContent = <AddFoodPage/>;
            break;
        case "view":
            mainContent = <ViewFoodPage/>;
            break;
        case "category": 
            mainContent = <FoodCategoryPage/>
            break;

    }

   

    return (
        <>
            
                
                    <nav className="bg-dark-cream mb-7 font-bold inline-flex space-x-1 p-1  rounded-2xl">
                        <ul onClick={() => setCurrentPage("add")} className={`horizontal-nav-bar ${currentPage === "add" ? "horizontal-nav-bar-active" : ""}`}>Add</ul>
                        <ul onClick={() => setCurrentPage("view")}  className={`horizontal-nav-bar ${currentPage === "view" ? "horizontal-nav-bar-active" : ""}`}>View</ul>
                        <ul onClick={() => setCurrentPage("category")}  className={`horizontal-nav-bar ${currentPage === "category" ? "horizontal-nav-bar-active" : ""}`}>Category</ul>
                    </nav>
                    <div>
                        {mainContent}
                    </div>
             
         
        </>
    );
}
