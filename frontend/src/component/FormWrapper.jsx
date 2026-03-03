export default function FormWrapper({title,children ,submitFuntion}){
    return(<>
    <div className=" bg-white w-96 py-10 px-6 rounded-2xl shadow-2xl border">
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