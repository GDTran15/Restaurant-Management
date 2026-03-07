
import { useState } from "react";
import AddFoodPage from "./food_management/AddFoodPage";
import ViewFoodPage from "./food_management/ViewFoodPage";
import FoodCategoryPage from "./food_management/FoodCategoryPage";

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
            <div>
                <div className="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
                    <nav className="bg-dark-cream mb-7 font-bold inline-flex space-x-1 p-1  rounded-2xl">
                        <ul onClick={() => setCurrentPage("add")} className={`horizontal-nav-bar ${currentPage === "add" ? "horizontal-nav-bar-active" : ""}`}>Add</ul>
                        <ul onClick={() => setCurrentPage("view")}  className={`horizontal-nav-bar ${currentPage === "view" ? "horizontal-nav-bar-active" : ""}`}>View</ul>
                        <ul onClick={() => setCurrentPage("category")}  className={`horizontal-nav-bar ${currentPage === "category" ? "horizontal-nav-bar-active" : ""}`}>Category</ul>
                    </nav>
                    <div>
                        {mainContent}
                    </div>
                </div>
            </div>
        </>
    );
}
