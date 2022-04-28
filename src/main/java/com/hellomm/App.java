package com.hellomm;

import com.hellomm.modules.ioccontainer.IoCContainer;
import com.hellomm.modules.vendingmachine.VendingMachineService;

public class App {
    public static void main(String[] args) {
        IoCContainer ioCContainer = new IoCContainer();
        VendingMachineService vendingMachineService = ioCContainer.getVendingMachineService();
        vendingMachineService.start();
    }
}
