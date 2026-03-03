export default function Button({buttonTitle, variant,width, type
 }){
    const variants = {
        navy : "bg-main-navy"
    }
    return(<>
        <button type={type} className={`${variants[variant]} text-white px-4 py-1 rounded hover:opacity-90 mt-2 ${width}`} >
            {buttonTitle}
        </button>
    </>)
}