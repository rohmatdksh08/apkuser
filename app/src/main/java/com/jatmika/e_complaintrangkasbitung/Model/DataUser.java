package com.jatmika.e_complaintrangkasbitung.Model;

public class DataUser {

    private String nik, nama_penduduk, tanggal_lahir, jenis_kelamin, alamat, tempat_lahir, id_penduduk, no_telpon, email;
    private int position;

    public DataUser() {

    }

    public DataUser(int position){
        this.position = position;
    }

    public DataUser(String nama_penduduk, String nik, String tanggal_lahir, String tempat_lahir, String id_penduduk, String jenis_kelamin, String alamat, String no_telpon, String email) {
        this.nik = nik;
        this.nama_penduduk = nama_penduduk;
        this.tanggal_lahir = tanggal_lahir;
        this.tempat_lahir = tempat_lahir;
        this.jenis_kelamin = jenis_kelamin;
        this.alamat = alamat;
        this.id_penduduk = id_penduduk;
        this.no_telpon = no_telpon;
        this.email = email;
    }

    public void setTempat_lahir(String tempat_lahir) {
        this.tempat_lahir = tempat_lahir;
    }

    public String getTempat_lahir() {
        return tempat_lahir;
    }

    public void setTanggal_lahir(String tanggal_lahir) {
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getTanggal_lahir() {
        return tanggal_lahir;
    }

    public void setNama_penduduk(String nama_penduduk) {
        this.nama_penduduk = nama_penduduk;
    }

    public String getNama_penduduk() {
        return nama_penduduk;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getNik() {
        return nik;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setId_penduduk(String id_penduduk) {
        this.id_penduduk = id_penduduk;
    }

    public String getAlamat() {
        return alamat;
    }

    public int getPosition() {
        return position;
    }

    public String getId_penduduk() {
        return id_penduduk;
    }

    public void setNo_telpon(String no_telpon) {
        this.no_telpon = no_telpon;
    }

    public String getNo_telpon() {
        return no_telpon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
