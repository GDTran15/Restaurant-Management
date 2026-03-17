import { NavLink } from "react-router-dom";

export default function SideBarIcon({ icon, text, to }) {
  return (
    <NavLink
      to={to}
      end={to === "/admin"}
      className={({ isActive }) =>
        `sidebar-icon ${isActive ? "sidebar-icon-active" : ""}`
      }
    >
      <span>{icon}</span>
      <h4>{text}</h4>
    </NavLink>
  );
}