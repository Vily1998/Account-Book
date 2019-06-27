package gin_regAndlog
import (
    db "backend/data"
    "net/http"
    "github.com/gin-gonic/gin"
    "encoding/json"
    "fmt"
)
type Item struct {
    Money float32 `json:"Money" form:"Money"`
    Date string `json:"Date" form:"Date"`
    Remark string `json:"Remark" form:"Remark"`
    Type int `json:"Type" form:"Type"`
    Category_id int `json:"Category_id" form:"Category_id"`
    Asset_id int `json:"Asset_id" form:"Asset_id"`
    Asset_name string `json:"Asset_name" form:"Asset_name"`
    Category_name string `Category_name:"Date" form:"Category_name"`
 }
//开始上传，清空数据库
func Uploadbegin(c *gin.Context) {
    _, err := db.Db.Exec("truncate  table daily")
    if err != nil {
        return
       }
    return 

}
//上传
func Upload(c *gin.Context) {
    money := c.Request.FormValue("Money")
    date := c.Request.FormValue("Date")
    remark :=c.Request.FormValue("Remark")
    types :=c.Request.FormValue("Type")
    category_id :=c.Request.FormValue("Category_id")
    category_name :=c.Request.FormValue("Category_name")
    asset_id :=c.Request.FormValue("Asset_id")
    asset_name :=c.Request.FormValue("Asset_name")
    fmt.Println("m"+money)
    fmt.Println(date)

    rs, err := db.Db.Exec("INSERT INTO daily(Money,Date,Remark,Type,Category_id,Category_name,Asset_id,Asset_name) VALUES (?,?,?,?,?,?,?,?)",money, date,remark,types,category_id,category_name,asset_id,asset_name)
    if err != nil {
        fmt.Println(err)
        return
       }
    _, err = rs.LastInsertId()
    return
      

}
func GetDown() (items []Item, err error) {
    items = make([]Item, 0)
    rows, err := db.Db.Query("SELECT * FROM daily")
    if err != nil {
        return
    }
    for rows.Next(){
        Item := Item{}
        rows.Scan(&Item.Money, &Item.Date, &Item.Remark,&Item.Type, &Item.Category_id, &Item.Category_name,&Item.Asset_id, &Item.Asset_name)
        items = append(items, Item)
    }
    if err = rows.Err(); err != nil {
        return
       }
    return 
}
//下载
func Download(c *gin.Context){

    ra, err := GetDown()
    if err != nil {
     return
    }
    b, err := json.Marshal(ra)
    c.JSON(http.StatusOK,string(b))
    return
}

//设置默认路由当访问一个错误网站时返回
func NotFound(c *gin.Context) {
    c.JSON(http.StatusNotFound, gin.H{
        "status": 404,
        "error":  "404 ,page not exists!",
    })
}