package com.syc.contable;

import java.util.Comparator;
import java.util.Base64;

public class AccountingMovementComparator implements Comparator<AccountingMovement> {

    public int compare(AccountingMovement movement0, AccountingMovement movement1) {
        return movement0.getPK().compareTo(movement1.getPK());
    }
}
