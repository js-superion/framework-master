package com.odoo.addons.carshare;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
//import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.carshare.models.CarPoint;
import com.odoo.addons.carshare.models.CarSeat;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.ServerDataHelper;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.OArguments;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.ODateUtils;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.List;

import odoo.controls.ODateTimeField;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by Administrator on 2016-11-28.
 */

public class SeatDetail extends OdooCompatActivity
        implements View.OnClickListener, OField.IOnFieldValueChangeListener {
    public static final String TAG = SeatDetail.class.getSimpleName();
    public static String KEY_CARSHARE_TYPE = "carshare_type";
    private final String KEY_MODE = "key_edit_mode";
    private final String KEY_NEW_IMAGE = "key_new_image";
    private Bundle extras;
    private CarSeat carSeat;
    private CarPoint carPoint;
    private ResPartner partner;
    private SeatDetail seatDetail;
    private ODataRow record = null;
    private ImageView userImage = null;
    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private OFileManager fileManager;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private Seat.Type carShareType = Seat.Type.CarSeat;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_seat);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.seat_collapsing_toolbar);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

//        userImage = (ImageView) findViewById(R.id.user_image);
//        findViewById(R.id.captureImage).setOnClickListener(this);

        fileManager = new OFileManager(this);
        if (toolbar != null)
            collapsingToolbarLayout.setTitle("");
        if (savedInstanceState != null) {
            mEditMode = savedInstanceState.getBoolean(KEY_MODE);
//            newImage = savedInstanceState.getString(KEY_NEW_IMAGE);
        }
        app = (App) getApplicationContext();
        carSeat = new CarSeat(this, null);
        carPoint = new CarPoint(this, null);
        partner = new ResPartner(this ,null);
        extras = getIntent().getExtras();
        if (hasRecordInExtra())
            carShareType = Seat.Type.valueOf(extras.getString(KEY_CARSHARE_TYPE));
        if (!hasRecordInExtra())
            mEditMode = true;
        setupToolbar();
    }

    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit) {
//        findViewById(R.id.captureImage).setVisibility(edit ? View.VISIBLE : View.GONE);
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_seat_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_seat_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_seat_save).setVisible(edit);
            mMenu.findItem(R.id.menu_seat_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle("New");
            }
            mForm = (OForm) findViewById(R.id.seat_edit_form);
            findViewById(R.id.seat_view_layout).setVisibility(View.GONE);
            findViewById(R.id.seat_edit_layout).setVisibility(View.VISIBLE);
//            OField is_company = (OField) findViewById(R.id.is_company_edit);
//            is_company.setOnValueChangeListener(this);
        } else {
            mForm = (OForm) findViewById(R.id.seat_view_form);
            findViewById(R.id.seat_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.seat_view_layout).setVisibility(View.VISIBLE);
        }
        setColor(color);
    }

    private void setupToolbar() {
        if (!hasRecordInExtra()) {
            setMode(mEditMode);
//            userImage.setColorFilter(Color.parseColor("#ffffff"));
//            userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            mForm.setEditable(mEditMode);
            mForm.initForm(null);
        } else {
            int rowId = extras.getInt(OColumn.ROW_ID);
            record = carSeat.browse(rowId);
//            record.put("full_address", carSeat.getAddress(record));
            checkControls();
            setMode(mEditMode);
            mForm.setEditable(mEditMode);
            mForm.initForm(record);
            collapsingToolbarLayout.setTitle(record.getString("name"));
//            setCustomerImage();
//            if (record.getInt("id") != 0 && record.getString("large_image").equals("false")) {
//                BigImageLoader bigImageLoader = new CustomerDetails.BigImageLoader();
//                bigImageLoader.execute(record.getInt("id"));
//            }
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.full_address:
                IntentUtils.redirectToMap(this, record.getString("full_address"));
                break;
            case R.id.website:
                IntentUtils.openURLInBrowser(this, record.getString("website"));
                break;
            case R.id.email:
                IntentUtils.requestMessage(this, record.getString("email"));
                break;
            case R.id.phone_number:
                IntentUtils.requestCall(this, record.getString("phone"));
                break;
            case R.id.mobile_number:
                IntentUtils.requestCall(this, record.getString("mobile"));
                break;
            case R.id.captureImage:
                fileManager.requestForFile(OFileManager.RequestType.IMAGE_OR_CAPTURE_HIGH_IMAGE);
                break;
        }

    }

    private void checkControls() {
//        findViewById(R.id.mobile_phone).setOnClickListener(this);
//        findViewById(R.id.website).setOnClickListener(this);
//        findViewById(R.id.email).setOnClickListener(this);
//        findViewById(R.id.phone_number).setOnClickListener(this);
//        findViewById(R.id.mobile_number).setOnClickListener(this);
    }

    private void setCustomerImage() {

        if (record != null && !record.getString("image_small").equals("false")) {
//            userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
//            String base64 = newImage;
//            if (newImage == null) {
//                if (!record.getString("large_image").equals("false")) {
//                    base64 = record.getString("large_image");
//                } else {
//                    base64 = record.getString("image_small");
//                }
//            }
//            userImage.setImageBitmap(BitmapUtils.getBitmapImage(this, base64));
        } else {
//            userImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            userImage.setColorFilter(Color.WHITE);
            int color = OStringColorUtil.getStringColor(this, record.getString("name"));
//            userImage.setBackgroundColor(color);
        }
    }

    private void setColor(int color) {
        mForm.setIconTintColor(color);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_seat_save:
                OValues values = mForm.getValues();
                if (values != null) {
                    if (record != null) {
                        carSeat.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.toast_saved_success, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {
                        try {
                            OArguments args = new OArguments();
                            args.add("4");
                            System.out.print("開始調用1");
                            ServerDataHelper helper2 = partner.getServerDataHelper();
                            ServerDataHelper helper = carSeat.getServerDataHelper();
                            System.out.print("開始調用2");
                            Object billno = helper.callMethod("get_bill_no", args);
                            Thread.sleep(500);

                            DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");
                            String strLevaeTime = values.getString("leave_time");
                            DateTime leaveTime = DateTime.parse(strLevaeTime, fmt);
//                        DateTimeZone zeroTz =  DateTimeZone.UTC;
//                        DateTime oneHourAgo = DateTime.now(zeroTz).minusHours(1);
                            String utcOneHour = ODateUtils.getCurrentDateWithHour(-1); //获取的是utc时间
                            DateTime oneHourTime = DateTime.parse(utcOneHour, fmt);
                            if (leaveTime.isBefore(oneHourTime)) {
                                Toast.makeText(this, R.string.toast_time_before_onehour, Toast.LENGTH_LONG).show();
                                break;
                            }
                            int startPoint = values.getInt("start_point");
                            int endPoint = values.getInt("end_point");
                            ODataRow startPointRow = carPoint.browse(startPoint);
                            ODataRow endPointRow = carPoint.browse(endPoint);
//                        List<ODataRow> pointRows = null;
//                        String sql = "SELECT name FROM car_point WHERE _id = ?";
//                        pointRows = carPoint.query(sql, new String[]{startPoint});
                            //排序是按照_id来，所有取索引时应该注意下0对应终点，1对应起点
                            values.put("start_point_name", startPointRow.getString("name"));
                            values.put("end_point_name", endPointRow.getString("name"));
                            final int row_id = carSeat.insert(values);
                            if (row_id != OModel.INVALID_ROW_ID) {
                                finish();
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                break;
            case R.id.menu_seat_cancel:
            case R.id.menu_seat_edit:
                if (hasRecordInExtra()) {
                    mEditMode = !mEditMode;
                    setMode(mEditMode);
                    mForm.setEditable(mEditMode);
                    mForm.initForm(record);
                    setCustomerImage();
                } else {
                    finish();
                }
                break;
            case R.id.menu_seat_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_seat_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_seat_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.label_confirm_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (carSeat.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(SeatDetail.this, R.string.toast_deleted_success,
                                                Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                }
                            }
                        });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_carseat, menu);
        mMenu = menu;
        setMode(mEditMode);
        return true;
    }

    @Override
    public void onFieldValueChange(OField field, Object value) {
        if (field.getFieldName().equals("is_company")) {
            Boolean checked = Boolean.parseBoolean(value.toString());
            int view = (checked) ? View.GONE : View.VISIBLE;
            findViewById(R.id.parent_id).setVisibility(view);
        }
    }

    private class BigImageLoader extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... params) {
            String image = null;
            try {
                Thread.sleep(300);
                OdooFields fields = new OdooFields();
                fields.addAll(new String[]{"image_medium"});
                OdooResult record = carSeat.getServerDataHelper().read(null, params[0]);
                if (record != null && !record.getString("image_medium").equals("false")) {
                    image = record.getString("image_medium");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return image;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result != null) {
                if (!result.equals("false")) {
                    OValues values = new OValues();
                    values.put("large_image", result);
                    carSeat.update(record.getInt(OColumn.ROW_ID), values);
                    record.put("large_image", result);
//                    setCustomerImage();
                }
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_MODE, mEditMode);
//        outState.putString(KEY_NEW_IMAGE, newImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        OValues values = fileManager.handleResult(requestCode, resultCode, data);
        if (values != null && !values.contains("size_limit_exceed")) {
//            newImage = values.getString("datas");
            userImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            userImage.setColorFilter(null);
//            userImage.setImageBitmap(BitmapUtils.getBitmapImage(this, newImage));
        } else if (values != null) {
            Toast.makeText(this, R.string.toast_image_size_too_large, Toast.LENGTH_LONG).show();
        }
    }


}
