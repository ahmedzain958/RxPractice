package com.zain.practice;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Geocoder;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.util.Locale;

/**
 * Created by Hassan on 26/03/2017.
 */

public class CountryDetector {

    private static final String TAG = "CountryDetector";

    public static final String KEY_PREFERENCE_TIME_UPDATED = "preference_time_updated";
    public static final String KEY_PREFERENCE_CURRENT_COUNTRY = "preference_current_country";
    private static CountryDetector sInstance;
    private final TelephonyManager mTelephonyManager;
    private final LocationManager mLocationManager;
    private final LocaleProvider mLocaleProvider;

    // Used as a default country code when all the sources of country data have failed in the
    // exceedingly rare event that the device does not have a default locale set for some reason.
    private final String DEFAULT_COUNTRY_ISO = "AE";
    private final Context mContext;

    /**
     * Class that can be used to return the user's default locale. This is in its own class so that
     * it can be mocked out.
     */
    public static class LocaleProvider {
        public Locale getDefaultLocale() {
            return Locale.getDefault();
        }
    }

    private CountryDetector(Context context) {
        this(context, (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE),
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE),
                new LocaleProvider());
    }

    private CountryDetector(Context context, TelephonyManager telephonyManager,
                            LocationManager locationManager, LocaleProvider localeProvider) {
        mTelephonyManager = telephonyManager;
        mLocationManager = locationManager;
        mLocaleProvider = localeProvider;
        mContext = context;

    }


    /**
     * Returns the instance of the country detector. {@link #( Context )} must have been
     * called previously.
     *
     * @return the initialized country detector.
     */
    public synchronized static CountryDetector getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CountryDetector(context.getApplicationContext());
        }
        return sInstance;
    }

    public String getCurrentCountryIso() {
        String result = null;
        if (TextUtils.isEmpty(result)) {
            result = getLocationBasedCountryIso();
        }
        if (isNetworkCountryCodeAvailable()) {
            result = getNetworkBasedCountryIso();
        }
        if (TextUtils.isEmpty(result)) {
            result = getSimBasedCountryIso();
        }
        if (TextUtils.isEmpty(result)) {
            result = getLocaleBasedCountryIso();
        }
        if (TextUtils.isEmpty(result)) {
            result = DEFAULT_COUNTRY_ISO;
        }
        return result.toUpperCase(Locale.US);
    }

    /**
     * @return the country code of the current telephony network the user is connected to.
     */
    private String getNetworkBasedCountryIso() {
        return mTelephonyManager.getNetworkCountryIso();
    }

    /**
     * @return the geocoded country code detected by the {@link LocationManager}.
     */
    public String getLocationBasedCountryIso() {
        if (!Geocoder.isPresent()) {
            return null;
        }
        final SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        return sharedPreferences.getString(KEY_PREFERENCE_CURRENT_COUNTRY, null);
    }

    /**
     * @return the country code of the SIM card currently inserted in the device.
     */
    private String getSimBasedCountryIso() {
        return mTelephonyManager.getSimCountryIso();
    }

    /**
     * @return the country code of the user's currently selected locale.
     */
    private String getLocaleBasedCountryIso() {
        Locale defaultLocale = mLocaleProvider.getDefaultLocale();
        if (defaultLocale != null) {
            return defaultLocale.getCountry();
        }
        return null;
    }

    private boolean isNetworkCountryCodeAvailable() {
        // On CDMA TelephonyManager.getNetworkCountryIso() just returns the SIM's country code.
        // In this case, we want to ignore the value returned and fallback to location instead.
        return mTelephonyManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM;
    }


}
