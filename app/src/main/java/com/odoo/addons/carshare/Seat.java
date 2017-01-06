package com.odoo.addons.carshare;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.odoo.R;
import com.odoo.addons.carshare.models.CarSeat;
import com.odoo.core.orm.ODataRow;
import com.odoo.core.orm.fields.types.ODate;
import com.odoo.core.support.addons.fragment.BaseFragment;
import com.odoo.core.support.addons.fragment.IOnSearchViewChangeListener;
import com.odoo.core.support.addons.fragment.ISyncStatusObserverListener;
import com.odoo.core.support.drawer.ODrawerItem;
import com.odoo.core.support.list.OCursorListAdapter;
import com.odoo.core.utils.IntentUtils;
import com.odoo.core.utils.OControls;
import com.odoo.core.utils.OCursorUtils;
import com.odoo.core.utils.ODateUtils;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016-11-25.
 */

public class Seat extends BaseFragment implements ISyncStatusObserverListener,
        LoaderManager.LoaderCallbacks<Cursor>, SwipeRefreshLayout.OnRefreshListener,
        OCursorListAdapter.OnViewBindListener, IOnSearchViewChangeListener, View.OnClickListener,
        AdapterView.OnItemClickListener{
    public static final String KEY = Seat.class.getSimpleName();
    public static final String EXTRA_KEY_TYPE = "extra_key_type";


    private View mView;
    private String mCurFilter = null;
    private OCursorListAdapter mAdapter = null;
    private boolean syncRequested = false;
    public enum Type {
        CarSeat
    }

    private Type mType = Type.CarSeat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        setHasSyncStatusObserver(KEY, this, db());
        return inflater.inflate(R.layout.common_listview, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasSwipeRefreshView(view, R.id.swipe_container, this);
        mView = view;
        mType = Seat.Type.valueOf(getArguments().getString(EXTRA_KEY_TYPE));
        ListView mPartnersList = (ListView) view.findViewById(R.id.listview);
        mAdapter = new OCursorListAdapter(getActivity(), null, R.layout.car_seat_row);
        mAdapter.setOnViewBindListener(this);
        //this setHasSectionIndexers second param represent quick search view on activity
        mAdapter.setHasSectionIndexers(true, "remark");
        mPartnersList.setAdapter(mAdapter);
        mPartnersList.setFastScrollAlwaysVisible(true);//
        mPartnersList.setOnItemClickListener(this);
        setHasFloatingButton(view, R.id.fabButton, mPartnersList, this);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public void onViewBind(View view, Cursor cursor, ODataRow row) {

        OControls.setText(view, R.id.start_point,row.getString("start_point")==null ? " "
                :_s(R.string.label_start_point)+row.getString("start_point_name"));
        OControls.setText(view, R.id.end_point, row.getString("end_point")==null ? " "
                :_s(R.string.label_end_point)+row.getString("end_point_name"));
        OControls.setText(view, R.id.person_num, row.getString("person_num")==null ? " "
                :row.getString("person_num")+_s(R.string.label_person_num));
        String leave_time =  row.getString("leave_time")==null ?"":row.getString("leave_time");
        if(!"".equals(leave_time)){
            DateTimeFormatter fmt = DateTimeFormat .forPattern("yyyy-MM-dd HH:mm:ss");
            DateTime dateTime = DateTime.parse(leave_time,fmt);
//            String leave_day =dateTime.toString("M-d", Locale.CHINESE);
            String leave_day =dateTime.toString("M月d日", Locale.CHINESE);
            String leave_day_time = dateTime.toString("HH:mm",Locale.CHINESE);
            OControls.setText(view, R.id.seat_day, leave_day);
            OControls.setText(view, R.id.seat_time, leave_day_time);
        }

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle data) {
        String where = "leave_time >= ?";
        List<String> args = new ArrayList<>();
        args.add("2016-12-01");
        if (mCurFilter != null) {
            where += " and name like ? ";
            args.add(mCurFilter + "%");
        }
        String selection = (args.size() > 0) ? where : null;
        String[] selectionArgs = (args.size() > 0) ? args.toArray(new String[args.size()]) : null;
        return new CursorLoader(getActivity(), db().uri(),
                null, null, null, "leave_time");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
        if (data.getCount() > 0) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setVisible(mView, R.id.swipe_container);
                    OControls.setGone(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.swipe_container, Seat.this);
                }
            }, 500);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    OControls.setGone(mView, R.id.loadingProgress);
                    OControls.setGone(mView, R.id.swipe_container);
                    OControls.setVisible(mView, R.id.data_list_no_item);
                    setHasSwipeRefreshView(mView, R.id.data_list_no_item, Seat.this);
                    OControls.setImage(mView, R.id.icon, R.drawable.ic_action_customers);
                    OControls.setText(mView, R.id.title, _s(R.string.label_no_person_found));
                    OControls.setText(mView, R.id.subTitle, "");
                }
            }, 500);
            if (db().isEmptyTable() && !syncRequested) {
                syncRequested = true;
                onRefresh();
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public  Class<CarSeat> database() {
        return CarSeat.class;
    }


    @Override
    public List<ODrawerItem> drawerMenus(Context context) {
        List<ODrawerItem> items = new ArrayList<>();
        items.add(new ODrawerItem(KEY).setTitle("找车")
                .setIcon(R.drawable.ic_action_customers)
                .setExtra(extra(Type.CarSeat))
                .setInstance(new Seat()));
        return items;
    }

    public Bundle extra(Seat.Type type) {
        Bundle extra = new Bundle();
        extra.putString(EXTRA_KEY_TYPE, type.toString());
        return extra;
    }

    @Override
    public void onStatusChange(Boolean refreshing) {
        getLoaderManager().restartLoader(0, null, this);
    }
    @Override
    public void onRefresh() {
        if (inNetwork()) {
            parent().sync().requestSync(CarSeat.AUTHORITY);
            setSwipeRefreshing(true);
        } else {
            hideRefreshingProgress();
            Toast.makeText(getActivity(), _s(R.string.toast_network_required), Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_partners, menu);
        setHasSearchView(this, menu, R.id.menu_partner_search);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onSearchViewTextChange(String newFilter) {
        mCurFilter = newFilter;
        getLoaderManager().restartLoader(0, null, this);
        return true;
    }
    @Override
    public void onSearchViewClose() {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fabButton:
                loadActivity(null);
                break;
        }
    }
    private void loadActivity(ODataRow row) {
        Bundle data = new Bundle();
        if (row != null) {
            data = row.getPrimaryBundleData();
        }
        data.putString(SeatDetail.KEY_CARSHARE_TYPE, mType.toString());
        IntentUtils.startActivity(getActivity(), SeatDetail.class, data);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ODataRow row = OCursorUtils.toDatarow((Cursor) mAdapter.getItem(position));
        loadActivity(row);
    }





}
