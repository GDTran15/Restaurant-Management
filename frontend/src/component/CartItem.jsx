import Button from "./Button";

export default function CartItem({ item, onIncrease, onDecrease }) {
  return (
    <div className="flex justify-between items-center py-3 border-b">
      <div>
        <p className="font-semibold">{item.foodName}</p>
        <p className="text-sm text-gray-500">${item.foodPrice} each</p>
      </div>

      <div className="text-right flex items-center gap-4">
        <Button
          className={"food-edit-button font-bold"}
          onClick={() => onDecrease(item.foodId)}
        >
          -
        </Button>

        <p>{item.quantity}</p>

        <Button
          className={"food-edit-button"}
          onClick={() => onIncrease(item.foodId)}
        >
          +
        </Button>
      </div>
    </div>
  );
}