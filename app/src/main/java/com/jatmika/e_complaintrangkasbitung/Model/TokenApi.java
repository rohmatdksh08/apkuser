package com.jatmika.e_complaintrangkasbitung.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TokenApi {
    @SerializedName("access_token")
    @Expose
    private String access_token;

    @SerializedName("errors")
    @Expose
    private Object errors;

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("id_penduduk")
    @Expose
    private Integer idPenduduk;


    public String getAccessToken() {
        return access_token;
    }

    public void setAccessToken(String access_token) {
        this.access_token = access_token;
    }

    public Object getErrors() {
        return errors;
    }

    public void setErrors(Object errors) {
        this.errors = errors;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getIdPenduduk() {
        return idPenduduk;
    }

    public void setIdPenduduk(Integer idPenduduk) {
        this.idPenduduk = idPenduduk;
    }
}
