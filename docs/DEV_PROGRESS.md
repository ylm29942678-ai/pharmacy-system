# 乡镇药店管理系统 - 开发进度记录

---

## Session 1：后端工程初始化与基础配置 ✅ 已完成

**核心内容：**
- Maven 项目初始化
- 数据库连接配置
- 统一响应体 Result 类
- 全局异常处理

**配置文件：** `pharmacy-admin/src/main/resources/application.yml`

**测试接口：**
- GET /api/test/health
- GET /api/test/hello

---

## Session 2：基础档案类数据 CRUD 生成 ✅ 已完成

**新增模块：**
- 顾客管理 (/api/customer)
- 药品管理 (/api/medicine)
- 供应商管理 (/api/supplier)
- 用户管理 (/api/user)

**核心功能：**
- 标准 CRUD 接口
- create_time/update_time 自动填充
- 药品/用户逻辑删除

---

## Session 3：采购与销售双表联动业务 ✅ 已完成

**新增模块：**
- 采购订单 (/api/purchase-order)
- 采购明细 (/api/purchase-item)
- 销售订单 (/api/sale-order)
- 销售明细 (/api/sale-item)

**核心接口：**
- POST /api/purchase-order/create - 创建采购单（主从表事务）
- POST /api/sale-order/create - 创建销售单（主从表事务 + 顾客累计消费更新）

---

## Session 4：库存、盘点逻辑与系统日志 AOP ✅ 已完成

**新增模块：**
- 库存管理 (/api/stock)
- 盘点管理 (/api/stock-check)
- 系统日志 (/api/sys-log)

**核心功能：**
- POST /api/stock-check/create - 创建盘点单，自动计算盈亏
- @OperateLog 注解 + AOP 切面 + 异步日志保存

**注解示例：**
```java
@PostMapping("/create")
@OperateLog(module = "库存管理", type = "新增", content = "创建盘点单")
public Result<List<StockCheck>> create(@RequestBody StockCheckCreateDTO dto) {
    // 业务逻辑
}
```

---

## 整体项目 API 总览

| 模块 | 基础路径 | 说明 |
|------|----------|------|
| 顾客管理 | /api/customer | 顾客信息 CRUD |
| 药品管理 | /api/medicine | 药品信息 CRUD |
| 供应商管理 | /api/supplier | 供应商信息 CRUD |
| 用户管理 | /api/user | 用户信息 CRUD |
| 采购订单 | /api/purchase-order | 采购订单 CRUD + 事务创建 |
| 采购明细 | /api/purchase-item | 采购明细 CRUD |
| 销售订单 | /api/sale-order | 销售订单 CRUD + 事务创建 |
| 销售明细 | /api/sale-item | 销售明细 CRUD |
| 库存管理 | /api/stock | 库存信息 CRUD |
| 盘点管理 | /api/stock-check | 盘点记录 CRUD + 批量创建 |
| 系统日志 | /api/sys-log | 系统日志查询 |

---

## 测试指南

**前置条件：**
1. JDK 17 + Maven 3.6+
2. MySQL 已启动，执行 docs/pms_db.sql 创建数据库
3. 配置 pharmacy-admin/src/main/resources/application.yml 中的数据库连接

**启动后端：**
```bash
cd pharmacy-admin
mvn clean compile
mvn spring-boot:run
```

---

---

## Session 5：前端工程初始化与 Layout 搭建 ✅ 已完成

**前端工程结构：**
- 根目录：`pharmacy-ui/`
- 技术栈：Vue 3 + Vite + Element Plus + Vue Router + Axios

**路由配置：** `pharmacy-ui/src/router/index.js`
12个路由菜单对应业务模块：
- /dashboard - 控制台
- /customer - 顾客管理
- /medicine - 药品管理
- /supplier - 供应商管理
- /user - 用户管理
- /purchase-order - 采购订单
- /purchase-item - 采购明细
- /sale-order - 销售订单
- /sale-item - 销售明细
- /stock - 库存管理
- /stock-check - 盘点管理
- /sys-log - 系统日志

**Axios 封装：** `pharmacy-ui/src/utils/request.js`
- 统一请求/响应拦截器
- baseURL 配置为 `/api`
- 超时设置 10000ms

**Vite 代理配置：** `pharmacy-ui/vite.config.js`
- `/api` 代理至 `http://localhost:8080`
- 开发端口：3000

**Layout 组件：** `pharmacy-ui/src/layout/index.vue`
- Header：系统标题 + 用户下拉菜单
- Sidebar：12个路由菜单 + 图标
- Main：路由视图容器

## 整体项目架构 ✅ 已搭建完成

---

## Session 6：基础档案页面开发（一）✅ 已完成

**新增文件：**
- `pharmacy-ui/src/api/supplier.js - 供应商 API 封装
- `pharmacy-ui/src/api/customer.js - 顾客 API 封装
- `pharmacy-ui/src/api/user.js - 用户 API 封装
- `pharmacy-ui/src/views/supplier/index.vue - 供应商管理页面
- `pharmacy-ui/src/views/customer/index.vue - 顾客管理页面
- `pharmacy-ui/src/views/user/index.vue - 用户管理页面

**统一页面风格：**
- 顶部：查询表单（支持重置
- 中部：数据表格（支持多选、分页
- 底部：操作按钮（新增、批量删除
- 表单：Dialog 弹窗（支持新增/编辑

**供应商页面 - 表单字段：**
- 供应商名称（必填）、简称（必填）、联系人（必填）、手机号（必填，格式校验）、电话
- 地址（textarea）、营业执照号、供应类型、银行信息、状态（启用/禁用）、备注（textarea）

**顾客页面 - 表单字段：**
- 顾客姓名（必填）、手机号（必填，格式校验）、是否会员（单选）、会员等级、累计消费
- 生日（日期选择）、地址（textarea）、状态（启用/禁用）、备注（textarea）

**用户页面 - 表单字段：**
- 用户名（必填，编辑时不可改）、密码（必填，最少6位）、真实姓名（必填）、角色（下拉选择）
- 手机号（必填，格式校验）、状态（启用/禁用）、备注（textarea）

**API 封装规范：**
- 统一从 src/api/ 目录下按模块划分
- 导出 getXxxList、getXxxById、addXxx、updateXxx、deleteXxx 标准方法
- 使用 src/utils/request.js 统一请求拦截器处理
