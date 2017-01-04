package com.odoo.addons.carshare.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.support.OUser;

import java.util.ArrayList;
import java.util.List;

/**'
 * Created by Administrator on 2016-11-27.
 */

public class CarDeparture extends OModel {

    public static final String TAG = CarDeparture.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".carshare.provider.content.sync.car_departure";
    OColumn name = new OColumn("单号", OVarchar.class);
    OColumn departure_time = new OColumn("时间", ODateTime.class);
    OColumn start_point = new OColumn("起点",CarPoint.class,OColumn.RelationType.ManyToOne);
    @Odoo.Functional(method="saveStartPointName", depends = {"start_point"}, store = true)
    OColumn start_point_name = new OColumn("起点名称",OVarchar.class).setLocalColumn();
    OColumn end_point = new OColumn("终点",CarPoint.class,OColumn.RelationType.ManyToOne);
    @Odoo.Functional(method="saveEndPointName", depends = {"end_point"}, store = true)
    OColumn end_point_name = new OColumn("终点名称",OVarchar.class).setLocalColumn();
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
//    public String savePointName(OValues values){
//        String pointName = "";
//        String startPoint = values.getString("start_point");
//        String endPoint = values.getString("end_point");
//        return pointName;
//    }

    //these two methods(saveEndpointName & saveStartPointName) was callend,when data sync
    public String saveEndPointName(OValues value) {
        try {
            if (!value.getString("end_point").equals("false")) {
                List<Object> points = (ArrayList<Object>) value.get("end_point");
                return points.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public String saveStartPointName(OValues value) {
        try {
            if (!value.getString("start_point").equals("false")) {
                List<Object> points = (ArrayList<Object>) value.get("start_point");
                return points.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
