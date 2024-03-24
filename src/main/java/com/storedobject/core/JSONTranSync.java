package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JSONTranSync implements JSONService {

    private static final String RSN = "Requesting system is not recognized";
    private Account account;
    private Id foreignSystem;

    @Override
    public void execute(Device device, JSON json, JSONMap result) {
        String action = json.getString("action");
        if(action == null) {
            result.error("Action not specified");
            return;
        }
        Id fs = json.getId("system");
        if(fs == null) {
            String o = json.getString("system");
            if (o != null) {
                ForeignFinancialSystem ffs = ForeignFinancialSystem.get(o);
                if (ffs == null || !ffs.getName().equals(o)) {
                    result.error(RSN + " - " + o);
                    return;
                }
                foreignSystem = ffs.getId();
            }
        } else {
            if(!StoredObject.exists(ForeignFinancialSystem.class, "Id=" + fs)) {
                result.error(RSN + " - " + fs);
                return;
            }
            foreignSystem = fs;
        }
        switch (action) {
            case "updatedNext" -> {
                if(!updated(device, json, result)) {
                    return;
                }
                account = null;
                next(result);
            }
            case "updated" -> updated(device, json, result);
            case "next" -> {
                account = null;
                next(result);
            }
            case "account" -> {
                if(syncAccount(device, json, result)) {
                    next(result);
                }
            }
            case "accountNext" -> {
                if(!updated(device, json, result)) {
                    return;
                }
                if(syncAccount(device, json, result)) {
                    next(result);
                }
            }
            default -> result.error("Unknown action");
        }
    }

    private boolean syncAccount(Device device, JSON json, JSONMap result) {
        String an = json.getString("account");
        if(an == null) {
            if(account != null) {
                return true;
            }
            result.error("Account not specified");
            return false;
        }
        account = Account.getFor(an);
        if(account == null) {
            result.error("Account not found: " + an);
            return false;
        }
        try {
            update(account);
            return true;
        } catch (SOException | SORuntimeException soe) {
            result.error(soe.getEndUserMessage());
        } catch (Exception e) {
            device.log(e);
            result.error("Technical error");
        }
        return false;
    }

    private boolean updated(Device device, JSON json, JSONMap result) {
        Id v = json.getId("voucher");
        if(v == null) {
            result.error("Voucher not specified");
            return false;
        }
        if(foreignSystem == null) {
            result.error(RSN);
            return false;
        }
        JournalVoucherStage jvs = StoredObject.get(JournalVoucherStage.class, "Voucher=" + v
                + " AND ForeignSystem=" + foreignSystem);
        try {
            if(device.getServer().getTransactionManager().transact(jvs::delete) != 0) {
                result.error("Transaction approval error");
            }
            return true;
        } catch (SOException | SORuntimeException soe) {
            result.error(soe.getEndUserMessage());
        } catch (Exception error) {
            device.log(error);
            result.error("Technical error");
        }
        return false;
    }

    private void next(JSONMap result) {
        if(foreignSystem == null) {
            result.error(RSN);
            return;
        }
        JournalVoucherStage jvs;
        if(account == null) {
            jvs = StoredObject.get(JournalVoucherStage.class, "ForeignSystem="
                    + foreignSystem, "ForeignSystem,Voucher");
        } else {
            jvs = StoredObject.list(JournalVoucherStage.class, "ForeignSystem="
                    + foreignSystem, "ForeignSystem,Voucher").filter(this::accInvolved).findFirst();
        }
        if(jvs == null) {
            result.put("voucher", "");
            return;
        }
        result.put("voucher", jvs.getVoucherId().toString());
        JournalVoucher jv = StoredObject.get(JournalVoucher.class, jvs.getVoucherId(), true);
        result.put("date", jv.getDate());
        List<Map<String, Object>> entries = new ArrayList<>();
        result.put("entries", entries);
        jv.entries().forEach(e -> {
            Map<String, Object> entry = new HashMap<>();
            entry.put("account", e.getAccount().getNumber());
            entry.put("serial", e.getEntrySerial());
            TransactionType type = e.getType();
            entry.put("type", type == null ? "" : type.getName());
            entry.put("amount", e.getAmount());
            entry.put("narration", e.getParticulars());
            entry.put("valueDate", e.getValueDate());
            entries.add(entry);
        });
    }

    private boolean accInvolved(JournalVoucherStage jvs) {
        return jvs.getVoucher().entries().anyMatch(e -> e.getAccount().getId().equals(account.getId()));
    }

    /**
     * Update the account's transactions from other sources.
     *
     * @param account Account.
     * @throws Exception If any error occurs.
     */
    protected void update(Account account) throws Exception {
    }
}
