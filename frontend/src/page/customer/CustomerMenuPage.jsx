import { useAsyncList } from "react-stately";
import { useEffect, useRef, useState } from "react";
import { useInView } from "react-intersection-observer";
import CustomerMenuNavbar from "../../component/CustomerMenuNavBar";
import MenuSection from "../../component/MenuSection";
import CartSidebar from "../../component/CartSidebar";

import { useParams } from "react-router-dom";
import { publicApi } from "../../api";


export default function CustomerMenuPage() {
  const [cartList, setCartList] = useState([]); // backend
  const [cartListToShow, setCartListToShow] = useState([]); 
  const [menuName, setMenuName] = useState("");
  const [categoryList, setCategoryList] = useState([]);
  const [diningSessionId,setDiningSessionId] = useState(null);
   const { token } = useParams();

  

  const list = useAsyncList({
    async load({ signal, cursor }) {
      const page = cursor ?? 0;

         const res = await publicApi.get(`/menus/active?page=${page}&size=9`, {
      signal,
    });
      console.log(res);
      const json =  res.data;
      console.log("API DATA:", json);

      setCategoryList(json.foodCategory);
      setMenuName(json.menuName);

      const currentPage = json.foods.page.number;
      const totalPages = json.foods.page.totalPages;

      return {
        items: json.foods.content,
        cursor: currentPage + 1 < totalPages ? currentPage + 1 : null,
      };
    },
  });

useEffect(() => {
  const authenticateDiningSession = async () => {
    try {
      const response = await publicApi.get(
        "/dining-sessions",
        { params: { tableQrToken: token } }
      );
      console.log(response)
      setDiningSessionId(response.data.dinningSessionId);
    } catch (error) {
      console.log(error);
    }
  };

  if (token) {
    authenticateDiningSession();
  }
}, [token]);
 const submitOrder = async () => {
  try {
    const response = await publicApi.post(
      "/orders",
      {
        diningSessionId: diningSessionId,
        orderItemDTOS: cartList
      }
    );

    setCartList([]);
    setCartListToShow([]);
    console.log(response);

  } catch (error) {
    console.log(error.response);
  }
};

 

  const { ref, inView } = useInView({});
  const listRef = useRef(list);

  useEffect(() => {
    listRef.current = list;
  }, [list]);

  useEffect(() => {
    if (list.cursor !== null && inView && !list.isLoading) {
      list.loadMore();
    }
  }, [inView, list]);

  const handleAddFood = (food) => {
    setCartList((prev) => {
      const existing = prev.find((i) => i.foodId === food.foodId);

      if (existing) {
        return prev.map((i) =>
          i.foodId === food.foodId
            ? { ...i, quantity: i.quantity + 1 }
            : i
        );
      }

      return [...prev, { foodId: food.foodId, quantity: 1 }];
    });

    // UI data
    setCartListToShow((prev) => {
      const existing = prev.find((i) => i.foodId === food.foodId);

      if (existing) {
        return prev.map((i) =>
          i.foodId === food.foodId
            ? { ...i, quantity: i.quantity + 1 }
            : i
        );
      }

      return [
        ...prev,
        {
          foodId: food.foodId,
          foodName: food.foodName,
          foodPrice: food.foodPrice,
          quantity: 1,
        },
      ];
    });
  };

  const handleIncreaseFoodQuantity = (foodId) => {
    setCartListToShow((prev) =>
      prev.map((i) =>
        i.foodId === foodId ? { ...i, quantity: i.quantity + 1 } : i
      )
    );

    setCartList((prev) =>
      prev.map((i) =>
        i.foodId === foodId ? { ...i, quantity: i.quantity + 1 } : i
      )
    );
  };

  const handleDecreaseFoodQuantity = (foodId) => {
    setCartListToShow((prev) => {
      const item = prev.find((i) => i.foodId === foodId);
      if (!item) return prev;

      if (item.quantity === 1) {
        return prev.filter((i) => i.foodId !== foodId);
      }

      return prev.map((i) =>
        i.foodId === foodId ? { ...i, quantity: i.quantity - 1 } : i
      );
    });

    setCartList((prev) => {
      const item = prev.find((i) => i.foodId === foodId);
      if (!item) return prev;

      if (item.quantity === 1) {
        return prev.filter((i) => i.foodId !== foodId);
      }

      return prev.map((i) =>
        i.foodId === foodId ? { ...i, quantity: i.quantity - 1 } : i
      );
    });
  };

  const totalPrice = cartListToShow.reduce(
    (total, item) => total + item.foodPrice * item.quantity,
    0
  );

  return (
    <>
      <CustomerMenuNavbar page={"menu"} token={token}/>

      <div className="max-w-7xl mx-auto p-4 sm:p-6 lg:p-8">
        <h1 className="text-2xl font-bold mb-6">
          {menuName || "Our Menu"}
        </h1>

        <div className="flex gap-6 items-start">
          
          <MenuSection
            categoryList={categoryList}
            foods={list.items}
            onAddFood={handleAddFood}
            loadingRef={ref}
            hasMore={list.cursor !== null}
            isLoading={list.isLoading}
          />

          
          <CartSidebar
            cartListToShow={cartListToShow}
            totalPrice={totalPrice}
            onIncrease={handleIncreaseFoodQuantity}
            onDecrease={handleDecreaseFoodQuantity}
            handleSubmit={ submitOrder}
          />
        </div>
      </div>
    </>
  );
}
