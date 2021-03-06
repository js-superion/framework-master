package com.odoo.addons.carshare.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by Administrator on 2016-11-27.
 */

public class CarPoint extends OModel {
    public static final String TAG = CarPoint.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".carshare.provider.content.sync.car_point";

    OColumn name = new OColumn("站点名", OVarchar.class);

    public CarPoint(Context context, OUser user) {
        super(context,"car.point", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
