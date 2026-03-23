import Button from "./Button";

export default function FoodCardForCustomer({ food, onAddFood }) {
  return (
    <div className="bg-white p-4 rounded-xl shadow w-full h-full flex flex-col justify-between">
      <div>
        <img
          src={`/${food.foodImageUrl}`}
          alt={food.foodName}
          className="w-full h-50 object-cover rounded-lg"
        />
        <h2 className="text-lg font-bold mt-2">{food.foodName}</h2>
        <p className="text-sm text-gray-500">{food.foodCategoryName}</p>
        <p className="text-sm mt-1">{food.description}</p>
      </div>

      <div className="flex justify-between mt-4">
        <p className="font-semibold">${food.price}</p>
        <Button
          className={"bg-main-navy text-input-bg"}
          onClick={() =>
            onAddFood({
              foodId: food.foodId,
              foodName: food.foodName,
              foodPrice: food.price,
            })
          }
        >
          Add to cart
        </Button>
      </div>
    </div>
  );
}