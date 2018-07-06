package yau.celine.golocal.app;

import java.util.ArrayList;

import yau.celine.golocal.utils.ItemNotBelongToShopException;
import yau.celine.golocal.utils.objects.OrderMenuItem;

public class CartSingleton {
    private static CartSingleton mInstance;
    private ArrayList<OrderMenuItem> cartItemList;
    private int shopId;

    public static synchronized CartSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new CartSingleton();
        }
        return mInstance;
    }

    private CartSingleton() {
        cartItemList = new ArrayList<>();
        shopId = -1;
    }

    public ArrayList<OrderMenuItem> getCartItemList() {
        return cartItemList;
    }

    public void addMenuItem(OrderMenuItem item) throws ItemNotBelongToShopException{
        if (cartItemList.size() == 0) {
            shopId = item.getShopId();
            cartItemList.add(item);
        }
        else {
            if (item.getShopId() == shopId) {
                cartItemList.add(item);
            } else {
                throw new ItemNotBelongToShopException(
                        item.getName() + "does not belong in shop id: "+shopId);
            }
        }

    }

    public OrderMenuItem getItem (int position) {
        return cartItemList.get(position);
    }

    public void resetCart() {
        this.cartItemList.clear();
        this.shopId = -1;
    }

    public int getOrderSize() {
        return cartItemList.size();
    }

    public int getShopId() {
        return shopId;
    }
}
