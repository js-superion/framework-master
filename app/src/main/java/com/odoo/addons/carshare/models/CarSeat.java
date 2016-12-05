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

public class CarSeat extends OModel {
    public static final String TAG = CarSeat.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".carshare.provider.content.sync.car_seat";

    OColumn name = new OColumn("单号", OVarchar.class);
    OColumn mobile_phone = new OColumn("手机", OVarchar.class);
    OColumn leave_time = new OColumn("时间", ODateTime.class);
    OColumn end_point = new OColumn("终点",CarPoint.class,OColumn.RelationType.ManyToOne);
    OColumn wait_point = new OColumn("候车点",CarPoint.class,OColumn.RelationType.ManyToOne);
    OColumn person_num = new OColumn("座位",OInteger.class);
    OColumn remark = new OColumn("备注",OVarchar.class).setSize(80);
    public CarSeat(Context context,  OUser user) {
        super(context, "car.seat", user);
    }


    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
}
