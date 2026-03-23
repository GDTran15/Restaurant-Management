
import { IoCartOutline } from "react-icons/io5";
import CartItem from "./CartItem";
import Button from "./Button";

export default function CartSidebar({
  cartListToShow,
  totalPrice,
  onIncrease,
  onDecrease,
  handleSubmit
}) {
  return (
    <div className="bg-white py-6 px-6 rounded-xl w-87.5 border border-gray-300 shrink-0 sticky top-25 self-start flex flex-col">
      <h5 className="font-bold flex items-center gap-2 text-xl">
        <IoCartOutline size={24} />
        Your Cart
      </h5>

      <div className="mt-6">
        {cartListToShow.map((item) => (
          <CartItem
            key={item.foodId}
            item={item}
            onIncrease={onIncrease}
            onDecrease={onDecrease}
          />
        ))}
      </div>

      <div className="flex justify-between text-xl font-semibold mt-6">
        <h5>Total</h5>
        <p>${totalPrice.toFixed(2)}</p>
      </div>

      <Button className={"bg-main-navy text-white font-bold w-full py-2 mt-6"} onClick={() => handleSubmit()}>
        Submit Order
      </Button>
    </div>
  );
}