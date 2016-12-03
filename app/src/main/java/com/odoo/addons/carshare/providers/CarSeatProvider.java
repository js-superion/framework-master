package com.odoo.addons.carshare.providers;

import com.odoo.addons.carshare.models.CarDeparture;
import com.odoo.addons.carshare.models.CarSeat;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Administrator on 2016-11-28.
 */

public class CarSeatProvider extends BaseModelProvider {
    public  static final  String TAG = CarSeatProvider.class.getSimpleName();

    @Override
    public String authority() {
        return CarSeat.AUTHORITY;
    }
}
