package com.jatmika.e_complaintrangkasbitung.API;

public class APIUtility {
    public static final String base_url = "http://192.168.126.94:8000";

    public static API getAPI(){
        return RetrofitClient.getClient(base_url).create(API.class);
    }
}
