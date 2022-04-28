package com.hellomm.modules.vendingmachine.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.hellomm.common.constants.ConstantsUtil;
import com.hellomm.common.exceptions.CannotPayBackException;
import com.hellomm.common.exceptions.InvalidDenominationException;
import com.hellomm.common.exceptions.InvalidProductException;

import org.apache.commons.lang3.tuple.ImmutablePair;

public class Store {
    private HashMap<Integer, Integer> denominationCount;
    private ArrayList<Integer> denominationList;

    private HashMap<String, Integer> productsCount;

    public Store() {
        this.denominationCount = new HashMap<>();
        this.productsCount = new HashMap<>();

        this.initMock();
    }

    private void initMock() {
        // cash in machine
        this.denominationList = new ArrayList<>(Arrays.asList(1, 2, 5, 10, 100, 200, 500));
        this.denominationCount.put(1, 40);
        this.denominationCount.put(2, 40);
        this.denominationCount.put(5, 30);
        this.denominationCount.put(10, 30);
        this.denominationCount.put(50, 25);
        this.denominationCount.put(100, 20);
        this.denominationCount.put(200, 15);

        for (String product : ConstantsUtil.PRODUCTS) {
            this.productsCount.put(product, 99);
        }
    }

    public void adminInsertCash(int denomination, int amount) {
        int currentAmount = this.getDenominationCount(denomination);
        if (currentAmount == 0) {
            this.denominationList.add(denomination);
        }
        this.denominationCount.put(denomination, currentAmount + amount);
    }

    public void customerInsertCash(int denomination) throws Exception {
        if (!Store.validDenomination(denomination)) {
            throw new InvalidDenominationException();
        }
        this.adminInsertCash(denomination, 1);
    }

    public static boolean validDenomination(int denomination) {
        for (int value : ConstantsUtil.ACCEPTED_DENOMINATIONS) {
            if (denomination == value) {
                return true;
            }
        }

        return false;
    }

    public static boolean validProduct(String product) {
        for (String value : ConstantsUtil.PRODUCTS) {
            if (product.equals(value)) {
                return true;
            }
        }
        return false;
    }

    public void returnCashForCustomer(ArrayList<ImmutablePair<Integer, Integer>> cashList) throws Exception {
        for (ImmutablePair<Integer, Integer> p : cashList) {
            int denomination = p.getLeft();
            int currentAmount = this.getDenominationCount(denomination);
            int needToDecrease = p.getRight();
            if (currentAmount < needToDecrease) {
                throw new CannotPayBackException();
            }

            currentAmount -= needToDecrease;
            this.denominationCount.put(denomination, currentAmount);

            if (currentAmount == 0) {
                this.deleteFromDenominationList(denomination);
            }
        }
    }

    private void deleteFromDenominationList(int denomination) {
        for (int i = 0; i < this.denominationList.size(); ++i) {
            int d = this.denominationList.get(i);
            if (d == denomination) {
                this.denominationList.remove(i);
                break;
            }
        }
    }

    public void adminAddProduct(String product, int amount) throws Exception {
        if (!Store.validProduct(product)) {
            throw new InvalidProductException();
        }
        int currentAmount = this.getProductCount(product);
        this.productsCount.put(product, currentAmount + amount);
    }

    public void returnProductsForCustomer(ArrayList<ImmutablePair<String, Integer>> selectedProducts) {
        for (ImmutablePair<String, Integer> p : selectedProducts) {
            String product = p.getLeft();
            int amount = p.getRight();
            int currentAmount = this.getProductCount(product);
            this.productsCount.put(product, currentAmount - amount);
        }
    }

    public int getTotalBalance() {
        int total = 0;
        for (int d : this.denominationList) {
            int amount = this.denominationCount.get(d);
            total += amount * d;
        }
        return total;
    }

    public int getProductCount(String product) {
        return this.productsCount.containsKey(product) ? this.productsCount.get(product) : 0;
    }

    public int getDenominationCount(int denomination) {
        return this.denominationCount.containsKey(denomination) ? this.denominationCount.get(denomination) : 0;
    }

    public ImmutablePair<int[], Integer> cashFlatten() {
        int MAX = 1005;
        int[] cash = new int[MAX];
        int n = 0;

        for (int d : this.denominationList) {
            int currentAmount = this.getDenominationCount(d);
            for (int i = 0; i < currentAmount; ++i) {
                cash[++n] = d;
            }
        }
        
        return new ImmutablePair<>(cash, n);
    }
}
