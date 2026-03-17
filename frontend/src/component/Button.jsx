export default function Button({children,type = "button",variant,width , className , onClick ,disabled
}) {

    const variants = {
        navy: "bg-main-navy text-white",
        warning: "bg-yellow-300 text-black",
        danger: "bg-red-600 text-black",
        outline: "border border-gray-400 text-gray-700 bg-white",
        secondary: "bg-gray-300",
        
    }

    return (
        <button
        disabled={disabled}
            type={type}
            className={`${variants[variant]} flex items-center justify-center px-4 py-1 rounded-xl hover:opacity-90 mt-2 ${width} ${className}`}
            onClick={onClick}
        >
            {children}
        </button>
    )
}