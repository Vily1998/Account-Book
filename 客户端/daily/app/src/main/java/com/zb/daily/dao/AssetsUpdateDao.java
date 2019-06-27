package com.zb.daily.dao;

import com.zb.daily.model.AssetsUpdate;
import org.litepal.crud.DataSupport;

import java.util.List;


public class AssetsUpdateDao {

    //保存资产转账
    public boolean saveAssets(AssetsUpdate temp) {
        return temp.save();
    }

    //查询资产修改列表根据资产id
    public List<AssetsUpdate> findAssetsUpdateListByAssetsId(Integer assetsId) {
        List<AssetsUpdate> assetsUpdateList = DataSupport.where("assets_id = ?", assetsId.toString()).order("date desc, id desc").find(AssetsUpdate.class);
        return assetsUpdateList;
    }
}
