import NavBar from "../../component/NavBar";
import { Outlet } from "react-router-dom";

export default function AdminHomePage() {
  const adminName = localStorage.getItem("username");

  return (
    <>
      <NavBar username={adminName} />
      <main className="ml-sidebar max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
        <Outlet />
      </main>
    </>
  );
}