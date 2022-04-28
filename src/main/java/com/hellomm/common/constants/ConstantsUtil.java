package com.hellomm.common.constants;

import java.util.ArrayList;
import java.util.Arrays;

import com.hellomm.common.exceptions.InvalidProductException;
import com.hellomm.modules.iohelper.IOHelperService;

public class ConstantsUtil {
    public static final ArrayList<String> PRODUCTS = new ArrayList<>(Arrays.asList("COKE", "PEPSI", "SODA"));

    public static final ArrayList<Integer> ACCEPTED_DENOMINATIONS = new ArrayList<>(
            Arrays.asList(10, 20, 50, 100, 200));

    public static int getPrice(String product) throws Exception {
        switch (product) {
            case "COKE":
                return IOHelperService.compress(10000); 
            case "PEPSI":
                return IOHelperService.compress(10000);
            case "SODA":
                return IOHelperService.compress(20000);
            default:
                throw new InvalidProductException();
        }
    }
}
