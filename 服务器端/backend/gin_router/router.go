package gin_router
import (
    api "backend/gin_api"
    "net/http"
    "github.com/gin-gonic/gin"
)
func InitRouter() {
    gin.SetMode(gin.ReleaseMode)
    //使用gin的Default方法创建一个路由handler
    router := gin.Default()
    router.Use(Cors())
    //设置默认路由当访问一个错误网站时返回
    router.NoRoute(api.NotFound)
    //使用以下gin提供的Group函数为不同的API进行分组
    router.GET("/uploadbegin",api.Uploadbegin)
    v1 := router.Group("update")
    {
        v1.POST("/upload", api.Upload)
        v1.GET("/download", api.Download)
    }
    //监听服务器端口
    router.Run(":8080")
}
// 处理跨域请求,支持options访问

func Cors() gin.HandlerFunc {

	return func(c *gin.Context) {

		method := c.Request.Method

 

		c.Header("Access-Control-Allow-Origin", "*")

		c.Header("Access-Control-Allow-Headers", "Content-Type,AccessToken,X-CSRF-Token, Authorization, Token")

		c.Header("Access-Control-Allow-Methods", "POST, GET, OPTIONS")

		c.Header("Access-Control-Expose-Headers", "Content-Length, Access-Control-Allow-Origin, Access-Control-Allow-Headers, Content-Type")

		c.Header("Access-Control-Allow-Credentials", "true")

 

		//放行所有OPTIONS方法

		if method == "OPTIONS" {

			c.AbortWithStatus(http.StatusNoContent)

		}

		// 处理请求

		c.Next()

	}

}