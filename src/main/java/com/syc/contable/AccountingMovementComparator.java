package com.syc.contable;

import java.util.Comparator;

public class AccountingMovementComparator implements Comparator<AccountingMovement> {

	public int compare(AccountingMovement movement0, AccountingMovement movement1) {
		return movement0.getPK().compareTo(movement1.getPK());
	}
}