package com.odoo.addons.carshare.providers;

import com.odoo.addons.carshare.models.CarDeparture;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Administrator on 2016-11-28.
 */

public class CarDepartureProvider extends BaseModelProvider {
    public  static final  String TAG = CarDepartureProvider.class.getSimpleName();

    @Override
    public String authority() {
        return CarDeparture.AUTHORITY;
    }
}
