package com.odoo.addons.carshare.services;

import android.content.Context;
import android.os.Bundle;

import com.odoo.addons.carshare.models.CarDepartureDetail;
import com.odoo.core.service.OSyncAdapter;
import com.odoo.core.service.OSyncService;
import com.odoo.core.support.OUser;

/**
 * Created by Administrator on 2016-11-28.
 */

public class DepartureDetailSyncService extends OSyncService {
    public static final String TAG = DepartureDetailSyncService.class.getSimpleName();
    @Override
    public OSyncAdapter getSyncAdapter(OSyncService service, Context context) {
        return new OSyncAdapter(context, CarDepartureDetail.class, service, true);
    }

    @Override
    public void performDataSync(OSyncAdapter adapter, Bundle extras, OUser user) {
        adapter.syncDataLimit(80);
    }
}
