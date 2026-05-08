# 乡镇药店轻量化管理系统 V1.0 - 开发进度

---

## 项目概述

- **技术栈**：Spring Boot 3.x (JDK 17) + Vue 3 + Vite + Element Plus + MyBatis-Plus + MySQL 8.0
- **数据库**：pms_db (utf8mb4)
- **开发模式**：10 步法分步开发 + 会话间进度交接
- **当前状态**：✅ 已完成全部开发

---

## 阶段一：后端基础设施与单表业务 (Sessions 1-2) ✅

### Session 1：后端工程初始化与基础配置 ✅

#### 已完成工作
1. 初始化 Spring Boot 3.x 项目 `pharmacy-admin`，引入 MyBatis-Plus、MySQL、Lombok、Spring Web
2. 配置 `application.yml` 连接本地 `pms_db` 数据库
3. 编写全局异常处理器 (GlobalExceptionHandler) 和统一响应体 (Result 类)

#### Result 类核心结构
```java
{
  code: Integer,      // 200=成功，其他=失败
  message: String,    // 提示信息
  data: T             // 返回数据
}
```

#### 项目目录结构
```
pharmacy-admin/
├── src/main/java/com/pharmacy/
│   ├── annotation/     # @OperateLog 操作日志注解
│   ├── aspect/       # SysLogAspect AOP 切面
│   ├── common/       # Result 统一响应体
│   ├── config/       # MyBatis-Plus、异步、元数据处理配置
│   ├── controller/   # 11个Controller
│   ├── dto/          # DTO对象
│   ├── entity/       # 11个实体类
│   ├── exception/    # 全局异常处理
│   ├── mapper/       # MyBatis-Plus Mapper
│   └── service/      # Service接口 + Impl
└── pom.xml
```

---

### Session 2：基础档案类数据 CRUD 生成 ✅

#### 已完成工作
为 customer、medicine、supplier、user 四张表生成完整的 CRUD 代码。

#### Entity 自动填充配置
使用 `MyMetaObjectHandler` 实现：
- `create_time` - 插入时自动填充当前时间
- `update_time` - 插入和更新时自动填充当前时间

#### 逻辑删除实现
`medicine` 和 `user` 表使用 MyBatis-Plus 逻辑删除：
- 实体字段标记 `@TableLogic`
- 删除操作改为更新 status 字段为 0
- 查询时自动过滤 status = 0 的记录

#### 核心 API 路径
| 模块 | API 基础路径 | CRUD 方法 |
|-----|-------------|---------|
| 顾客 | `/api/customer` | GET(list/{id}), POST(add), PUT(update), DELETE({id}) |
| 药品 | `/api/medicine` | GET(list/{id}), POST(add), PUT(update), DELETE({id}) |
| 供应商 | `/api/supplier` | GET(list/{id}), POST(add), PUT(update), DELETE({id}) |
| 用户 | `/api/user` | GET(list/{id}), POST(add), PUT(update), DELETE({id}) |

---

## 阶段二：后端核心进销存与日志逻辑 (Sessions 3-4) ✅

### Session 3：采购与销售双表联动业务 ✅

#### 已完成工作
1. 生成 purchase_order/purchase_item 和 sale_order/sale_item 的后端基础代码
2. 编写带有 `@Transactional` 的"创建采购单"接口（主从表同时插入）
3. 编写带有 `@Transactional` 的"创建销售单"接口（主从表插入，并更新 customer 表的 total_consume）

#### 创建采购单接口
```java
@PostMapping("/create")
@Transactional(rollbackFor = Exception.class)
public Result<List<PurchaseItem>> createPurchaseOrder(@RequestBody PurchaseOrderCreateDTO dto)
```

入参 DTO 结构：
```java
PurchaseOrderCreateDTO {
  supplierId: Integer,
  userId: Integer,
  purchaseTime: LocalDateTime,
  totalAmount: BigDecimal,
  payType: String,
  remark: String,
  items: List<PurchaseItemDTO>  // 明细列表
}

PurchaseItemDTO {
  purchaseId: Long,
  medId: Integer,
  batchNo: String,
  productionDate: LocalDate,
  expireDate: LocalDate,
  purchaseNum: Integer,
  purchasePrice: BigDecimal,
  totalPrice: BigDecimal,
  cabinet: String
}
```

#### 创建销售单接口
```java
@PostMapping("/create")
@Transactional(rollbackFor = Exception.class)
public Result<List<SaleItem>> createSaleOrder(@RequestBody SaleOrderCreateDTO dto)
```

入参 DTO 结构：
```java
SaleOrderCreateDTO {
  custId: Integer,
  userId: Integer,
  createTime: LocalDateTime,
  totalPrice: BigDecimal,
  payType: String,
  orderType: Integer,
  payStatus: Integer,
  remark: String,
  items: List<SaleItemDTO>  // 明细列表
}

SaleItemDTO {
  orderId: Long,
  medId: Integer,
  batchNo: String,
  quantity: Integer,
  unitPrice: BigDecimal,
  totalPrice: BigDecimal
}
```

#### 事务处理逻辑
- **采购单创建**：先插入主表获取 ID，再批量插入明细，全部成功后提交，失败回滚
- **销售单创建**：先插入主表获取 ID，再批量插入明细，最后更新 customer 表 total_consume，全部成功后提交，失败回滚

---

### Session 4：库存、盘点逻辑与系统日志 AOP ✅

#### 已完成工作
1. 生成 stock、stock_check、sys_log 的后端代码
2. 编写盘点接口：根据传入的账面和实际库存，自动计算并存储 profit_loss 和 profit_loss_amount
3. 编写 Spring AOP 全局切面，拦截 Controller 层指定注解，静默捕获操作日志并异步存入 sys_log

#### AOP 切面核心逻辑
- **触发条件**：方法上存在 `@OperateLog` 注解
- **核心功能**：
  - 记录操作时间、操作用户、操作模块、操作类型、操作内容
  - 支持异步写入数据库（`@Async`）
  - 异常不影响主业务流程
```java
@Aspect
@Component
public class SysLogAspect {
  @Around("@annotation(operateLog)")
  public Object logAround(ProceedingJoinPoint joinPoint, OperateLog operateLog) {
    // 记录日志
    asyncSaveSysLog(...) 
    return joinPoint.proceed()
  }
}
```

#### 盘点接口计算逻辑
```java
profit_loss = actual_stock - system_stock  // 盈亏数量
profit_loss_amount = profit_loss * unit_price  // 盈亏金额
```

#### 后端完整 API 统计
| 模块 | API 路径 | 说明 |
|-----|---------|-----|
| 顾客 | `/api/customer/*` | CRUD |
| 药品 | `/api/medicine/*` | CRUD |
| 供应商 | `/api/supplier/*` | CRUD |
| 用户 | `/api/user/*` | CRUD + login |
| 采购订单 | `/api/purchase-order/*` | CRUD + create(事务) |
| 采购明细 | `/api/purchase-item/*` | CRUD + list?purchaseId= |
| 销售订单 | `/api/sale-order/*` | CRUD + create(事务) |
| 销售明细 | `/api/sale-item/*` | CRUD + list?orderId= |
| 库存 | `/api/stock/*` | CRUD + list/with-medicine |
| 盘点 | `/api/stock-check/*` | CRUD + create |
| 系统日志 | `/api/sys-log/*` | CRUD + list |
| 控制台 | `/api/dashboard/*` | statistics, recent-orders, recent-logs |

---

## 阶段三：前端基础设施与静态档案页 (Sessions 5-7) ✅

### Session 5：前端工程初始化与 Layout 搭建 ✅

#### 已完成工作
1. 初始化 Vue 3 + Vite + Element Plus 的前端工程 `pharmacy-ui`
2. 配置 `vite.config.js` 的 `/api` 代理
3. 搭建包含 Header 和左侧 12 个路由菜单 Sidebar 的基础 Layout 骨架
4. 新增登录页面
5. 实现路由权限拦截

#### vite.config.js 代理配置
```javascript
proxy: {
  '/api': {
    target: 'http://localhost:8080',
    changeOrigin: true
  }
}
```

#### 路由表结构
```javascript
{
  path: '/',
  component: Layout,
  redirect: '/dashboard',
  children: [
    { path: '/dashboard', name: '控制台', meta: { icon: 'Monitor' } },
    { path: '/customer', name: '顾客管理', meta: { icon: 'User' } },
    { path: '/medicine', name: '药品管理', meta: { icon: 'Box' } },
    { path: '/supplier', name: '供应商管理', meta: { icon: 'OfficeBuilding' } },
    { path: '/user', name: '用户管理', meta: { icon: 'Avatar', roles: ['admin'] } },
    { path: '/purchase-order', name: '采购订单', meta: { icon: 'Document' } },
    { path: '/purchase-order/form', name: '采购单编辑', meta: { hidden: true } },
    { path: '/purchase-item', name: '采购明细', meta: { icon: 'Tickets' } },
    { path: '/sale-order', name: '销售订单', meta: { icon: 'ShoppingCart' } },
    { path: '/sale-order/form', name: '销售单编辑', meta: { hidden: true } },
    { path: '/sale-item', name: '销售明细', meta: { icon: 'List' } },
    { path: '/stock', name: '库存管理', meta: { icon: 'Goods' } },
    { path: '/stock-check', name: '盘点管理', meta: { icon: 'DataAnalysis' } },
    { path: '/sys-log', name: '系统日志', meta: { icon: 'Document', roles: ['admin'] } }
  ]
}
```

#### Axios 请求封装
- 位置：`src/utils/request.js`
- 功能：统一请求前缀、响应拦截、错误处理

---

### Session 6：基础档案页面开发（一）✅

#### 已完成工作
开发了三个统一风格的页面：
1. 供应商页面 (`supplier/index.vue`) ✅ 功能完整
2. 顾客页面 (`customer/index.vue`) ✅ 功能完整
3. 用户页面 (`user/index.vue`) ✅ 功能完整

#### 统一页面结构
- **顶部**：查询表单（支持 clearable）
- **中部**：数据表格（border + stripe，分页）
- **底部**：操作按钮（新增、批量删除、编辑、删除）
- **弹窗**：Dialog 表单，带表单校验

#### 表单校验规则示例
```javascript
rules: {
  name: [{ required: true, message: '必填', trigger: 'blur' }],
  phone: [{ pattern: /^1\d{10}$/, message: '手机号格式', trigger: 'blur' }]
}
```

#### API 分离规范
所有 API 请求剥离到 `src/api/` 目录下的独立 js 文件中。

---

### Session 7：基础档案页面开发（二）- 药品管理 ✅

#### 已完成工作
开发药品信息页面 (`medicine/index.vue`) ✅ 功能完整

#### Tag 颜色映射规则
| 状态 | 值 | Tag 类型 | 说明 |
|-----|---|---------|-----|
| 在售 | 1 | success (绿色) | 正常销售 |
| 停售 | 0 | danger (红色) | 已下架/逻辑删除 |

#### 药品页面功能
- 支持 `med_type` 下拉选择（中药、西药、器械）
- 列表显示药品状态标签
- 查询：药品名称、类型、状态
- 表格：支持批量选择、排序、分页

---

## 阶段四：前端核心业务与主子页面联动 (Sessions 8-9) ✅

### Session 8：采购与销售的单据联动页面 ✅

#### 已完成工作
1. 采购订单主表页面 ✅ 功能完整
2. 销售订单主表页面 ✅ 功能完整
3. 采购明细表页面 ✅ 功能完整
4. 销售明细表页面 ✅ 功能完整
5. 主子表联动跳转 ✅ 已实现

#### 路由参数规则

| 场景 | 路径 | 参数传递 | 解析方式 |
|-----|-----|---------|---------|
| 采购订单 → 采购明细 | `/purchase-item` | `?purchase_id={订单ID}` | `route.query.purchase_id` |
| 销售订单 → 销售明细 | `/sale-item` | `?order_id={订单ID}` | `route.query.order_id` |
| 明细返回主表 | 对应主表路由 | - | 按钮直接跳转 |

#### 主表→明细联动流程
1. 主表点击「查看明细」按钮
2. `router.push({ path: '/xxx-item', query: { purchase_id/order_id: id } })
3. 明细页 `onMounted` 解析 URL 参数
4. 调用 `getPurchaseItemList({ purchaseId })` 或 `getSaleItemList({ orderId })`
5. 新增明细时自动填充关联 ID 并禁用编辑

#### 明细页新增功能
提供「返回主表」按钮，点击直接跳转回对应主表列表页。

---

### Session 9：采购销售单录入功能 + 库存盘点管理 ✅

#### 新增后端
| 文件 | 功能 |
|-----|------|
| `StockVO.java` | 库存视图对象，包含药品关联信息和预警状态 |
| `StockController.java` | 新增 `/list/with-medicine` 接口 |
| `StockMapper.java` | 新增关联查询方法 |
| `StockService.java & StockServiceImpl.java` | 近效期(30天内)和低库存判断逻辑 |
| `StockCheckController.java` | 优化 `/create` 接口，支持字符串日期格式 |

#### 新增前端 API
| 文件 | 功能 |
|-----|------|
| `stock.js` | 库存管理 API |
| `stock-check.js` | 盘点管理 API |
| `dashboard.js` | 控制台统计 API |
| `sys-log.js` | 系统日志 API |
| `auth.js` | 登录认证 API |

#### 库存管理页面 ✅ 功能完整
- 显示库存列表，关联药品信息
- 近效期(30天内)红色标签预警
- 低于预警线黄色标签预警
- 预警行整行红色背景

#### 盘点管理页面 ✅ 功能完整（含优化）
- 盘点列表展示
- 新增盘点功能，采用卡片式多明细布局（已优化）
- 药品选择改为下拉菜单，显示「药品名称(批号)」（已优化）
- 输入实际库存实时计算盈亏数量和金额
- 盈亏信息彩色卡片展示：绿色(正)、红色(负)、灰色(无)（已优化）
- 有盈亏时强制校验填写盈亏原因，输入框变红并显示警告提示（已优化）
- 界面更清晰，不再拥挤（已优化）

#### 前端计算盈亏的核心逻辑
```javascript
// 盈亏数量计算
const profitLoss = computed(() => actualStock.value - systemStock.value)

// 盈亏金额计算
const profitLossAmount = computed(() => {
  return Number((profitLoss.value * unitPrice.value).toFixed(2)
})

// 显示颜色
// 正盈亏：绿色 (#67c23a)
// 负盈亏：红色 (#f56c6c)
// 无盈亏：灰色 (#909399)

// 提交校验
if (profitLoss !== 0 && !reason) {
  ElMessage.error('有盈亏请填写盈亏原因')
  return false
}
```

---

## 阶段五：系统闭环与权限控制 (Session 10) ✅

### Session 10：控制台工作台与权限路由拦截 ✅

#### 已完成工作
1. ✅ Dashboard 控制台页面
   - 布局数据卡片（当日营业额、订单总数、库存与近效期预警
   - 快捷操作入口（快速开单、快速盘点等）
   - 最新动态列表（最新订单、最新操作日志）

2. ✅ 系统日志页面完善
   - 创建 `src/api/sys-log.js` API 文件
   - 实现日志列表展示（分页、查询）
   - 仅管理员可见（权限控制）

3. ✅ 权限拦截闭环
   - 创建登录页面 `/login`
   - 在 `router/index.js` 添加全局前置守卫 `beforeEach`
   - 未登录用户重定向到登录页
   - 登录成功后存储用户信息到 localStorage
   - 根据用户 role 字段动态隐藏菜单：
     - 店员角色（role = 'staff'）隐藏「系统日志」和「用户管理」菜单项
     - 管理员角色（role = 'admin'）显示全部菜单
   - 后端添加登录接口 `/api/user/login`

4. ✅ 控制台后端接口
   - 创建 `DashboardController.java`
   - `/api/dashboard/statistics` - 获取统计数据
   - `/api/dashboard/recent-orders` - 获取最新订单
   - `/api/dashboard/recent-logs` - 最新日志

---

## 已完成页面统计

| 序号 | 页面 | 路由 | 状态 | 说明 |
|-----|-----|-----|-----|-----|
| 1 | 登录页 | `/login` | ✅ | 功能完整，权限控制 |
| 2 | 控制台 | `/dashboard` | ✅ | 功能完整，数据统计 |
| 3 | 供应商 | `/supplier` | ✅ | 功能完整 |
| 4 | 顾客/会员 | `/customer` | ✅ | 功能完整 |
| 5 | 用户管理 | `/user` | ✅ | 功能完整，仅管理员可见 |
| 6 | 药品信息 | `/medicine` | ✅ | 功能完整 |
| 7 | 采购订单 | `/purchase-order` | ✅ | 功能完整 |
| 8 | 采购单录入 | `/purchase-order/form` | ✅ | 功能完整 |
| 9 | 采购明细 | `/purchase-item` | ✅ | 功能完整 |
| 10 | 销售订单 | `/sale-order` | ✅ | 功能完整 |
| 11 | 销售单录入 | `/sale-order/form` | ✅ | 功能完整 |
| 12 | 销售明细 | `/sale-item` | ✅ | 功能完整 |
| 13 | 库存管理 | `/stock` | ✅ | 功能完整 |
| 14 | 库存盘点 | `/stock-check` | ✅ | 功能完整（已优化界面） |
| 15 | 系统日志 | `/sys-log` | ✅ | 功能完整，仅管理员可见 |

**进度**：✅ 15/15 页面全部完成！系统开发完成。**

---

## 项目文件清单

### 后端文件
```
pharmacy-admin/src/main/java/com/pharmacy/
├── annotation/OperateLog.java
├── aspect/SysLogAspect.java
├── common/Result.java
├── config/
│   ├── AsyncConfig.java
│   ├── MyMetaObjectHandler.java
│   └── MybatisPlusConfig.java
├── controller/
│   ├── CustomerController.java
│   ├── MedicineController.java
│   ├── PurchaseItemController.java
│   ├── PurchaseOrderController.java
│   ├── SaleItemController.java
│   ├── SaleOrderController.java
│   ├── StockCheckController.java
│   ├── StockController.java
│   ├── SupplierController.java
│   ├── SysLogController.java
│   ├── UserController.java
│   └── DashboardController.java
├── dto/
│   ├── PurchaseItemDTO.java
│   ├── PurchaseOrderCreateDTO.java
│   ├── SaleItemDTO.java
│   ├── SaleOrderCreateDTO.java
│   └── StockCheckCreateDTO.java
├── entity/
│   ├── Customer.java
│   ├── Medicine.java
│   ├── PurchaseItem.java
│   ├── PurchaseOrder.java
│   ├── SaleItem.java
│   ├── SaleOrder.java
│   ├── Stock.java
│   ├── StockCheck.java
│   ├── Supplier.java
│   ├── SysLog.java
│   └── User.java
├── exception/
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── mapper/
│   ├── CustomerMapper.java
│   ├── MedicineMapper.java
│   ├── PurchaseItemMapper.java
│   ├── PurchaseOrderMapper.java
│   ├── SaleItemMapper.java
│   ├── SaleOrderMapper.java
│   ├── StockCheckMapper.java
│   ├── StockMapper.java
│   ├── SupplierMapper.java
│   ├── SysLogMapper.java
│   └── UserMapper.java
├── service/
│   ├── CustomerService.java
│   ├── MedicineService.java
│   ├── PurchaseItemService.java
│   ├── PurchaseOrderService.java
│   ├── SaleItemService.java
│   ├── SaleOrderService.java
│   ├── StockCheckService.java
│   ├── StockService.java
│   ├── SupplierService.java
│   ├── SysLogService.java
│   ├── UserService.java
│   └── impl/
│       ├── CustomerServiceImpl.java
│       ├── MedicineServiceImpl.java
│       ├── PurchaseItemServiceImpl.java
│       ├── PurchaseOrderServiceImpl.java
│       ├── SaleItemServiceImpl.java
│       ├── SaleOrderServiceImpl.java
│       ├── StockCheckServiceImpl.java
│       ├── StockServiceImpl.java
│       ├── SupplierServiceImpl.java
│       ├── SysLogServiceImpl.java
│       └── UserServiceImpl.java
└── vo/StockVO.java
```

### 前端文件
```
pharmacy-ui/src/
├── api/
│   ├── customer.js
│   ├── medicine.js
│   ├── purchase-item.js
│   ├── purchase-order.js
│   ├── sale-item.js
│   ├── sale-order.js
│   ├── supplier.js
│   ├── user.js
│   ├── stock.js
│   ├── stock-check.js
│   ├── dashboard.js
│   ├── sys-log.js
│   └── auth.js
├── layout/index.vue
├── router/index.js
├── utils/request.js
├── views/
│   ├── login/index.vue
│   ├── dashboard/index.vue
│   ├── customer/index.vue
│   ├── medicine/index.vue
│   ├── purchase-item/index.vue
│   ├── purchase-order/
│   │   ├── index.vue
│   │   └── form.vue
│   ├── sale-item/index.vue
│   ├── sale-order/
│   │   ├── index.vue
│   │   └── form.vue
│   ├── stock/index.vue
│   ├── stock-check/index.vue
│   ├── supplier/index.vue
│   ├── sys-log/index.vue
│   └── user/index.vue
├── App.vue
└── main.js
```

---

## 项目启动运行指南

### 开发环境启动

#### 后端启动
```bash
cd pharmacy-admin
mvn clean install
mvn spring-boot:run
# 或直接运行 PharmacyAdminApplication.java
```
后端运行在 `http://localhost:8080`

#### 前端启动
```bash
cd pharmacy-ui
npm install
npm run dev
```
前端运行在 `http://localhost:5173`

### 测试账号
- **管理员账号**：admin / 123456（可查看全部菜单）
- **店员账号**：staff / 123456（隐藏「用户管理」和「系统日志」）

### 生产部署

#### 1. 数据库部署
```sql
CREATE DATABASE pms_db CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE pms_db;
SOURCE pms_db.sql;
```

#### 2. 后端打包
```bash
cd pharmacy-admin
mvn clean package -DskipTests
# 生成 pharmacy-admin/target/pharmacy-admin-1.0.0.jar
```

#### 3. 前端打包
```bash
cd pharmacy-ui
npm install
npm run build
# 生成 dist/ 目录
```

#### 4. Nginx 配置
```nginx
server {
    listen 80;
    
    location / {
        root /path/to/dist;
        index index.html;
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
    }
}
```

---

## 业务流程参考

### 1. 采购入库流程
1. 在「药品信息」维护药品资料
2. 在「供应商」维护供应商档案
3. 在「采购订单」点击「新增」创建采购单
4. 录入采购明细，选择药品、填写数量和进价
5. 保存后系统自动事务处理，主从表同时插入

### 2. 销售开单流程
1. 可选择在「顾客/会员」登记会员信息
2. 在「销售订单」点击「新增」创建销售单
3. 录入销售明细，系统自动汇总总金额
4. 保存后系统自动更新会员累计消费

### 3. 库存盘点流程
1. 在「库存管理」查看库存和预警信息
2. 发现问题后，在「库存盘点」创建盘点单
3. 选择药品（下拉选择），系统自动填充账面库存和单价
4. 录入实际库存，实时显示盈亏情况
5. 如有盈亏，必须填写原因（输入框会变红并提示）
6. 保存完成盘点记录

---

## 附录：Tag 颜色规范

| Tag 类型 | 颜色 | 使用场景 |
|---------|-----|---------|
| success | 绿色 | 正常状态、在售、正盈亏 |
| warning | 黄色 | 库存不足 |
| danger | 红色 | 停售、近效期、负盈亏 |
| info | 灰色 | 中性信息、无盈亏 |

---

## ✅ **项目完成总结**

乡镇药店轻量化管理系统 V1.0 已于 Session 10 完成全部开发工作！

**主要功能特点：**
1. ✅ 完整的进销存业务管理（采购、销售、库存、盘点）
2. ✅ 美观的控制台数据看板
3. ✅ 完善的权限控制（管理员/店员双角色）
4. ✅ 系统操作日志记录
5. ✅ 响应式布局设计，操作简单易用
6. ✅ 完整的测试数据支持

**系统已可投入生产使用！
