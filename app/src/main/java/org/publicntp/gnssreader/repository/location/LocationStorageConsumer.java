package org.publicntp.gnssreader.repository.location;

import android.annotation.SuppressLint;

import org.publicntp.gnssreader.repository.location.converters.CoordinateConverter;
import org.publicntp.gnssreader.repository.location.converters.LatLongConverter;
import org.publicntp.gnssreader.repository.location.converters.UTMConverter;

/**
 * Created by zac on 2/7/18.
 */

public class LocationStorageConsumer {
    private CoordinateConverter coordinateConverter;

    public LocationStorageConsumer() {
        coordinateConverter = new LatLongConverter();
    }

    public LocationStorageConsumer(CoordinateConverter coordinateConverter) {
        this.coordinateConverter = coordinateConverter;
    }

    @SuppressLint("DefaultLocale")
    public String getString() {
        if (LocationStorage.isPopulated()) {
            double latitude = LocationStorage.getLatitude();
            double longitude = LocationStorage.getLongitude();
            return coordinateConverter.getString(latitude, longitude);
        } else {
            return "--°\n--°";
        }
    }

//    @SuppressLint("DefaultLocale")
//    public String getString2() {
//        if(LocationStorage.isPopulated()) {
//            double latitude = LocationStorage.getLatitude();
//            double longitude = LocationStorage.getLongitude();
//            return coordinateConverter.getString2(latitude, longitude);
//        } else {
//            return "--°";
//        }
//    }

    @SuppressLint("DefaultLocale")
    public String getHumanReadableLocation() {
        return String.format("%.4f, %.4f", LocationStorage.getLatitude(), LocationStorage.getLongitude());
    }

    @SuppressLint("DefaultLocale")
    public String getSharableLocation() {
        return String.format("geo:%.4f,%.4f", LocationStorage.getLatitude(), LocationStorage.getLongitude());
    }

    @SuppressLint("DefaultLocale")
    public String getStringError() {
        if(LocationStorage.isPopulated()) {
            float error = LocationStorage.getAccuracy();
            return String.format("±%.2f", error);
        } else {
            return "±--";
        }
    }
}