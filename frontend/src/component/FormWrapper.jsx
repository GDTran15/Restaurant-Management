export default function FormWrapper({title,children ,submitFuntion ,width}){
    return(<>
    <div className={` bg-white w-${width} py-10 px-6 rounded border-gray-300 border`}>
        <h2 className="text-center">
            {title}
        </h2>
        <form onSubmit={(e) => {
            e.preventDefault();
            submitFuntion();}}>
        <div>{children}</div>
        
        </form>
    </div>
    </>)
}