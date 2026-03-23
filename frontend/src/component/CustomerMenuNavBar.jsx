
import { TbPackage } from "react-icons/tb";
import Button from "./Button";

export default function CustomerMenuNavbar() {
  return (
    <nav className="bg-main-navy h-20 sticky py-2 border-b border-gray-300 top-0 text-second-cream">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 flex justify-between items-center h-full">
        <div className="text-start">
          <h1 className="text-2xl">PiggyOinkOink</h1>
        </div>

        <div>
          <Button className={"food-edit-button flex gap-2 font-bold"}>
            <TbPackage size={20} />
            Orders
          </Button>
        </div>
      </div>
    </nav>
  );
}