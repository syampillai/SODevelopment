package com.storedobject.core;

import java.sql.ResultSet;

public final class AccountChart extends StoredObject implements HasParents, HasChildren {

	private static final String[] categoryValues = new String[] {
			"Balance Sheet", "Profit & Loss", "Stock", "Contingent"
	};
	private static final String[] balanceTypeValues = new String[] {
			"Debit", "Credit"
	};
	private static final String[] transactionTypeValues = new String[] {
			"All Types Allowed", "Generally Debited", "Generally Credited", "Temporarily Frozen"
	};
	private String name;
	private int category, balanceType, transactionType;
	private boolean strictBalanceControl, deepFrozen, limitCheck, accountsAllowed = true;
	private int status = -1;

	public AccountChart() {
	}

	public static void columns(Columns columns) {
		columns.add("Name", "text");
		columns.add("Category", "int");
		columns.add("BalanceType", "int");
		columns.add("TransactionType", "int");
		columns.add("StrictBalanceControl", "boolean");
		columns.add("DeepFrozen", "boolean");
		columns.add("LimitCheck", "boolean");
		columns.add("AccountsAllowed", "boolean");
	}

	public static void indices(Indices indices) {
		indices.add("lower(name)", true);
	}

	public static String[] displayColumns() {
		return new String[] { "Name" };
	}

	public static String[] browseColumns() {
		return new String[] { "Name", "Category", "BalanceType", "LimitCheck", "AccountsAllowed" };
	}

	public static int hints() {
		return ObjectHint.SMALL_LIST;
	}

	public static String[] links() {
		return new String[] { "Children" };
	}

	public int getBalanceType() {
		return balanceType;
	}

	public void setBalanceType(int balanceType) {
		this.balanceType = balanceType % balanceTypeValues.length;
	}

	public int getTransactionType() {
		return transactionType;
	}

	public void setTransactionType(int transactionType) {
		this.transactionType = transactionType % transactionTypeValues.length;
	}

	public boolean getStrictBalanceControl() {
		return strictBalanceControl;
	}

	public void setStrictBalanceControl(boolean strictBalanceControl) {
		this.strictBalanceControl = strictBalanceControl;
	}

	public boolean getDeepFrozen() {
		return deepFrozen;
	}

	public void setDeepFrozen(boolean deepFrozen) {
		this.deepFrozen = deepFrozen;
	}

	public boolean getLimitCheck() {
		return limitCheck;
	}

	public void setLimitCheck(boolean limitCheck) {
		this.limitCheck = limitCheck;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getStatus(int accountStatus) {
		return 0;
	}

	public static String[] getCategoryValues() {
		return categoryValues;
	}

	public void setCategory(int category) {
		this.category = category % categoryValues.length;
	}

	public int getCategory() {
		return category;
	}

	public String getCategoryValue() {
		return categoryValues[category];
	}

	public static String getCategoryValue(int category) {
		return categoryValues[category % categoryValues.length];
	}

	public static String[] getBalanceTypeValues() {
		return balanceTypeValues;
	}

	public String getBalanceTypeValue() {
		return balanceTypeValues[this.balanceType];
	}

	public static String getBalanceTypeValue(int balanceType) {
		return balanceTypeValues[balanceType % balanceTypeValues.length];
	}

	public static String[] getTransactionTypeValues() {
		return transactionTypeValues;
	}

	public String getTransactionTypeValue() {
		return transactionTypeValues[this.transactionType];
	}

	public static String getTransactionTypeValue(int transactionType) {
		return transactionTypeValues[transactionType % transactionTypeValues.length];
	}

	public String toString() {
		return name;
	}

	@Override
	public void validateData(TransactionManager tm) throws Exception {
		super.validateData(tm);
		if(accountsAllowed) {
			return;
		}
		Query q = query(getTransaction(), Account.class, "Number", "Chart=" + getId(), null, true);
		StringBuilder sb = new StringBuilder();
		int n = 0;
		try {
			for(ResultSet rs: q) {
				if(sb.length() > 35) {
					sb.append(", ...");
					break;
				}
				if(n == 0) {
					sb.append("This chart is already in use: Account Number = ");
				} else {
					sb.append(", ");
				}
				sb.append(rs.getString(1));
				++n;
			}
		} finally {
			q.close();
		}
		if(!sb.isEmpty()) {
			throw new Invalid_State(sb.toString());
		}
	}

	@Override
	public void validateChildAttach(StoredObject child, int type) throws Exception {
		if(type != 0) {
			return;
		}
		if(child instanceof AccountChart c) {
			if(c.category != category) {
				throw new Invalid_State("Children must belong to '" + getCategoryValue() + "' category");
			}
		}
	}

	@Override
	public void validateParentAttach(StoredObject parent, int type) throws Exception {
		if(type != 0) {
			return;
		}
		if(parent instanceof AccountChart p) {
			if(p.category != category) {
				throw new Invalid_State("Parent must belong to '" + getCategoryValue() + "' category");
			}
		}
	}

	public int getStatus() {
		return status;
	}

	public String getStatusDescription() {
		return Account.getStatusDescription(status);
	}

	private void status() {
		status = transactionType;
		status <<= 1;
		status |= (limitCheck ? 1 : 0);
		status <<= 1;
		status |= (deepFrozen ? 1 : 0);
		status <<= 2;
		status |= category;
		status <<= 1;
		status |= balanceType;
		status <<= 1;
		status |= (strictBalanceControl ? 1 : 0);
		status <<= 3;
	}

	@Override
	public void loaded() {
		status();
	}

	public void setAccountsAllowed(boolean accountsAllowed) {
		this.accountsAllowed = accountsAllowed;
	}

	public boolean getAccountsAllowed() {
		return accountsAllowed;
	}

	public static void set(Account account, TransactionManager tm) {
		set(account, StringUtility.makeLabel(account.getClass()), tm);
	}

	public static void set(Account account, String chartName, TransactionManager tm) {
		AccountChartMap acm =get(AccountChartMap.class, "lower(AccountClassName)='"
				+ account.getClass().getName().toLowerCase() + "'");
		if(acm != null) {
			account.setChart(acm.getChartId());
		} else {
			acm = new AccountChartMap();
			acm.setAccountClassName(account.getClass().getName());
			AccountChart ac = get(AccountChart.class, "lower(Name)='" + chartName.toLowerCase() + "'");
			if(ac == null) {
				ac = new AccountChart();
				ac.setName(chartName);
				if(chartName.toLowerCase().contains("cash") || account.getClass().getName().contains("Cash")) {
					ac.setStrictBalanceControl(true);
				}
				try {
					tm.transact(ac::save);
				} catch (Exception e) {
					return;
				}
			}
			account.setChart(ac);
			acm.setChart(ac);
			try {
				tm.transact(acm::save);
			} catch (Exception ignored) {
			}
		}
	}
}
