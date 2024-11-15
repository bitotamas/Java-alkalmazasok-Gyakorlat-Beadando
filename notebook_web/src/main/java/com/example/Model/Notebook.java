package com.example.Model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="gep")
public class Notebook {
    @Id
    private Integer id;
    private String gyarto;
    private String tipus;
    private Integer kijelzo;
    private Integer memoria;
    private Integer merevlemez;
    private String videovezerlo;
    private Integer ar;
    private Integer processzorid;
    private Integer oprendszerid;
    private Integer db;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getGyarto() {
        return gyarto;
    }

    public void setGyarto(String gyarto) {
        this.gyarto = gyarto;
    }

    public String getTipus() {
        return tipus;
    }

    public void setTipus(String tipus) {
        this.tipus = tipus;
    }

    public Integer getKijelzo() {
        return kijelzo;
    }

    public void setKijelzo(Integer kijelzo) {
        this.kijelzo = kijelzo;
    }

    public Integer getMemoria() {
        return memoria;
    }

    public void setMemoria(Integer memoria) {
        this.memoria = memoria;
    }

    public Integer getMerevlemez() {
        return merevlemez;
    }

    public void setMerevlemez(Integer merevlemez) {
        this.merevlemez = merevlemez;
    }

    public String getVideovezerlo() {
        return videovezerlo;
    }

    public void setVideovezerlo(String videovezerlo) {
        this.videovezerlo = videovezerlo;
    }

    public Integer getAr() {
        return ar;
    }

    public void setAr(Integer ar) {
        this.ar = ar;
    }

    public Integer getProcesszorid() {
        return processzorid;
    }

    public void setProcesszorid(Integer processzorid) {
        this.processzorid = processzorid;
    }

    public Integer getOprendszerid() {
        return oprendszerid;
    }

    public void setOprendszerid(Integer oprendszerid) {
        this.oprendszerid = oprendszerid;
    }

    public Integer getDb() {
        return db;
    }

    public void setDb(Integer db) {
        this.db = db;
    }
}
