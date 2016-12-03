package com.odoo.addons.carshare.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.carshare.models.CarDeparture;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by Administrator on 2016-11-28.
 */

public class CarShareSyncService extends OSyncService {
    public static final String TAG = CarShareSyncService.class.getSimpleName();
    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, CarDeparture.class, service, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(80);
    }
}
