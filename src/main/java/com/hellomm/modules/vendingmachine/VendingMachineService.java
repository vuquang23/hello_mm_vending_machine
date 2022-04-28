package com.hellomm.modules.vendingmachine;

import com.hellomm.common.enums.StateEnum;
import com.hellomm.common.enums.CustomerActionEnum;
import com.hellomm.modules.iohelper.IOHelperService;
import com.hellomm.modules.statemachine.StateMachineService;
import com.hellomm.modules.vendingmachine.components.CustomerCart;
import com.hellomm.modules.vendingmachine.components.Store;

import org.apache.commons.lang3.tuple.Pair;

public class VendingMachineService {
    private IOHelperService ioHelperService;
    private StateMachineService stateMachineService;

    private Store store;
    private CustomerCart customerCart;

    private StateEnum machineState;

    public VendingMachineService(IOHelperService ioHelperService, StateMachineService stateMachineService) {
        this.ioHelperService = ioHelperService;
        this.stateMachineService = stateMachineService;

        this.store = new Store();

        this.setState(StateEnum.READY);
    }

    public void start() {
        ioHelperService.clrscr();
        ioHelperService.log("Vending machine has started!\n");

        for (;;) {
            try {
                switch (this.machineState) {
                    case READY:
                        this.ready();
                        break;
                    case ADMIN_WAIT:
                        // this.ioHelperService.adminWaitStateOptions();

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

    private void customerWait() throws Exception {
        this.createCartIfNewTransaction();

        this.ioHelperService.printCustomerCartInfo(customerCart);
        this.ioHelperService.customerWaitStateOptions();

        Pair<StateEnum, CustomerActionEnum> result = this.ioHelperService.customerWaitStateSelect();
        StateEnum nxtState = result.getLeft();
        CustomerActionEnum action = result.getRight();

        try {
            switch (action) {
                case INSERT_CASH: {
                    int denomination = this.ioHelperService.customerInsertCash();
                    if (denomination == -1) {
                        this.setState(StateEnum.CUSTOMER_WAIT);
                        return;
                    }
                    this.store.customerInsertCash(denomination);
                    this.customerCart.insertCash(denomination);
                    break;
                }

                case SELECT_ITEM: {
                    String product = this.ioHelperService.customerSelectProduct();
                    if (product.equals("-1")) {
                        this.setState(StateEnum.CUSTOMER_WAIT);
                        return;
                    }
                    this.customerCart.selectProduct(this.store, product);
                    break;
                }

                case UNSELECT_ITEM: {
                    String product = this.ioHelperService.customerSelectProduct();
                    if (product.equals("-1")) {
                        this.setState(StateEnum.CUSTOMER_WAIT);
                        return;
                    }
                    this.customerCart.unselectProduct(product);
                    break;
                }

                case TRANSACT: {

                    break;
                }

                case CANCEL: {
                    this.ioHelperService.customerCancelTx(customerCart);
                    this.destroyCustomerCart();
                    break;
                }

                default:
                    throw new Exception("Invalid action.");

            }

            this.setState(nxtState);
        } catch (Exception e) {
            this.ioHelperService.error(e);
            Thread.sleep(2000);
        }
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


}
