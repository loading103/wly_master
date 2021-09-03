文旅云app说明文档：

（1）打包流程
    a.环境切换
       CommonUrl->DEBUG(线上/线下 切换)
    b.站点切换
      CommonUrl->SiteCode 切换站点信息
（2）第三方账号集成说明
    a.高德地图       账号: 383856285@qq.com
    b.版本更新（daqsoft app版本管理系统）
    c.友盟统计相关appid 	Dqsoft
    ....
(3)注意事项
   a.Json解析，因为后台特殊返回数据，在接口调试得时候注意一下json解析异常。
   原因：使用GsonTypeAdapterFactory拦截了解析异常。


