package com.odoo.addons.carshare.providers;
import com.odoo.addons.carshare.models.CarPoint;
import com.odoo.core.orm.provider.BaseModelProvider;

/**
 * Created by Administrator on 2016-11-28.
 */

public class PointProvider extends BaseModelProvider {
    public  static final  String TAG = PointProvider.class.getSimpleName();

    @Override
    public String authority() {
        return CarPoint.AUTHORITY;
    }

}
