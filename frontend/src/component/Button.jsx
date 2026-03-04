export default function Button({buttonTitle, variant,width, type,children
 }){
    const variants = {
        navy : "bg-main-navy"
    }
    return(<>
        <button type={type} className={`${variants[variant]} text-white px-4 py-1 rounded-xl hover:opacity-90 mt-2 ${width}`} >
            {children}
        </button>
    </>)
}