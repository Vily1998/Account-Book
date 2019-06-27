package data
//用于存储用户信息的结构体，Id,Name,Passwd

import (
	"fmt"
    "database/sql"
    _ "github.com/go-sql-driver/mysql"
)

var Db *sql.DB
func init() {
    var err error
    Db,err =sql.Open("mysql","root:root@(localhost:3306)/test")
	if err != nil {
		panic(fmt.Sprintf("数据库连接失败，err: %s", err))
    }
    err = Db.Ping()
    if err != nil {
        panic(fmt.Sprintf("数据库连接失败，err: %s", err))
    }
    fmt.Println("数据库连接成功")

	Db.SetMaxOpenConns(100)
	Db.SetMaxIdleConns(10)
}