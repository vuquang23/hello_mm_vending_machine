package com.hellomm.common.constants;

import java.util.ArrayList;
import java.util.Arrays;

import com.hellomm.common.exceptions.InvalidProductException;
import com.hellomm.modules.iohelper.IOHelperService;

public class ConstantsUtil {
    public static final ArrayList<String> PRODUCTS = new ArrayList<>(Arrays.asList("COKE", "PEPSI", "SODA"));

    public static final ArrayList<Integer> ACCEPTED_DENOMINATIONS = new ArrayList<>(
            Arrays.asList(
                    IOHelperService.compress(10000),
                    IOHelperService.compress(20000),
                    IOHelperService.compress(50000),
                    IOHelperService.compress(100000),
                    IOHelperService.compress(200000)));

    public static final ArrayList<Integer> ACCEPTED_DENOMINATIONS_FOR_ADMIN = new ArrayList<>(
            Arrays.asList(
                    IOHelperService.compress(1000),
                    IOHelperService.compress(2000),
                    IOHelperService.compress(5000),
                    IOHelperService.compress(10000),
                    IOHelperService.compress(20000),
                    IOHelperService.compress(50000),
                    IOHelperService.compress(100000),
                    IOHelperService.compress(200000)));

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
