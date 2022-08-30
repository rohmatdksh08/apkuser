package com.jatmika.e_complaintrangkasbitung.Model;

public class Penduduk {
    private String nama_penduduk, jenis_kelamin, tempat_lahir, tanggal_lahir, alamat;
    private Integer nik, id_penduduk;
    private int position;
    public Penduduk(){

    }

    public  Penduduk(int position){
        this.position = position;
    }

    public Penduduk(String nama_penduduk, String jenis_kelamin, String tempat_lahir, String tanggal_lahir, String alamat, Integer nik, Integer id_penduduk){
        this.nama_penduduk = nama_penduduk;
        this.id_penduduk = id_penduduk;
        this.jenis_kelamin = jenis_kelamin;
        this.tempat_lahir = tempat_lahir;
        this.tanggal_lahir = tanggal_lahir;
        this.alamat = alamat;
        this.nik = nik;
    }

    public Integer getId_penduduk() {
        return id_penduduk;
    }

    public void setId_penduduk(Integer id_penduduk) {
        this.id_penduduk = id_penduduk;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setJenis_kelamin(String jenis_kelamin) {
        this.jenis_kelamin = jenis_kelamin;
    }

    public String getJenis_kelamin() {
        return jenis_kelamin;
    }

    public void setNik(Integer nik) {
        this.nik = nik;
    }

    public Integer getNik() {
        return nik;
    }

    public void setNama_penduduk(String nama_penduduk) {
        this.nama_penduduk = nama_penduduk;
    }

    public String getNama_penduduk() {
        return nama_penduduk;
    }

    public void setTanggal_lahir(String tanggal_lahir) {
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getTanggal_lahir() {
        return tanggal_lahir;
    }

    public void setTempat_lahir(String tempat_lahir) {
        this.tempat_lahir = tempat_lahir;
    }

    public String getTempat_lahir() {
        return tempat_lahir;
    }

}
