
import FoodCardForCustomer from "./FoodCardForCustomer";
import Loading from "./Loading";

export default function MenuSection({
  categoryList,
  foods,
  onAddFood,
  loadingRef,
  hasMore,
}) {
  return (
    <div className="flex-1 space-y-10">
      {categoryList.map((category) => {
        const foodsInCategory = foods.filter(
          (food) => food.foodCategoryName === category.foodCategoryName
        );

        if (foodsInCategory.length === 0) return null;

        return (
          <div key={category.foodCategoryId}>
            <div className="grid md:grid-cols-2 gap-6">
              {foodsInCategory.map((food) => (
                <FoodCardForCustomer key={food.foodId} food={food} onAddFood={onAddFood} />
              ))}
            </div>
          </div>
        );
      })}

      {hasMore && <Loading ref={loadingRef} />}
    </div>
  );
}