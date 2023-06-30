<!-- BEGIN MUNGE: GENERATED_TOC -->
- [v2.0.0-beta.19](#v200-beta19)
   - [Changelog since v2.0.0-beta.18](#changelog-since-v200-beta18)

<!-- END MUNGE: GENERATED_TOC -->



<!-- NEW RELEASE NOTES ENTRY -->
# v2.0.0-beta.19
## Changelog since v2.0.0-beta.18
#### 新增
- [新增] 构建详情页问题优化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8955)
- [新增] framework更新到0.0.7 [链接](http://github.com/TencentBlueKing/bk-ci/issues/9027)
- [新增] 蓝盾国际化优化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8975)
- [新增] 并发组排队的队列长度上限由20调整到200 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8989)
- [新增] shardingDatabase接入micrometer [链接](http://github.com/TencentBlueKing/bk-ci/issues/8999)
- [新增] 蓝盾对接权限中心RBAC优化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8941)
- [新增] 优化容器化启动速度和JVM内存分配 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8960)
- [新增] 蓝盾内置插件支持国际化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8900)
- [新增] auth微服务的初始化数据需支持国际化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8898)
- [新增] JerseyConfig初始化完之后端口才可以访问 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8946)
- [新增] 支持获取构建环境的构建机列表 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8947)
- [新增] 流水线LATEST STATUS 状态取最新次构建的状态 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8907)
- [新增] 流水线构建详情页重构需求 [链接](http://github.com/TencentBlueKing/bk-ci/issues/7983)
- [新增] [stream] [链接](http://github.com/TencentBlueKing/bk-ci/issues/8910)
- [新增] hotfix:蓝盾国际化方案优化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8211)
- [新增] 增加stream开启pac模式的环境配置 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8939)
- [新增] 蓝盾对接权限中心RBAC [链接](http://github.com/TencentBlueKing/bk-ci/issues/7794)
- [新增] 市场插件输入参数支持插件信息相关变量的替换 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8840)
- [新增] 邮件发送后收件人顺序维持用户指定的顺序 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8902)
- [新增] 添加项目管理前端服务 [链接](http://github.com/TencentBlueKing/bk-ci/issues/7923)
- [新增] 流水线国际化相关优化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8683)
- [新增] 优化agent环境变量获取逻辑 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8857)
- [新增] 下线二进制的定时任务 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8888)
- [新增] 增加获取代码库详情openapi接口 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8850)
- [新增] 关闭jvm进程之前等待30秒 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8875)
- [新增] 网关新增强制容器化header [链接](http://github.com/TencentBlueKing/bk-ci/issues/8863)
- [新增] 关联代码库页面优化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8621)
- [新增] 新增环境页面优化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8622)
- [新增] 优化首页文案 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8612)

#### 优化
- [优化] 进入复杂流水线详情页耗时优化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8956)
- [优化] 互斥组支持重试时的占位符重新替换 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8878)
- [优化] 最新动态的时间展示 xxxx-xx-xx hh:mm:ss [链接](http://github.com/TencentBlueKing/bk-ci/issues/8838)

#### 修复
- [修复] 构建详情页针对矩阵场景post插件渲染修复 [链接](http://github.com/TencentBlueKing/bk-ci/issues/9026)
- [修复] 流水线通知信息异常 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8983)
- [修复] 移动端产物二维码下载地址错误 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8966)
- [修复] 新详情页数据问题修复 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8890)
- [修复] 更新流水线模板实例的openapi接口在更新研发商店模板的实例失败 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8913)
- [修复] 国际化优化 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8903)
- [修复] Job启动插件收到取消请求时意外返回失败 [链接](http://github.com/TencentBlueKing/bk-ci/issues/8879)
