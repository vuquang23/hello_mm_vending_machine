package com.hellomm.modules.vendingmachine.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.hellomm.common.constants.ConstantsUtil;
import com.hellomm.common.exceptions.CannotPayBackException;
import com.hellomm.common.exceptions.InvalidDenominationException;
import com.hellomm.common.exceptions.InvalidProductException;
import com.hellomm.modules.iohelper.IOHelperService;

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

    /**
     * Init mock
     */
    private void initMock() {
        // cash in machine
        this.denominationList = new ArrayList<>(Arrays.asList(1, 2, 5, 10, 20, 50, 100, 200));
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

        // TODO: test exceed product amount
        // this.productsCount.put("SODA", 0);
    }

    /**
     * Admin insert cash
     * @param denomination int
     * @param amount int
     */
    public void adminInsertCash(int denomination, int amount) {
        int currentAmount = this.getDenominationCount(denomination);
        this.denominationCount.put(denomination, currentAmount + amount);
    }

    /**
     * Customer insert cash
     * @param denomination int
     * @throws Exception
     */
    public void customerInsertCash(int denomination) throws Exception {
        if (!Store.validDenomination(denomination)) {
            throw new InvalidDenominationException();
        }
        this.adminInsertCash(denomination, 1);
    }

    /**
     * Check if a valid denomination for customer
     * @param denomination int
     * @return Boolean
     */
    public static boolean validDenomination(int denomination) {
        for (int value : ConstantsUtil.ACCEPTED_DENOMINATIONS) {
            if (denomination == value) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if a valid denomination for admin
     * @param denomination
     * @return
     */
    public static boolean validDenominationForAdmin(int denomination) {
        for (int value : ConstantsUtil.ACCEPTED_DENOMINATIONS_FOR_ADMIN) {
            if (denomination == value) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if a valid product name
     * @param product String
     * @return Boolean
     */
    public static boolean validProduct(String product) {
        for (String value : ConstantsUtil.PRODUCTS) {
            if (product.equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Deduct cash from machine, pay cash to customer
     * @param cashList ArrayList
     * @throws Exception
     */
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
        }
    }

    /**
     * Admin add product to machine
     * @param product String
     * @param amount int
     * @throws Exception
     */
    public void adminAddProduct(String product, int amount) throws Exception {
        if (!Store.validProduct(product)) {
            throw new InvalidProductException();
        }
        int currentAmount = this.getProductCount(product);
        this.productsCount.put(product, currentAmount + amount);
    }

    /**
     * Deduct product from machine, return product for customer
     * @param selectedProducts ArrayList
     */
    public void returnProductsForCustomer(ArrayList<ImmutablePair<String, Integer>> selectedProducts) {
        for (ImmutablePair<String, Integer> p : selectedProducts) {
            String product = p.getLeft();
            int amount = p.getRight();
            int currentAmount = this.getProductCount(product);
            this.productsCount.put(product, currentAmount - amount);
        }
    }

    /**
     * Deduct product from machine, return product for customer
     * @param customerCart CustomerCart
     */
    public void returnProductsForCustomer(CustomerCart customerCart) {
        ArrayList<String> selectedProductList = customerCart.getSelectedProductList();
        for (String product : selectedProductList) {
            int amount = customerCart.getProductCount(product);
            int currentAmount = this.getProductCount(product);
            this.productsCount.put(product, currentAmount - amount);
        }
    }

    /**
     * Get total balance in machine
     * @return int
     */
    public int getTotalBalance() {
        int total = 0;
        for (int d : this.denominationList) {
            int amount = this.denominationCount.get(d);
            total += amount * d;
        }
        return total;
    }

    /**
     * Get number of a product in machine
     * @param product String
     * @return int
     */
    public int getProductCount(String product) {
        return this.productsCount.containsKey(product) ? this.productsCount.get(product) : 0;
    }

    /**
     * Get number of a denomination in machine
     * @param denomination int
     * @return int
     */
    public int getDenominationCount(int denomination) {
        return this.denominationCount.containsKey(denomination) ? this.denominationCount.get(denomination) : 0;
    }

    /**
     * Return int[] array of cash in machine to calculate DP
     * @return ImmutablePair
     */
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

    /**
     * View cash info in machine
     * @return String
     */
    public String viewCashInfo() {
        String info = "";
        int total = 0;
        for (int d : this.denominationList) {
            int amount = this.getDenominationCount(d);
            info = info + IOHelperService.toVnd(d) + " vnd: " + amount + "\n";
            total += amount * d;
        }
        info = info + "Total: " + IOHelperService.toVnd(total) + " vnd";
        return info;
    }

    /**
     * View item info in machine
     * @return String
     */
    public String viewItemInfo() {
        String info = "";
        int total = 0;
        for (String product : this.productsCount.keySet()) {
            int amount = this.getProductCount(product);
            info = info + product + ": " + amount + "\n";
            total += amount;
        }
        info = info + "Total: " + total;
        return info;
    }
}
