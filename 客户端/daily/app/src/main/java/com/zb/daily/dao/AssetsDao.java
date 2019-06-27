package com.zb.daily.dao;

import android.content.ContentValues;
import android.util.Log;
import com.zb.daily.model.Assets;
import org.litepal.crud.DataSupport;

import java.util.List;


public class AssetsDao {

    //保存资产
    public boolean saveAssets(Assets temp) {
        return temp.save();
    }

    //保存资产集合
    public void saveAssetsList(List<Assets> assetsList){
        DataSupport.saveAll(assetsList);
    }

    //按照资产id查询资产
    public Assets findAssetsById(Integer id){
        Assets assets = DataSupport.find(Assets.class, id);
        return assets;
    }

    //查询所有资产集合
    public List<Assets> findAssetsList() {
        List<Assets> assetsList = DataSupport.findAll(Assets.class);
        return assetsList;
    }

    //按照资产类型查询资产集合
    public List<Assets> findAssetsListByType(Integer type){
        List<Assets> assetsList = DataSupport.where("type = ?", type.toString()).find(Assets.class);
        return assetsList;
    }

    //查询总资产或总负债
    public double getAssetsSummary(Integer type) {
        List<Assets> assetsList = DataSupport.select("balance").where("type = ?", type.toString()).find(Assets.class);
        double sum = 0;
        for (Assets assets : assetsList){
            sum += assets.getBalance();
        }
        return sum;
    }

    //修改资产
    public boolean updateAssets(Assets temp) {
        ContentValues values = new ContentValues();
        values.put("imageId", temp.getImageId());
        values.put("name", temp.getName());
        values.put("balance", temp.getBalance());
        values.put("type", temp.getType());
        values.put("remark", temp.getRemark());

        return DataSupport.update(Assets.class, values, temp.getId()) == 1;
    }

    //替换旧的资产列表
    public void replaceOldList(List<Assets> assetsList) {
        if (assetsList.size() == 0){
            return;
        }
        List<Assets> tempAssets = findAssetsListByType(assetsList.get(0).getType());

        for (int i=0; i<assetsList.size(); i++){
           Assets temp = new Assets();
           temp.setImageId(assetsList.get(i).getImageId());
           temp.setName(assetsList.get(i).getName());
           temp.setBalance(assetsList.get(i).getBalance());
           temp.setType(assetsList.get(i).getType());
           temp.setRemark(assetsList.get(i).getRemark());

           temp.update(tempAssets.get(i).getId());
        }
    }

    //删除资产
    public boolean deleteAssets(Integer id) {
        return DataSupport.delete(Assets.class, id) == 1;
    }

    //减去资产
    public boolean removeBalance(Assets outAssets, String money) {
        ContentValues values = new ContentValues();
        values.put("imageId", outAssets.getImageId());
        values.put("name", outAssets.getName());
        values.put("balance", outAssets.getBalance() - Double.valueOf(money));
        values.put("type", outAssets.getType());
        values.put("remark", outAssets.getRemark());
        Log.d("removeBalance: ", outAssets.toString());
        Log.d("removeBalance: ", values.toString());

        return DataSupport.update(Assets.class, values, outAssets.getId()) == 1;
    }

    //添加资产
    public boolean addBalance(Assets inAssets, String money) {
        ContentValues values = new ContentValues();
        values.put("imageId", inAssets.getImageId());
        values.put("name", inAssets.getName());
        values.put("balance", inAssets.getBalance() + Double.valueOf(money));
        values.put("type", inAssets.getType());
        values.put("remark", inAssets.getRemark());
        Log.d("addBalance: ", inAssets.toString());
        Log.d("addBalance: ", values.toString());
        return DataSupport.update(Assets.class, values, inAssets.getId()) == 1;
    }
}
