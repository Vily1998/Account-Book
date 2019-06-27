package com.zb.daily.dao;

import com.zb.daily.model.AssetsTransfer;
import org.litepal.crud.DataSupport;

import java.util.List;


public class AssetsTransferDao {

    //保存资产转账
    public boolean saveAssets(AssetsTransfer temp) {
        return temp.save();
    }

    //查询资产转账列表根据资产id
    public List<AssetsTransfer> findAssetsTransferListByAssetsId(Integer assetsId) {
        List<AssetsTransfer> assetsTransferList = DataSupport.where("fromId = ? or toId = ?", assetsId.toString(), assetsId.toString()).order("date desc, id desc").find(AssetsTransfer.class);
        return assetsTransferList;
    }
}
