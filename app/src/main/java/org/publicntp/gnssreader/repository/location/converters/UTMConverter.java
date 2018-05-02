package org.publicntp.gnssreader.repository.location.converters;

import android.annotation.SuppressLint;

import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.geom.Angle;
import gov.nasa.worldwind.geom.coords.UTMCoord;

public class UTMConverter extends CoordinateConverter {
    public String getShortHemisphere(String longHemisphere) {
        return AVKey.NORTH.equals(longHemisphere) ? "N" : "S"; //Taken from UTMCoord.toString
    }

    @SuppressLint("DefaultLocale")
    @Override
    public String getString(double lat, double lon) {
        Angle latAngle = Angle.fromDegreesLatitude(lat);
        Angle lonAngle = Angle.fromDegreesLongitude(lon);

        UTMCoord utmCoord = UTMCoord.fromLatLon(latAngle, lonAngle);
        return String.format("%s %s\n%.2f E\n%.2f N", utmCoord.getZone(), getShortHemisphere(utmCoord.getHemisphere()), utmCoord.getNorthing(), utmCoord.getEasting());
    }
}
