package yau.celine.golocal.app;

import java.util.ArrayList;

import yau.celine.golocal.utils.ItemNotBelongToShopException;
import yau.celine.golocal.utils.objects.OrderItemObject;
import yau.celine.golocal.utils.objects.ShopObject;

public class CartSingleton {
    private static CartSingleton mInstance;
    private ArrayList<OrderItemObject> cartItemList;
    private ShopObject mShop;

    public static synchronized CartSingleton getInstance() {
        if (mInstance == null) {
            mInstance = new CartSingleton();
        }
        return mInstance;
    }

    private CartSingleton() {
        cartItemList = new ArrayList<>();
        mShop = null;
    }

    public ArrayList<OrderItemObject> getCartItemList() {
        return cartItemList;
    }

    public void addFirstItem(OrderItemObject item, ShopObject shop) throws ItemNotBelongToShopException {
        if (cartItemList.size() == 0) {
            mShop = shop;
            addMenuItem(item);
        }
    }

    public boolean addMenuItem(OrderItemObject item) throws ItemNotBelongToShopException{
        if (mShop == null) {
            return false;
        }

        if (item.getShopId() == mShop.getId()) {
            cartItemList.add(item);
            return true;
        } else {
            throw new ItemNotBelongToShopException(
                    item.getName() + "does not belong in shop id: "+mShop.getId());
        }

    }

    public OrderItemObject getItem (int position) {
        return cartItemList.get(position);
    }

    public void resetCart() {
        this.cartItemList.clear();
        this.mShop = null;

    }

    public int getOrderSize() {
        return cartItemList.size();
    }

    public ShopObject getShop() {
        return mShop;
    }
}
