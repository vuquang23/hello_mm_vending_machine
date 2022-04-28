package com.hellomm.modules.vendingmachine.components;

import java.util.ArrayList;
import java.util.HashMap;

import com.hellomm.common.exceptions.CannotUnselectProductException;
import com.hellomm.common.exceptions.ExceedProductAmountException;
import com.hellomm.common.exceptions.InvalidProductException;

public class CustomerCart {
    private HashMap<Integer, Integer> denominationCount;
    private ArrayList<Integer> denominationList;

    private HashMap<String, Integer> selectedProducts;
    private ArrayList<String> selectedProductList;

    public CustomerCart() {
        this.denominationCount = new HashMap<>();
        this.denominationList = new ArrayList<>();
        this.selectedProducts = new HashMap<>();
        this.selectedProductList = new ArrayList<>();
    }

    public void insertCash(int denomination) {
        int currentAmount = this.getDenominationCount(denomination);
        if (currentAmount == 0) {
            this.denominationList.add(denomination);
        }
        this.denominationCount.put(denomination, currentAmount + 1);
    }

    public void selectProduct(Store store, String product) throws Exception {
        if (!Store.validProduct(product)) {
            throw new InvalidProductException();
        }
        int currentAmount = this.getProductCount(product);
        if (currentAmount == store.getProductCount(product)) {
            throw new ExceedProductAmountException();
        }
        if (currentAmount == 0) {
            this.selectedProductList.add(product);
        }
        this.selectedProducts.put(product, currentAmount + 1);
    }

    public void unselectProduct(String product) throws Exception {
        int currentAmount = this.getProductCount(product);
        if (currentAmount == 0) {
            throw new CannotUnselectProductException();
        }
        currentAmount -= 1;
        this.selectedProducts.put(product, currentAmount);
        if (currentAmount == 0) {
            this.deleteProductFromSelectedProductList(product);
        }
    }

    private void deleteProductFromSelectedProductList(String product) {
        for (int i = 0; i < this.selectedProductList.size(); ++i) {
            String value = this.selectedProductList.get(i);
            if (product.equals(value)) {
                this.selectedProductList.remove(i);
                break;
            }
        }
    }

    public int totalBalance() {
        int total = 0;
        for (int d : this.denominationList) {
            int currentAmount = this.getDenominationCount(d);
            total += d * currentAmount;
        }
        return total;
    }

    public String selectedProductsInfo() {
        String ret = "";
        for (String product : this.selectedProductList) {
            int currentAmount = this.getProductCount(product);
            ret = ret + product + ": " + currentAmount + "\n";
        }
        if (ret.length() == 0) {
            ret = "Empty!\n";
        }
        return ret;
    }

    public int getDenominationCount(int denomination) {
        return this.denominationCount.containsKey(denomination) ? this.denominationCount.get(denomination) : 0;
    }

    public int getProductCount(String product) {
        return this.selectedProducts.containsKey(product) ? this.selectedProducts.get(product) : 0;
    }
}
