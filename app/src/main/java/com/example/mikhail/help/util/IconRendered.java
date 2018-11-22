package com.example.mikhail.help.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class IconRendered extends DefaultClusterRenderer<CustomMarker> {

    public IconRendered(Context context, GoogleMap map,
                           ClusterManager<CustomMarker> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected void onBeforeClusterItemRendered(CustomMarker item, MarkerOptions markerOptions) {
        markerOptions.icon(item.getMarker().getIcon());
    }

}
