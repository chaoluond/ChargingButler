package com.chaocompany.chargebutler;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Chao on 9/16/2015.
 */
public class MapMenuEventHandler implements PopupMenu.OnMenuItemClickListener {
    Context context;
    GoogleMap mGoogleMap;
    public MapMenuEventHandler(Context context, GoogleMap mGoogleMap){
        this.context =context;
        this.mGoogleMap = mGoogleMap;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.id_map_normal) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            return true;
        }
        if (item.getItemId()==R.id.id_map_satellite){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            return true;

        }

        if (item.getItemId()==R.id.id_map_hybrid) {
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            return true;
        }
        if (item.getItemId()==R.id.id_map_terrain){
            mGoogleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
            return true;

        }

        return false;
    }
}
