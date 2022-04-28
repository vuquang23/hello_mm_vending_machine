package com.hellomm.modules.ioccontainer;

import com.hellomm.modules.iohelper.IOHelperService;
import com.hellomm.modules.vendingmachine.VendingMachineService;

public class IoCContainer {
    private VendingMachineService vendingMachineService;
    private IOHelperService ioHelperService;

    public IoCContainer() {
        ioHelperService = new IOHelperService();
        vendingMachineService = new VendingMachineService(ioHelperService);
    }

    public VendingMachineService getVendingMachineService() {
        return vendingMachineService;
    }

}
