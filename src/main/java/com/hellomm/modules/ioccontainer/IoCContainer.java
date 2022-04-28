package com.hellomm.modules.ioccontainer;

import com.hellomm.modules.iohelper.IOHelperService;
import com.hellomm.modules.statemachine.StateMachineService;
import com.hellomm.modules.vendingmachine.VendingMachineService;

public class IoCContainer {
    private VendingMachineService vendingMachineService;
    private IOHelperService ioHelperService;
    private StateMachineService stateMachineService;

    public IoCContainer() {
        ioHelperService = new IOHelperService();
        stateMachineService = new StateMachineService();
        vendingMachineService = new VendingMachineService(ioHelperService, stateMachineService);
    }

    public VendingMachineService getVendingMachineService() {
        return vendingMachineService;
    }

}
