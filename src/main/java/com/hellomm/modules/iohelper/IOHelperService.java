package com.hellomm.modules.iohelper;

import java.util.ArrayList;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.hellomm.common.enums.AdminActionEnum;
import com.hellomm.common.enums.CustomerActionEnum;
import com.hellomm.common.enums.StateEnum;
import com.hellomm.common.exceptions.InvalidDenominationException;
import com.hellomm.common.exceptions.InvalidProductException;
import com.hellomm.modules.vendingmachine.components.CustomerCart;
import com.hellomm.modules.vendingmachine.components.Store;

public class IOHelperService {
    public static final String RESET = "\u001B[0m";
    public static final String GREEN = "\033[1;32m";
    public static final String RED = "\033[1;31m";

    public static final int READY_STATE_OPTIONS = 2;
    public static final int ADMIN_WAIT_STATE_OPTIONS = 2;
    public static final int CUSTOMER_WAIT_STATE_OPTIONS = 2;

    private Scanner scanner;

    public IOHelperService() {
        this.scanner = new Scanner(System.in);
    }

    public <T> void log(T message) {
        System.out.println(GREEN + message + RESET);
    }

    public <T> void error(T message) {
        System.out.println(RED + message + RESET);
    }

    public void error(Exception e) {
        System.out.println(RED + e.getMessage() + RESET);
    }

    public void clrscr() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void readyStateOptions() {
        this.clrscr();
        this.log("Options:");
        this.log("1. Admin role");
        this.log("2. Customer role");
        this.log("3. Stop Machine");
    }

    public StateEnum readyStateSelect() {
        this.log("\nPlease select:");

        for (;;) {
            String optionSelected = this.scanner.nextLine();
            if (!StringUtils.isNumeric(optionSelected)) {
                this.error("Invalid input. Try again!");
                continue;
            }

            int optionSelectedNum = Integer.parseInt(optionSelected);
            switch (optionSelectedNum) {
                case 1:
                    return StateEnum.ADMIN_WAIT;
                case 2:
                    return StateEnum.CUSTOMER_WAIT;
                case 3:
                    this.clrscr();
                    this.log("Bye!\n");
                    System.exit(0);
                default:
                    this.error("Invalid input. Try again!");
            }
        }
    }

    public Pair<StateEnum, CustomerActionEnum> customerWaitStateSelect() {
        this.log("\nPlease select:");

        for (;;) {
            String optionSelected = this.scanner.nextLine();
            if (!StringUtils.isNumeric(optionSelected)) {
                this.error("Invalid input. Try again!");
                continue;
            }
            int optionSelectedNum = Integer.parseInt(optionSelected);

            switch (optionSelectedNum) {
                case 1:
                    return new ImmutablePair<>(StateEnum.CUSTOMER_WAIT, CustomerActionEnum.INSERT_CASH);
                case 2:
                    return new ImmutablePair<>(StateEnum.CUSTOMER_WAIT, CustomerActionEnum.SELECT_ITEM);
                case 3:
                    return new ImmutablePair<>(StateEnum.CUSTOMER_WAIT, CustomerActionEnum.UNSELECT_ITEM);
                case 4:
                    return new ImmutablePair<>(StateEnum.READY, CustomerActionEnum.TRANSACT);
                case 5:
                    return new ImmutablePair<>(StateEnum.READY, CustomerActionEnum.CANCEL);
                default:
                    this.error("Invalid input. Try again!");
            }
        }
    }

    public Pair<StateEnum, AdminActionEnum> adminWaitStateSelect() {
        this.log("\nPlease select:");

        for (;;) {
            String optionSelected = this.scanner.nextLine();
            if (!StringUtils.isNumeric(optionSelected)) {
                this.error("Invalid input. Try again!");
                continue;
            }
            int optionSelectedNum = Integer.parseInt(optionSelected);

            switch (optionSelectedNum) {
                case 1:
                    return new ImmutablePair<>(StateEnum.ADMIN_WAIT, AdminActionEnum.VIEW_CASH_INFO);
                case 2:
                    return new ImmutablePair<>(StateEnum.ADMIN_WAIT, AdminActionEnum.ADD_CASH);
                case 3:
                    return new ImmutablePair<>(StateEnum.ADMIN_WAIT, AdminActionEnum.VIEW_ITEM_INFO);
                case 4:
                    return new ImmutablePair<>(StateEnum.ADMIN_WAIT, AdminActionEnum.ADD_ITEM);
                case 5:
                    return new ImmutablePair<>(StateEnum.READY, AdminActionEnum.CANCEL);
                default:
                    this.error("Invalid input. Try again!");
            }
        }
    }

    public void adminWaitStateOptions() {
        this.clrscr();
        this.log("Options:");
        this.log("1. View cash info");
        this.log("2. Add cash");
        this.log("3. View item info");
        this.log("4. Add item");
        this.log("5. Cancel");
    }

    public void customerWaitStateOptions() {
        this.log("Options:");
        this.log("1. Insert cash");
        this.log("2. Select item");
        this.log("3. Unselect item");
        this.log("4. Transact");
        this.log("5. Cancel");
    }

    public int customerInsertCash() {
        this.clrscr();
        this.log("Insert a cash: (x to go back).");

        for (;;) {
            String cash = this.scanner.nextLine();
            if (cash.equals("x")) {
                return -1;
            }
            if (!StringUtils.isNumeric(cash)) {
                this.error(new InvalidDenominationException());
                continue;
            }
            int cashInt = Integer.parseInt(cash);
            if (cashInt % 1000 != 0 || !Store.validDenomination(IOHelperService.compress(cashInt))) {
                this.error(new InvalidDenominationException());
                continue;
            }
            return IOHelperService.compress(cashInt);
        }
    }

    public String customerSelectProduct() {
        this.clrscr();
        this.log("Choose a product: (x to go back).");

        for (;;) {
            String product = this.scanner.nextLine();
            if (product.equals("x")) {
                return "-1";
            }
            if (!Store.validProduct(product)) {
                this.error(new InvalidProductException());
                continue;
            }
            return product;
        }
    }

    public void printCustomerCartInfo(CustomerCart customerCart) {
        this.clrscr();
        this.log("Your balance:");
        this.log(IOHelperService.toVnd(customerCart.totalBalance()) + " vnd.");
        this.log("Products in cart:");
        this.log(customerCart.selectedProductsInfo());
    }

    public static long toVnd(int balance) {
        return balance * 1000;
    }

    public static int compress(long balance) {
        return (int) balance / 1000;
    }

    public void customerCancelTx(CustomerCart customerCart) {
        String insertCashHistoryInfo = customerCart.insertCashHistoryInfo();
        if (insertCashHistoryInfo.length() == 0) {
            return;
        }
        this.clrscr();
        this.log("You receive back:");
        this.log(insertCashHistoryInfo);
        this.log("\nPress enter key to continue");
        this.scanner.nextLine();
    }

    public void returnCashAndProductsForCustomer(ArrayList<ImmutablePair<Integer, Integer>> traceRes,
            CustomerCart customerCart) {
        this.clrscr();
        this.log("You bought:");
        this.log(customerCart.selectedProductsInfo());

        String change = "";
        int totalChange = 0;
        for (ImmutablePair<Integer, Integer> p : traceRes) {
            int denomination = p.getLeft();
            int amount = p.getRight();
            change = change + IOHelperService.toVnd(denomination) + " vnd: " + amount + "\n";
            totalChange += denomination * amount;
        }
        if (change.length() > 0) {
            this.log("\nYour change:");
            this.log(change);
            this.log("Total:");
            this.log(IOHelperService.toVnd(totalChange) + " vnd");
        }
        this.log("\nPress enter key to continue");
        this.scanner.nextLine();
    }

    public void adminViewCashInfo(Store store) {
        this.clrscr();
        String info = store.viewCashInfo();
        this.log("Cash in machine:");
        this.log(info);
        this.log("\nPress enter key to continue");
        this.scanner.nextLine();
    }

    public Pair<Integer, Integer> adminAddCash() {
        this.clrscr();
        this.log("Insert cash: (x to go back).");
        int cashDenomination, cashAmount;
        for (;;) {
            String cash = this.scanner.nextLine();
            if (cash.equals("x")) {
                return null;
            }
            if (!StringUtils.isNumeric(cash)) {
                this.error(new InvalidDenominationException());
                continue;
            }
            cashDenomination = Integer.parseInt(cash);
            if (cashDenomination % 1000 != 0
                    || !Store.validDenominationForAdmin(IOHelperService.compress(cashDenomination))) {
                this.error(new InvalidDenominationException());
                continue;
            }
            cashDenomination = IOHelperService.compress(cashDenomination);
            break;
        }
        this.log("Amount: (x to go back).");
        for (;;) {
            String amount = this.scanner.nextLine();
            if (amount.equals("x")) {
                return null;
            }
            if (!StringUtils.isNumeric(amount)) {
                this.error("Invalid amount of denomination.");
                continue;
            }
            cashAmount = Integer.parseInt(amount);
            break;
        }
        return new ImmutablePair<Integer, Integer>(cashDenomination, cashAmount);
    }

    public void adminViewItemInfo(Store store) {
        this.clrscr();
        String info = store.viewItemInfo();
        this.log("Products in machine:");
        this.log(info);
        this.log("\nPress enter key to continue");
        this.scanner.nextLine();
    }
}
