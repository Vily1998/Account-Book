package com.zb.daily.model;

import org.litepal.annotation.Column;
import org.litepal.crud.DataSupport;


public class AssetsUpdate extends DataSupport {

    @Column(unique = true)
    private Integer id;

    private Double fromMoney;//修改前金额

    private Double toMoney;//修改后金额

    private String date;//修改日期

    private Integer assets_id; //资产id

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Double getFromMoney() {
        return fromMoney;
    }

    public void setFromMoney(Double fromMoney) {
        this.fromMoney = fromMoney;
    }

    public Double getToMoney() {
        return toMoney;
    }

    public void setToMoney(Double toMoney) {
        this.toMoney = toMoney;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getAssetsId() {
        return assets_id;
    }

    public void setAssetsId(Integer assets_id) {
        this.assets_id = assets_id;
    }

    public AssetsUpdate() {
    }

    public AssetsUpdate(Double fromMoney, Double toMoney, String date, Integer assets_id) {
        this.fromMoney = fromMoney;
        this.toMoney = toMoney;
        this.date = date;
        this.assets_id = assets_id;
    }

    @Override
    public String toString() {
        return "AssetsUpdate{" +
                "id=" + id +
                ", fromMoney=" + fromMoney +
                ", toMoney=" + toMoney +
                ", date='" + date + '\'' +
                '}';
    }
}
