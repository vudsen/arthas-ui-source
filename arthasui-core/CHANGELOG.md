# Changelog

## Unreleased

## 0.5.1 - 2025-06-27

- fix: attach 到 JVM 之后不做操作会自动断开连接 #50
- fix: 执行 watch、trace 等命令时可能会导致输出解析异常 #51
- feat: 刷新 JVM 节点时同时刷新整个列表 #52

## 0.5.0 - 2025-06-24

- feat: 支持 tunnel server #40
- refactor: 移除协程，优化代码性能 #41
- feat: 支持 K8s #42
- ci: K8s 集成测试 #43
- ci: UI 测试 #44
- refactor: 重构 K8s 连接实现 #45
- fix: 创建默认配置时会在 #46
- feat: 手动安装支持 #47
- feat: 优化工具栏交互体验 #48

## 0.4.2 - 2025-05-07

- fix: 连接时报 unknown error #38

## 0.4.1 - 2025-05-06

- feat: 优化下载和上传时的进度条 #29
- fix: 传输本地本机的配置在更新时无法保存 #30
- fix: 下载文件时如果先前下载失败，再次下载时会认为下载成功 #31
- feat: 使用 `-XshowSettings:properties` 搜索 `JAVA_HOME` #33
- test: attach arthas 进程的集成测试 #34
- feat: UI 集成测试 #35
- feat: 当没有 unzip 命令时，自动安装解压工具 #36

## 0.4.0 - 2025-04-25

- chore: 使用合适的默认数据目录 #25
- feat: 支持自动识别数据目录的压缩包 #26
- feat: 支持从本地上传工具链到远程宿主机 #27

## 0.3.1 - 2025-04-24

- fix: 搜索组部分内置方法无法使用 #23

## 0.3.0 - 2025-04-22

- fix: 测试OGNL脚本时在 EDT 线程 #17 
- feat: 打开查询窗口前检查 JVM 是否存活 #18
- refactor: 重构代码架构，使用 jattach 取代 jdk #19
- enhance: 使用 id 辨别每项配置, 支持更新时修改名称 #20
- ci: release 时自动上传 API 文档 #21
- fix: 当 jps 不存在时使用 ps 搜索 jvm 
- fix: 当第一次添加宿主机时，工具栏不会自动刷新 
- fix: 使用 docker 时，若容器大于一个，则会报错

## 0.2.0-alpha - 2025-03-31

- refactor: 重构代码架构 by @vudsen in https://github.com/vudsen/arthas-ui-source/pull/6
- feature: full support of Arthas grammer by @vudsen in https://github.com/vudsen/arthas-ui-source/pull/7
- feat: 当连接长时间不用时，自动关闭 by @vudsen in https://github.com/vudsen/arthas-ui-source/pull/9
- feat: 添加一些 search group 的工具类 by @vudsen in https://github.com/vudsen/arthas-ui-source/pull/8
- feat: 配置的表单验证 by @vudsen in https://github.com/vudsen/arthas-ui-source/pull/10
- fix: windows 上 attach 后没有任何输出 by @vudsen in https://github.com/vudsen/arthas-ui-source/pull/12
- feat: 选中命令时自动去除换行和分号 by @vudsen in https://github.com/vudsen/arthas-ui-source/pull/13
