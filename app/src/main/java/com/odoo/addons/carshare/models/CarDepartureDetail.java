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

public class CarDepartureDetail extends OModel {
    public static final String TAG = CarDepartureDetail.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".carshare.provider.content.sync.car_departure_detail";

    OColumn departure_id = new OColumn("发车记录",CarDeparture.class, OColumn.RelationType.ManyToOne);
    OColumn point_name = new OColumn("站点", OVarchar.class);

    public CarDepartureDetail(Context context, OUser user) {
        super(context, "car.departure.detail", user);
    }


    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
