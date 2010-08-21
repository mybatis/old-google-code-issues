package com.opensymphony.oscache.general;

import com.opensymphony.oscache.base.NeedsRefreshException;

public class GeneralCacheAdministrator {
    public void flushGroup(String id) {
    }

    public Object getFromCache(String keyString, int refreshPeriod) throws NeedsRefreshException {
        return null;
    }

    public void cancelUpdate(String keyString) {
    }

    public void flushEntry(String keyString) {
    }

    public void putInCache(String keyString, Object object, String[] strings) {
    }
}
