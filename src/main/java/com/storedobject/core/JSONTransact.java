package com.storedobject.core;

import com.storedobject.common.Fault;
import com.storedobject.common.SORuntimeException;

import java.sql.Date;

public class JSONTransact implements JSONService {

    private final JournalVoucher jv = new JournalVoucher();
    private boolean error = false;

    @Override
    public void execute(Device device, JSON json, JSONMap result) {
        if(error) {
            result.error("Error occurred in the previous call");
            return;
        }
        error = true; // Assume error by default
        String action = json.getString("action");
        if(action == null) {
            result.error(Fault.ACTION_NOT_SPECIFIED);
            return;
        }
        switch (action) {
            case "exists" -> {
                if(origin(json, result, true)) {
                    error = false;
                }
            }
            case "credit", "debit", "commit" -> {
                switch (action) {
                    case "credit", "debit" -> {
                        Boolean preserve = json.getBoolean("continue");
                        if(preserve == null || !preserve) {
                            result.error("Transaction leg specified with the intent not to continue");
                            return;
                        }
                        Account account = json.getAccount("account");
                        if(account == null) {
                            account = getAccount(device, json.getString("account"));
                        }
                        if(account == null) {
                            result.error(Fault.ACCOUNT_NOT_FOUND);
                            return;
                        }
                        Money amount = json.getMoney("amount");
                        if(amount == null) {
                            result.error(Fault.INVALID_AMOUNT);
                            return;
                        }
                        Money lcAmount = json.getMoney("lcAmount");
                        if("debit".equals(action)) {
                            amount = amount.negate();
                            if(lcAmount != null) {
                                lcAmount = lcAmount.negate();
                            }
                        }
                        Integer serial = json.getInteger("serial");
                        try {
                            jv.credit(account, amount, lcAmount, serial == null ? 0 : serial,
                                    json.getString("type"),
                                    json.getString("narration"),
                                    json.getDate("valueDate"));
                            entryCreated(device, jv, json);
                            error = false;
                        } catch (SOException | SORuntimeException soe) {
                            result.error(Fault.TECHNICAL_FAULT.replace(soe.getEndUserMessage()));
                        } catch (Exception e) {
                            device.log(e);
                            result.error(Fault.TECHNICAL_FAULT);
                        }
                    }
                    default -> { // Commit
                        if(jv.getEntryCount() == 0) {
                            result.error(Fault.NO_ENTRIES_FOUND);
                            return;
                        }
                        if(!validateWorkingDate(device, jv, json.getDate())) {
                            result.error(Fault.INVALID_DATE);
                            return;
                        }
                        if(origin(json, result, false)) {
                            try {
                                Fault fault = commit(device, jv, json, result);
                                if(fault == null) {
                                    result.put("reference", jv.getForeignReference());
                                } else {
                                    result.error(fault);
                                }
                            } catch (SOException soe) {
                                result.error(Fault.TECHNICAL_FAULT.replace(soe.getEndUserMessage()));
                            } catch (SORuntimeException sore) {
                                device.log(sore);
                                result.error(Fault.TECHNICAL_FAULT.replace(sore.getEndUserMessage()));
                            } catch (Exception e) {
                                device.log(e);
                                result.error(Fault.TECHNICAL_FAULT);
                            }
                        }
                    }
                }
            }
            default -> result.error("Invalid action specified");
        }
    }

    protected void entryCreated(Device device, JournalVoucher jv, JSON json) {
    }

    protected boolean validateWorkingDate(Device device, JournalVoucher jv, Date date) {
        return (date == null && jv.getDate() == null) ||
                DateUtility.isSameDate(device.getServer().getTransactionManager().getWorkingDate(), date);
    }

    private boolean origin(JSON json, JSONMap result, boolean check) {
        Id origin = json.getId("origin");
        if(origin == null) {
            String o = json.getString("origin");
            if (o == null) {
                result.error("Origin not specified");
                return false;
            }
            ForeignFinancialSystem ffs = ForeignFinancialSystem.get(o);
            if (ffs == null || !ffs.getName().equals(o)) {
                result.error("Origin not found - " + o);
                return false;
            }
            origin = ffs.getId();
        } else {
            if(!StoredObject.exists(ForeignFinancialSystem.class, "Id=" + origin)) {
                result.error("Origin not found - " + origin);
                return false;
            }
        }
        jv.setOrigin(origin);
        String ref = json.getString("reference");
        if(ref != null) {
            ref = ref.toUpperCase().strip();
            if(check) {
                result.put("reference", ref);
            }
            if(StoredObject.exists(JournalVoucher.class, "Origin=" + origin
                    + " AND ForeignReference='" + ref + "'", true)) {
                if(check) {
                    result.put("exists", true);
                    return true;
                }
                result.error(Fault.REFERENCE_ALREADY_EXISTS);
                return false;
            }
            jv.setForeignReference(ref);
            if(check) {
                result.put("exists", false);
                return true;
            }
        } else {
            if(check) {
                result.error(Fault.REFERENCE_NOT_SPECIFIED);
                return false;
            }
        }
        return true;
    }

    protected Fault commit(Device device, JournalVoucher jv, JSON json, JSONMap result) throws Exception {
        device.getServer().getTransactionManager().transact(jv::save);
        return null;
    }

    private Account getAccount(Device device, String account) {
        if(account == null || account.isEmpty()) {
            return null;
        }
        return createAccount(device, account);
    }

    protected Account createAccount(Device device, String account) {
        return null;
    }
}
