export default function FormWrapper({title,children ,submitFuntion ,width}){
    return(<>
    <div className={` bg-white w-${width} py-10 px-6 rounded-xl border-gray-300 border`}>
        {title === "Login" ?
        <h2 className="text-center">
            {title}
        </h2> :
        <h2 className="text-left text-2xl mb-3">
            {title}
        </h2>
    }
        
        <form onSubmit={(e) => {
            e.preventDefault();
            submitFuntion();}}>
        <div>{children}</div>
        
        </form>
    </div>
    </>)
}