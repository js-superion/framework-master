package com.odoo.addons.carshare;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.odoo.App;
import com.odoo.R;
import com.odoo.addons.carshare.models.CarDeparture;
import com.odoo.addons.carshare.models.CarDepartureDetail;
import com.odoo.addons.carshare.models.CarPoint;
import com.odoo.addons.customers.utils.ShareUtil;
import com.odoo.base.addons.ir.feature.OFileManager;
import com.odoo.base.addons.res.ResPartner;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.OModel;
import com.odoo.core.orm.OValues;
import com.odoo.core.orm.ServerDataHelper;
import com.odoo.core.orm.fields.OColumn;
import com.odoo.core.rpc.helper.OdooFields;
import com.odoo.core.rpc.helper.utils.gson.OdooResult;
import com.odoo.core.support.OdooCompatActivity;
import com.odoo.core.utils.BitmapUtils;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OAlert;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OResource;
import com.odoo.core.utils.OStringColorUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import odoo.controls.ExpandableListControl;
import odoo.controls.OField;
import odoo.controls.OForm;

/**
 * Created by Administrator on 2016-11-28.
 */

public class DepartureDetail extends OdooCompatActivity
        implements View.OnClickListener, OField.IOnFieldValueChangeListener{
    public static final String TAG = DepartureDetail.class.getSimpleName();
    public static final int REQUEST_ADD_ITEMS = 323;
    public static String KEY_CARSHARE_TYPE = "carshare_type";
    private final String KEY_MODE = "key_edit_mode";
    private final String KEY_NEW_IMAGE = "key_new_image";
    private Bundle extras;
    private CarDeparture carDeparture;
    private ExpandableListControl mList;
    private List<Object> objects = new ArrayList<>();
    private HashMap<String, Float> lineValues = new HashMap<>();
    private HashMap<String, Integer> lineIds = new HashMap<>();
    private ExpandableListControl.ExpandableListAdapter mAdapter;
    private ODataRow record = null;
    private CarPoint carPoint = null;
    private ImageView userImage = null;
    private OForm mForm;
    private App app;
    private Boolean mEditMode = false;
    private Menu mMenu;
    private OFileManager fileManager;
//    private String newImage = null;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private Toolbar toolbar;
    private Departure.Type carShareType = Departure.Type.CarDeparture;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.car_departure);

        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.departure_collapsing_toolbar);

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
        carDeparture = new CarDeparture(this, null);
        carPoint = new CarPoint(this,null);
        extras = getIntent().getExtras();
        if (hasRecordInExtra())
            carShareType = Departure.Type.valueOf(extras.getString(KEY_CARSHARE_TYPE));
        if (!hasRecordInExtra())
            mEditMode = true;
        setupToolbar();
        initAdapter();
    }
    private boolean hasRecordInExtra() {
        return extras != null && extras.containsKey(OColumn.ROW_ID);
    }

    private void setMode(Boolean edit) {
//        findViewById(R.id.captureImage).setVisibility(edit ? View.VISIBLE : View.GONE);
        if (mMenu != null) {
            mMenu.findItem(R.id.menu_departure_detail_more).setVisible(!edit);
            mMenu.findItem(R.id.menu_departure_edit).setVisible(!edit);
            mMenu.findItem(R.id.menu_departure_save).setVisible(edit);
            mMenu.findItem(R.id.menu_departure_cancel).setVisible(edit);
        }
        int color = Color.DKGRAY;
        if (record != null) {
            color = OStringColorUtil.getStringColor(this, record.getString("name"));
        }
        if (edit) {
            if (!hasRecordInExtra()) {
                collapsingToolbarLayout.setTitle("New");
            }
            mForm = (OForm) findViewById(R.id.departure_edit_form);
            findViewById(R.id.departure_view_layout).setVisibility(View.GONE);
            findViewById(R.id.departure_edit_layout).setVisibility(View.VISIBLE);
            OField is_company = (OField) findViewById(R.id.is_company_edit);
            findViewById(R.id.layoutAddItem).setOnClickListener(this);
            is_company.setOnValueChangeListener(this);
        } else {
            mForm = (OForm) findViewById(R.id.departure_view_form);
            findViewById(R.id.departure_edit_layout).setVisibility(View.GONE);
            findViewById(R.id.departure_view_layout).setVisibility(View.VISIBLE);
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
            record = carDeparture.browse(rowId);
//            record.put("full_address", carDeparture.getAddress(record));
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
            case R.id.layoutAddItem:
                if (mForm.getValues() != null) {
                    Intent intent = new Intent(this, AddPointWizard.class);
                    Bundle extra = new Bundle();
                    for (String key : lineValues.keySet()) {
                        extra.putFloat(key, lineValues.get(key));
                    }
                    intent.putExtras(extra);
                    startActivityForResult(intent, REQUEST_ADD_ITEMS);
                }
                break;
        }

    }

    private void initAdapter() {
        mList = (ExpandableListControl) findViewById(R.id.expListCarPoint);
        mList.setVisibility(View.VISIBLE);
        if (extras != null && record != null) {
            List<ODataRow> lines = record.getO2MRecord("details").browseEach();
            for (ODataRow line : lines) {
                int point_id = carPoint.selectServerId(line.getInt("id"));
                if (point_id != 0) {
//                    lineValues.put(point_id + "", line.getString("product_uom_qty"));
                    lineIds.put(point_id + "", line.getInt("id"));
                }
            }
            objects.addAll(lines);
        }
        mAdapter = mList.getAdapter(R.layout.car_add_point_item, objects,
                new ExpandableListControl.ExpandableListAdapterGetViewListener() {
                    @Override
                    public View getView(int position, View mView, ViewGroup parent) {
                        ODataRow row = (ODataRow) mAdapter.getItem(position);
                        OControls.setText(mView, R.id.point_name, row.getString("point_name"));
                        return mView;
                    }
                });
        mAdapter.notifyDataSetChanged(objects);
    }
    /**
     * 设置在只读模式下，调用本地资源
     * **/
    private void checkControls() {
        findViewById(R.id.mobile_phone).setOnClickListener(this);
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
            case R.id.menu_departure_save:
                OValues values = mForm.getValues();
                if (values != null) {
//                    switch (carShareType) {
//                        case CarDeparture:
//                            values.put("customer", "false");
//                            values.put("supplier", "true");
//                            break;
//                        default:
//                            values.put("customer", "true");
//                            break;
//                    }
//                    if (newImage != null) {
//                        values.put("image_small", newImage);
//                        values.put("large_image", newImage);
//                    }
                    if (record != null) {
                        carDeparture.update(record.getInt(OColumn.ROW_ID), values);
                        Toast.makeText(this, R.string.toast_information_saved, Toast.LENGTH_LONG).show();
                        mEditMode = !mEditMode;
                        setupToolbar();
                    } else {
                        final int row_id = carDeparture.insert(values);
                        if (row_id != OModel.INVALID_ROW_ID) {
                            finish();
                        }
                    }
                }
                break;
            case R.id.menu_departure_cancel:
            case R.id.menu_departure_edit:
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
            case R.id.menu_departure_share:
                ShareUtil.shareContact(this, record, true);
                break;
            case R.id.menu_departure_import:
                ShareUtil.shareContact(this, record, false);
                break;
            case R.id.menu_departure_delete:
                OAlert.showConfirm(this, OResource.string(this,
                        R.string.confirm_are_you_sure_want_to_delete),
                        new OAlert.OnAlertConfirmListener() {
                            @Override
                            public void onConfirmChoiceSelect(OAlert.ConfirmType type) {
                                if (type == OAlert.ConfirmType.POSITIVE) {
                                    // Deleting record and finishing activity if success.
                                    if (carDeparture.delete(record.getInt(OColumn.ROW_ID))) {
                                        Toast.makeText(DepartureDetail.this, R.string.toast_record_deleted,
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
        getMenuInflater().inflate(R.menu.menu_cardeparture, menu);
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
                OdooResult record = carDeparture.getServerDataHelper().read(null, params[0]);
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
                    carDeparture.update(record.getInt(OColumn.ROW_ID), values);
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
        if (requestCode == REQUEST_ADD_ITEMS && resultCode == Activity.RESULT_OK) {
            lineValues.clear();
            objects.clear();
            objects.addAll(null);
            mAdapter.notifyDataSetChanged(objects);
        }else{
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




}
