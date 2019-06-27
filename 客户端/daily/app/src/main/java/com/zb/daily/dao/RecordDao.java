package com.zb.daily.dao;

import android.content.ContentValues;
import com.zb.daily.model.Category;
import com.zb.daily.model.Record;
import org.litepal.crud.DataSupport;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class RecordDao {

    //保存记录
    public boolean saveRecord(Record temp) {
        return temp.save();
    }

    //保存记录集合
    public void saveRecordList(List<Record> recordList) {
        DataSupport.saveAll(recordList);
    }

    //查询记录集合按时间降序排列
    public List<Record> findRecordList() {
        List<Record> recordList = DataSupport.order("date desc, id desc").find(Record.class);
        return recordList;
    }

    //查询记录列表根据资产id
    public List<Record> findRecordListByAssetsId(Integer assetsId) {
        List<Record> recordList = DataSupport.where("assets_id = ?", assetsId.toString()).order("date desc, id desc").find(Record.class);
        return recordList;
    }

    //查询日支出/收入
    public double getDaySummary(Integer type, String date) {
        List<Record> recordList = DataSupport.select("money").where("type = ? and date = ?", type.toString(), date).find(Record.class);
        double sum = 0;
        for (Record record : recordList){
            sum += record.getMoney();
        }
        return sum;
    }

    //查询月支出/收入
    public double getMonthSummary(String month, Integer type) {
        List<Record> recordList = DataSupport.select("money").where("type = ? and date like ?", type.toString(), month + "%").find(Record.class);
        double sum = 0;
        for (Record record : recordList){
            sum += record.getMoney();
        }
        return sum;
    }

    //查询月支出/收入
    public Map<String, Double> getMonthSummaryByCategory(String month, Integer type) {
        List<Record> recordList = DataSupport.where("type = ? and date like ?", type.toString(), month + "%").find(Record.class);
        Map<String, Double> map = new HashMap<>();
        for (Record record : recordList){
            if (map.containsKey(record.getCategory().getName())){
                map.put(record.getCategory().getName(), map.get(record.getCategory().getName()) + record.getMoney());
            }else {
                map.put(record.getCategory().getName(), record.getMoney());
            }
        }
        return map;
    }

    //修改资产
    public boolean updateRecord(Record temp) {
        ContentValues values = new ContentValues();
        values.put("money", temp.getMoney());
        values.put("date", temp.getDate());
        values.put("remark", temp.getRemark());
        values.put("type", temp.getType());
        values.put("category_id", temp.getCategoryId());
        values.put("category_imageId", temp.getCategoryImageId());
        values.put("category_name", temp.getCategoryName());
        values.put("assets_id", temp.getAssetsId());
        values.put("assets_name", temp.getAssetsName());

        return DataSupport.update(Record.class, values, temp.getId()) == 1;
    }

    //删除记录
    public boolean deleteRecord(Integer id) {
        return DataSupport.delete(Record.class, id) == 1;
    }

    public boolean deleteallRecord() {
        return DataSupport.deleteAll(Record.class, "id >0") > 0;
    }
}
