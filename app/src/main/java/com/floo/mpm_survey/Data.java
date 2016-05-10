package com.floo.mpm_survey;

/**
 * Created by Floo on 2/4/2016.
 */
public class Data {
    
    public static String logUrl = "http://apps.mpm-motor.com/vendorws/Login.svc/VendorLogin/FLOO_SURVEY/FLOOVNDR";

    //dataUrl = "http://apps.mpm-motor.com/vendorws/MarketingSurvey.svc/getSurveysBySurveyor/<username>";
    //public static String dataUrl = "http://apps.mpm-motor.com/vendorws/MarketingSurvey.svc/getSurveysBySurveyor/KRISHNA";

    public static String dataUrl = "http://apps.mpm-motor.com/vendorws/MarketingSurvey.svc/getSurveysBySurveyor/";
    //loginUrl = "http://apps.mpm-motor.com/vendorws/MarketingSurvey.svc/GetLoginSurveyors/<username>/<password>";
    public static String loginUrl = "http://apps.mpm-motor.com/vendorws/MarketingSurvey.svc/GetLoginSurveyors/";

    public static String postUrl = "http://apps.mpm-motor.com/vendorws/MarketingSurvey.svc/AddRespondenAnswer";
    public static String uploadImageUrl = "http://apps.mpm-motor.com/vendorws/MarketingSurvey.svc/UploadImage";

    public static String header = "x-header_vendor_key";

    public static String SURVEYOR_ID = "";
}
