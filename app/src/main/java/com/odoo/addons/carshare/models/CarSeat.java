package com.odoo.addons.carshare.models;

import android.content.Context;
import android.net.Uri;

import com.odoo.BuildConfig;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.ServerDataHelper;
import com.odoo.core.orm.annotation.Odoo;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.orm.fields.types.ODateTime;
import com.odoo.core.orm.fields.types.OInteger;
import com.odoo.core.orm.fields.types.OSelection;
import com.odoo.core.orm.fields.types.OVarchar;
import com.odoo.core.rpc.helper.OArguments;
import com.odoo.core.support.OUser;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016-11-27.
 */

public class CarSeat extends OModel {
    public static final String TAG = CarSeat.class.getSimpleName();
    public static final String AUTHORITY = BuildConfig.APPLICATION_ID +
            ".carshare.provider.content.sync.car_seat";

    OColumn name = new OColumn("单号", OVarchar.class);
    OColumn mobile_phone = new OColumn("手机", OVarchar.class).setSize(20);
    @Odoo.onChange(method = "onDateTimeChange")
    OColumn leave_time = new OColumn("时间", ODateTime.class).setRequired();;
    OColumn start_point = new OColumn("起点",CarPoint.class,OColumn.RelationType.ManyToOne).setRequired();;
    @Odoo.Functional(method="saveStartPointName", depends = {"start_point"}, store = true)
    OColumn start_point_name = new OColumn("起点名称",OVarchar.class).setLocalColumn();

    OColumn end_point = new OColumn("终点",CarPoint.class,OColumn.RelationType.ManyToOne).setRequired();;
    @Odoo.Functional(method="saveEndPointName", depends = {"end_point"}, store = true)
    OColumn end_point_name = new OColumn("终点名称",OVarchar.class).setLocalColumn();

    OColumn wait_point = new OColumn("候车点",CarPoint.class,OColumn.RelationType.ManyToOne);
    @Odoo.Functional(method="saveWaitPointName", depends = {"wait_point"}, store = true)
    OColumn wait_point_name = new OColumn("候车点名称",OVarchar.class).setLocalColumn();

//    OColumn person_num = new OColumn("人数",OInteger.class).setDefaultValue(1);
    OColumn person_num = new OColumn("人数", OSelection.class)
            .addSelection("1","1")
            .addSelection("2","2")
            .addSelection("3","3")
            .addSelection("4","4")
            .addSelection("5","5").setRequired();;

    OColumn remark = new OColumn("备注",OVarchar.class).setSize(80);
    public CarSeat(Context context,  OUser user) {
        super(context, "car.seat", user);
    }


    @Override
    public Uri uri() {
        return buildURI(AUTHORITY);
    }
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

    public ODataRow onDateTimeChange(ODataRow row){
        ServerDataHelper helper = getServerDataHelper();
        OArguments oArguments = new OArguments();
        oArguments.add(new JSONArray().put(2));
        Object billno = helper.callMethod("default_car_seat_get", oArguments);
        ODataRow newValues = new ODataRow();
        newValues.put("city", row.getString("city"));
        return newValues;
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

    public String saveWaitPointName(OValues value) {
        try {
            if (!value.getString("wait_point").equals("false")) {
                List<Object> points = (ArrayList<Object>) value.get("wait_point");
                return points.get(1) + "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


}
