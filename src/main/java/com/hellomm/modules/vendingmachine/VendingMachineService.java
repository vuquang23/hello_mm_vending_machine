package com.hellomm.modules.vendingmachine;

import com.hellomm.common.enums.StateEnum;
import com.hellomm.common.exceptions.CannotPayBackException;
import com.hellomm.common.exceptions.NotEnoughPaidException;

import java.util.ArrayList;
import java.util.HashMap;

import com.hellomm.common.enums.AdminActionEnum;
import com.hellomm.common.enums.CustomerActionEnum;
import com.hellomm.modules.iohelper.IOHelperService;
import com.hellomm.modules.vendingmachine.components.CustomerCart;
import com.hellomm.modules.vendingmachine.components.Store;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class VendingMachineService {
    private IOHelperService ioHelperService;

    private Store store;
    private CustomerCart customerCart;

    private StateEnum machineState;

    public VendingMachineService(IOHelperService ioHelperService) {
        this.ioHelperService = ioHelperService;

        this.store = new Store();

        this.setState(StateEnum.READY);
    }

    public void start() {
        ioHelperService.clrscr();
        for (;;) {
            try {
                switch (this.machineState) {
                    case READY:
                        this.ready();
                        break;
                    case ADMIN_WAIT:
                        this.adminWait();
                        break;
                    case CUSTOMER_WAIT:
                        this.customerWait();
                        break;
                    default:
                        this.ioHelperService.error("Invalid machine state.");
                        System.exit(0);
                }
            } catch (Exception e) {
                this.ioHelperService.error(e);
            }
        }
    }

    private void ready() {
        this.ioHelperService.readyStateOptions();
        StateEnum nxtState = this.ioHelperService.readyStateSelect();
        this.setState(nxtState);
    }

    private void adminWait() throws Exception {
        this.ioHelperService.adminWaitStateOptions();

        Pair<StateEnum, AdminActionEnum> result = this.ioHelperService.adminWaitStateSelect();
        StateEnum nxtState = result.getLeft();
        AdminActionEnum action = result.getRight();

        try {
            switch (action) {
                case VIEW_CASH_INFO:
                    this.adminViewCashInfo();
                    break;
                case ADD_CASH:
                    this.adminAddCash();
                    break;
                case VIEW_ITEM_INFO:
                    this.adminViewItemInfo();
                    break;
                case ADD_ITEM:
                    this.adminAddItem();
                    break;
                case CANCEL:
                    this.adminCancel();
                    break;
                default:
                    throw new Error("Invalid action.");
            }
            this.setState(nxtState);
        } catch (Exception e) {
            this.ioHelperService.clrscr();
            this.ioHelperService.error(e);
            Thread.sleep(2500);
        }
    }

    private void adminViewCashInfo() {
        this.ioHelperService.adminViewCashInfo(store);
    }

    private void adminAddCash() {
        Pair<Integer, Integer> result = this.ioHelperService.adminAddCash();
        if (result == null) {
            return;
        }
        this.store.adminInsertCash(result.getLeft(), result.getRight());
    }

    private void adminViewItemInfo() {
        this.ioHelperService.adminViewItemInfo(store);
    }

    private void adminAddItem() throws Exception {
        Pair<String, Integer> result = this.ioHelperService.adminAddItem();
        this.store.adminAddProduct(result.getLeft(), result.getRight());
    }

    private void adminCancel() {
        // Do nothing.
    }

    private void customerWait() throws Exception {
        this.createCartIfNewTransaction();

        this.ioHelperService.printCustomerCartInfo(customerCart);
        this.ioHelperService.customerWaitStateOptions();

        Pair<StateEnum, CustomerActionEnum> result = this.ioHelperService.customerWaitStateSelect();
        StateEnum nxtState = result.getLeft();
        CustomerActionEnum action = result.getRight();

        try {
            switch (action) {
                case INSERT_CASH:
                    this.customerInsertCash();
                    break;
                case SELECT_ITEM:
                    this.customerSelectItem();
                    break;
                case UNSELECT_ITEM:
                    this.customerUnselectItem();
                    break;
                case TRANSACT:
                    this.customerMakeTx();
                    break;
                case CANCEL:
                    this.customerCancelTx();
                    break;
                default:
                    throw new Exception("Invalid action.");
            }

            this.setState(nxtState);
        } catch (Exception e) {
            this.ioHelperService.clrscr();
            this.ioHelperService.error(e);
            Thread.sleep(2500);
        }
    }

    private void customerInsertCash() throws Exception {
        int denomination = this.ioHelperService.customerInsertCash();
        if (denomination == -1) {
            return;
        }
        this.store.customerInsertCash(denomination);
        this.customerCart.insertCash(denomination);
    }

    private void customerSelectItem() throws Exception {
        String product = this.ioHelperService.customerSelectProduct();
        if (product.equals("-1")) {
            return;
        }
        this.customerCart.selectProduct(this.store, product);
    }

    private void customerUnselectItem() throws Exception {
        String product = this.ioHelperService.customerSelectProduct();
        if (product.equals("-1")) {
            return;
        }
        this.customerCart.unselectProduct(product);
    }

    private void customerMakeTx() throws Exception {
        ImmutablePair<Boolean, ArrayList<ImmutablePair<Integer, Integer>>> changeCalc = this
                .changeCalculate();
        if (!changeCalc.getLeft()) {
            throw new CannotPayBackException();
        }
        this.store.returnCashForCustomer(changeCalc.getRight());
        this.store.returnProductsForCustomer(customerCart);
        this.ioHelperService.returnCashAndProductsForCustomer(changeCalc.getRight(), this.customerCart);
        this.destroyCustomerCart();
    }

    private void customerCancelTx() {
        this.ioHelperService.customerCancelTx(customerCart);
        this.destroyCustomerCart();
    }

    private void createCartIfNewTransaction() {
        if (this.customerCart == null) {
            this.customerCart = new CustomerCart();
        }
    }

    private void setState(StateEnum nxtState) {
        this.machineState = nxtState;
    }

    private void destroyCustomerCart() {
        this.customerCart = null;
    }

    public ImmutablePair<Boolean, ArrayList<ImmutablePair<Integer, Integer>>> changeCalculate() throws Exception {
        int change = this.customerCart.getChange();
        if (change < 0) {
            throw new NotEnoughPaidException();
        }
        // 1m vnd.
        int MAX = 1005;

        boolean[] dp = new boolean[MAX];
        dp[0] = true;
        int[] trace = new int[MAX];

        ImmutablePair<int[], Integer> cashFlatten = this.store.cashFlatten();
        int[] cash = cashFlatten.getLeft();
        int n = cashFlatten.getRight();

        for (int i = 1; i <= n; ++i) {
            int val = cash[i];
            for (int j = change; j >= 0; --j) {
                if (j + val <= change && dp[j] && trace[j + val] < val) {
                    dp[j + val] = true;
                    trace[j + val] = val;
                }
            }
        }

        if (!dp[change]) {
            return new ImmutablePair<>(false, null);
        }

        ArrayList<ImmutablePair<Integer, Integer>> traceRes = new ArrayList<>();
        HashMap<Integer, Integer> changeDenominationCount = new HashMap<>();

        while (change > 0) {
            int val = trace[change];
            int cnt = changeDenominationCount.containsKey(val) ? changeDenominationCount.get(val) : 0;
            changeDenominationCount.put(val, cnt + 1);
            change -= val;
        }

        for (int k : changeDenominationCount.keySet()) {
            int cnt = changeDenominationCount.get(k);
            traceRes.add(new ImmutablePair<>(k, cnt));
        }

        return new ImmutablePair<>(true, traceRes);
    }
}
