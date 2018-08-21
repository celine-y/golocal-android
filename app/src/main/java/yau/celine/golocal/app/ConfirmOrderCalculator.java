package yau.celine.golocal.app;

import java.text.NumberFormat;
import java.util.ArrayList;

import yau.celine.golocal.utils.objects.OrderItemObject;
import yau.celine.golocal.utils.objects.TaxObject;

public class ConfirmOrderCalculator {
    private ArrayList<TaxObject> taxes;


    public ArrayList<TaxObject> getTaxes() {
        return taxes;
    }

    public String getFormattedTotal() {
        NumberFormat numberFormat = NumberFormat.getCurrencyInstance();

        return numberFormat.format(getTotalAmount());
    }

    private double getTotalAmount() {
        double total = getPretaxTotal();
        double totalTaxPercent = 0.0;

//        TODO: calculate taxes

        return total + totalTaxPercent;
    }

    private double getPretaxTotal() {
        double total = 0.0;
        ArrayList<OrderItemObject> orderItems = CartSingleton.getInstance().getCartItemList();
        for (int i = 0; i < orderItems.size(); i++) {
            total += orderItems.get(i).getPrice();
        }

        return total;
    }
}
