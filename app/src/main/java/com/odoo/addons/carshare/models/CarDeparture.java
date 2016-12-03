package com.odoo.addons.carshare.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

/**
 * Created by Administrator on 2016-11-27.
 */

public class CarDeparture extends OModel {

    public static final String TAG = CarDeparture.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".carshare.provider.content.sync.car_departure";
    OColumn name = new OColumn("单号", OVarchar.class);
    OColumn departure_time = new OColumn("时间", ODateTime.class);
    OColumn start_point = new OColumn("起点",CarPoint.class,OColumn.RelationType.ManyToOne);
    OColumn end_point = new OColumn("终点",CarPoint.class,OColumn.RelationType.ManyToOne);
    OColumn seat_num = new OColumn("座位",OInteger.class);
    OColumn mobile_phone = new OColumn("电话",OVarchar.class).setSize(15);
    OColumn remark = new OColumn("备注",OVarchar.class).setSize(80);
    OColumn details = new OColumn("途经站点",CarDepartureDetail.class, OColumn.RelationType.OneToMany)
            .setRelatedColumn("departure_id");


    public CarDeparture(Context context, OUser user) {
        super(context, "car.departure", user);
    }

    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
