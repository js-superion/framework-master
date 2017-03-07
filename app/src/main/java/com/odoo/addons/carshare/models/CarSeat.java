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
import com.odoo.core.rpc.helper.ODomain;
import com.odoo.core.rpc.helper.ORecordValues;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooRecord;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
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
//    @Odoo.onChange(method = "onDateTimeChange")
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

    public boolean createCarSeat(OValues value) {
        OValues values = new OValues();
        values.put("name", value.getString("name"));
        values.put("mobile_phone", value.getString("mobile_phone"));
        values.put("leave_time", value.getString("leave_time"));
        values.put("start_point", value.getString("start_point"));
        values.put("end_point", value.getString("end_point"));
        values.put("start_point_name", value.getString("start_point_name"));
        values.put("end_point_name", value.getString("end_point_name"));
//        values.put("wait_point", value.getString("wait_point"));
        values.put("person_num", value.getString("person_num"));
        values.put("id", value.get("id"));
        insert(values);
        return true;
    }

    public static ORecordValues valuesToData(OModel model, OValues value) {
        ORecordValues data = new ORecordValues();

        data.put("name", value.get("name"));
        data.put("mobile_phone", value.getString("mobile_phone"));
        data.put("leave_time", value.get("leave_time"));
        int v1 = value.getInt("start_point");
        int v2 = value.getInt("end_point");
        ODataRow startPointRow = model.browse(v1);
        ODataRow endPointRow = model.browse(v2);
        data.put("start_point",startPointRow.getInt("id"));//获取本地数据对应的服务端id,传给服务端
        data.put("end_point", endPointRow.getInt("id"));
        value.put("start_point_name", startPointRow.getString("name"));
        value.put("end_point_name", endPointRow.getString("name"));
//        data.put("wait_point", value.getString("wait_point"));
        data.put("person_num", value.get("person_num"));
        return data;
    }

    public String getNameFromServer(Integer serverId) {
        ODomain domain = new ODomain();
        domain.add("id", "=", serverId);
        OdooFields fields = new OdooFields();
        fields.addAll(new String[]{"name"});
        OdooResult result = getServerDataHelper().read(fields, serverId);
        String billNo = "false";
        if (result != null && result.has("name")) {
            billNo = result.getString("name");
        }
        return billNo;
    }


}
