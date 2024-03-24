package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

public class JSONTransact implements JSONService {

    private final JournalVoucher jv = new JournalVoucher();
    private Id origin;
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
            result.error("No action specified");
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
                            result.error("Account not specified/found");
                            return;
                        }
                        Money amount = json.getMoney("amount");
                        if(amount == null) {
                            result.error("Amount not specified");
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
                            error = false;
                        } catch (SOException | SORuntimeException soe) {
                            result.error(soe.getEndUserMessage());
                        } catch (Exception e) {
                            device.log(e);
                            result.error("Technical error");
                        }
                    }
                    default -> { // Commit
                        if(jv.getEntryCount() == 0) {
                            result.error("No entries");
                            return;
                        }
                        if(origin(json, result, false)) {
                            try {
                                commit(device, jv);
                                result.put("reference", jv.getForeignReference());
                            } catch (SOException | SORuntimeException soe) {
                                result.error(soe.getEndUserMessage());
                            } catch (Exception e) {
                                device.log(e);
                                result.error("Technical error");
                            }
                        }
                    }
                }
            }
            default -> result.error("Invalid action specified");
        }
    }

    private boolean origin(JSON json, JSONMap result, boolean check) {
        origin = json.getId("origin");
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
                result.error("Already exists");
                return false;
            }
            jv.setForeignReference(ref);
            if(check) {
                result.put("exists", false);
                return true;
            }
        } else {
            if(check) {
                result.error("Reference not specified");
                return false;
            }
        }
        return true;
    }

    protected void commit(Device device, JournalVoucher jv) throws Exception {
        //noinspection ResultOfMethodCallIgnored
        device.getServer().getTransactionManager().transact(jv::save);
    }

    protected Id getOrigin() {
        return origin;
    }
}
