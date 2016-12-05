package com.odoo.addons.carshare.providers;

import com.odoo.addons.carshare.models.CarDepartureDetail;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Administrator on 2016-11-28.
 */

public class DepartureDetailProvider extends BaseModelProvider {
    public  static final  String TAG = DepartureDetailProvider.class.getSimpleName();
    @Override
    public String authority() {
        return CarDepartureDetail.AUTHORITY;
    }
}
