 export default function SideBarIcon({icon, text,onClick,pageKey,currentPage }){
    


    return(<>
    <div className={`sidebar-icon ${pageKey === currentPage ? "sidebar-icon-active" : ""} `} onClick={onClick} >
        <span>{icon}</span>
      <h4>{text}</h4>
     </div>
        
    </>)
        
    };