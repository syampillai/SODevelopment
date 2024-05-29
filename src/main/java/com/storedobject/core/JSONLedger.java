package com.storedobject.core;

import com.storedobject.common.SORuntimeException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.storedobject.common.Fault.*;

public class JSONLedger implements JSONService {

    @Override
    public void execute(Device device, JSON json, JSONMap result) {
        String a = json.getString("account");
        if (a == null) {
            result.error(ACCOUNT_NOT_FOUND);
            return;
        }
        Account account = getAccount(a);
        if (account == null) {
            result.error(ACCOUNT_NOT_FOUND.append(a));
            return;
        }
        DatePeriod period = getPeriod(json);
        if(period == null) {
            result.error(INVALID_PERIOD);
            return;
        }
        if(account instanceof AccountTitle at) {
            account = at.getAccount();
        }
        try {
            Ledger ledger = getLedger(account, period);
            result.put("account", account.getNumber());
            result.put("dateFrom", period.getFrom());
            result.put("dateTo", period.getTo());
            result.put("openingBalance", ledger.getOpeningBalance());
            List<Map<String, Object>> entries = new ArrayList<>();
            result.put("ledgerEntries", entries);
            Map<String, Object> e;
            for (LedgerEntry le : ledger) {
                e = new HashMap<>();
                e.put("date", le.getDate());
                e.put("amount", le.getAmount());
                e.put("narration", le.getParticulars());
                e.put("valueDate", le.getValueDate());
                e.put("posted", !le.isUnposted());
                e.put("balance", le.getBalance());
                addEntryDetails(e, le);
                entries.add(e);
            }
        } catch (SOException soe) {
            result.error(TECHNICAL_FAULT.append(soe.getEndUserMessage()));
        }catch (SORuntimeException soe) {
            log(device, soe);
            result.error(TECHNICAL_FAULT.append(soe.getEndUserMessage()));
        } catch (Exception e) {
            log(device, e);
            result.error(TECHNICAL_FAULT);
        }
    }

    protected Account getAccount(String account) {
        return Account.getFor(account);
    }

    protected DatePeriod getPeriod(JSON json) {
        return json.getDatePeriod();
    }

    @SuppressWarnings("RedundantThrows")
    protected Ledger getLedger(Account account, DatePeriod period) throws Exception {
        return account.getLedger(period);
    }

    protected void log(Device device, Object anything) {
        device.log(anything);
    }

    protected void addEntryDetails(Map<String, Object> details, LedgerEntry ledgerEntry) {}
}
