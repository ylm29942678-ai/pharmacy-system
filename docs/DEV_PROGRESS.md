# 乡镇药店管理系统 - 开发进度记录

## Session 1：后端工程初始化与基础配置 ✅ 已完成

### 一、项目目录结构

```
pharmacy-system/
├── docs/                          # 项目文档
│   ├── DEV_PROGRESS.md           # 开发进度记录
│   ├── pms_db.sql                # 数据库脚本
│   └── 功能+业务+部署.txt        # 项目说明文档
└── pharmacy-admin/                # 后端项目
    ├── pom.xml                    # Maven 配置文件
    └── src/
        └── main/
            ├── java/com/pharmacy/
            │   ├── PharmacyAdminApplication.java    # 主启动类
            │   ├── common/
            │   │   └── Result.java                  # 统一响应体
            │   ├── controller/
            │   │   └── TestController.java          # 测试控制器
            │   └── exception/
            │       ├── BusinessException.java       # 业务异常类
            │       └── GlobalExceptionHandler.java  # 全局异常处理器
            └── resources/
                └── application.yml                   # 配置文件
```

### 二、数据库连接配置 (application.yml)

```yaml
server:
  port: 8080

spring:
  application:
    name: pharmacy-admin
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/pms_db?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai&useSSL=false
    username: root
    password: 130521

mybatis-plus:
  configuration:
    map-underscore-to-camel-case: true
  global-config:
    db-config:
      id-type: auto
      logic-delete-field: status
      logic-delete-value: 0
      logic-not-delete-value: 1
```

### 三、Result 类核心结构

```java
@Data
public class Result<T> {
    private Integer code;      // 状态码：200-成功，500-失败
    private String message;    // 提示信息
    private T data;            // 返回数据

    // 静态方法：成功响应（无数据）
    public static <T> Result<T> success()

    // 静态方法：成功响应（带数据）
    public static <T> Result<T> success(T data)

    // 静态方法：失败响应
    public static <T> Result<T> error(String message)
}
```

---

## Session 1 测试方法

### 前置条件

1. 确保已安装 **JDK 17** 和 **Maven 3.6+**
2. 确保本地 MySQL 已启动，且已执行 `docs/pms_db.sql` 创建数据库 `pms_db`
3. 根据实际情况修改 `pharmacy-admin/src/main/resources/application.yml` 中的数据库用户名和密码

### 测试步骤

#### 1. 编译项目

```bash
cd pharmacy-admin
mvn clean compile
```

#### 2. 启动后端服务

```bash
mvn spring-boot:run
```

#### 3. 验证接口（使用浏览器或 Postman）

| 接口   | 方法  | URL                                   | 预期结果                                               |
| ---- | --- | ------------------------------------- | -------------------------------------------------- |
| 健康检查 | GET | http://localhost:8080/api/test/health | 返回 `{"code":200,"message":"操作成功","data":"系统运行正常"}` |
| 欢迎接口 | GET | http://localhost:8080/api/test/hello  | 返回包含系统信息的 JSON                                     |

#### 4. 验证数据库连接

启动成功且无报错即表示数据库连接配置正确。

---

## Session 2：基础档案类数据 CRUD 生成 ✅ 已完成

### 一、新增目录结构

```
pharmacy-system/
└── pharmacy-admin/
    └── src/
        └── main/
            └── java/com/pharmacy/
                ├── config/
                │   └── MyMetaObjectHandler.java       # 自动填充处理器
                ├── entity/
                │   ├── Customer.java
                │   ├── Medicine.java
                │   ├── Supplier.java
                │   └── User.java
                ├── mapper/
                │   ├── CustomerMapper.java
                │   ├── MedicineMapper.java
                │   ├── SupplierMapper.java
                │   └── UserMapper.java
                ├── service/
                │   ├── CustomerService.java
                │   ├── MedicineService.java
                │   ├── SupplierService.java
                │   ├── UserService.java
                │   └── impl/
                │       ├── CustomerServiceImpl.java
                │       ├── MedicineServiceImpl.java
                │       ├── SupplierServiceImpl.java
                │       └── UserServiceImpl.java
                └── controller/
                    ├── CustomerController.java
                    ├── MedicineController.java
                    ├── SupplierController.java
                    └── UserController.java
```

### 二、核心 API 路径列表

| 模块    | 基础路径          | 说明         |
| ----- | ------------- | ---------- |
| 顾客管理  | /api/customer | 顾客信息 CRUD  |
| 药品管理  | /api/medicine | 药品信息 CRUD  |
| 供应商管理 | /api/supplier | 供应商信息 CRUD |
| 用户管理  | /api/user     | 用户信息 CRUD  |

每个 API 都包含以下接口：

- POST /api/xxx - 新增
- DELETE /api/xxx/{id} - 删除
- PUT /api/xxx - 更新
- GET /api/xxx/{id} - 根据ID查询
- GET /api/xxx/list?current=1&size=10 - 分页列表

### 三、约束条件实现

#### 1. create_time 和 update_time 自动填充

- **实现方式**：创建 `MyMetaObjectHandler` 类实现 `MetaObjectHandler` 接口
- **配置文件位置**：`com.pharmacy.config.MyMetaObjectHandler`
- **Entity 注解**：在对应字段上使用 `@TableField(fill = FieldFill.INSERT)` 和 `@TableField(fill = FieldFill.INSERT_UPDATE)`

#### 2. 逻辑删除（medicine 和 user 表）

- **实现方式**：使用 MyBatis-Plus 提供的 `@TableLogic` 注解
- **配置位置**：`application.yml` 中已配置全局逻辑删除规则
  
  ```yaml
  mybatis-plus:
    global-config:
      db-config:
        logic-delete-field: status
        logic-delete-value: 0
        logic-not-delete-value: 1
  ```
- **Entity 配置**：在 `Medicine` 和 `User` 实体的 `status` 字段上添加 `@TableLogic` 注解
- **效果**：调用 `removeById()` 方法时，会自动将 `status` 置为 0，而非物理删除

### 四、Session 2 测试方法

#### 前置条件

同 Session 1，确保数据库已创建并连接正常。

#### 测试步骤

1. 启动后端服务
2. 使用 Postman 或浏览器测试各个 API 接口

#### 示例测试：

- **新增顾客**：POST http://localhost:8080/api/customer
- **查询药品列表**：GET http://localhost:8080/api/medicine/list
- **删除用户（逻辑删除）**：DELETE http://localhost:8080/api/user/1

---

## Session 3：采购与销售双表联动业务 ✅ 已完成

### 一、新增目录结构

```
pharmacy-system/
└── pharmacy-admin/
    └── src/
        └── main/
            └── java/com/pharmacy/
                ├── dto/
                │   ├── PurchaseItemDTO.java
                │   ├── PurchaseOrderCreateDTO.java
                │   ├── SaleItemDTO.java
                │   └── SaleOrderCreateDTO.java
                ├── entity/
                │   ├── PurchaseOrder.java
                │   ├── PurchaseItem.java
                │   ├── SaleOrder.java
                │   └── SaleItem.java
                ├── mapper/
                │   ├── PurchaseOrderMapper.java
                │   ├── PurchaseItemMapper.java
                │   ├── SaleOrderMapper.java
                │   └── SaleItemMapper.java
                ├── service/
                │   ├── PurchaseOrderService.java
                │   ├── PurchaseItemService.java
                │   ├── SaleOrderService.java
                │   ├── SaleItemService.java
                │   └── impl/
                │       ├── PurchaseOrderServiceImpl.java
                │       ├── PurchaseItemServiceImpl.java
                │       ├── SaleOrderServiceImpl.java
                │       └── SaleItemServiceImpl.java
                └── controller/
                    ├── PurchaseOrderController.java
                    ├── PurchaseItemController.java
                    ├── SaleOrderController.java
                    └── SaleItemController.java
```

### 二、核心事务接口说明

#### 1. 创建采购单接口（主从表同时插入）

**接口路径**：`POST /api/purchase-order/create`

**方法签名**：
```java
@Transactional(rollbackFor = Exception.class)
public PurchaseOrder createPurchaseOrder(PurchaseOrderCreateDTO dto)
```

**入参 DTO 结构**：
```java
public class PurchaseOrderCreateDTO {
    private Integer supplierId;        // 供应商ID
    private Integer userId;            // 操作员ID
    private LocalDateTime purchaseTime;// 采购时间
    private BigDecimal totalAmount;    // 总金额
    private String payType;            // 支付方式
    private String remark;             // 备注
    private List<PurchaseItemDTO> items; // 采购明细列表
}

public class PurchaseItemDTO {
    private Integer medId;            // 药品ID
    private String batchNo;           // 批号
    private LocalDate productionDate; // 生产日期
    private LocalDate expireDate;     // 有效期
    private Integer purchaseNum;      // 采购数量
    private BigDecimal purchasePrice; // 采购单价
    private BigDecimal totalPrice;    // 小计
    private String cabinet;           // 药柜位置
    private String remark;            // 备注
}
```

**事务处理逻辑**：
- 插入 `purchase_order` 主表记录
- 获取生成的 `purchase_id`
- 批量插入 `purchase_item` 明细表记录（设置 purchase_id）
- 使用 `@Transactional` 保证主从表原子性

---

#### 2. 创建销售单接口（主从表插入 + 更新顾客累计消费）

**接口路径**：`POST /api/sale-order/create`

**方法签名**：
```java
@Transactional(rollbackFor = Exception.class)
public SaleOrder createSaleOrder(SaleOrderCreateDTO dto)
```

**入参 DTO 结构**：
```java
public class SaleOrderCreateDTO {
    private Long orderId;             // 订单号（手动输入）
    private Integer custId;           // 顾客ID（可选）
    private Integer userId;           // 操作员ID
    private BigDecimal totalPrice;    // 订单总价
    private String payType;           // 支付方式
    private Integer orderType;        // 订单类型
    private Integer payStatus;        // 支付状态
    private String remark;            // 备注
    private List<SaleItemDTO> items;  // 销售明细列表
}

public class SaleItemDTO {
    private Integer medId;            // 药品ID
    private String batchNo;           // 批号
    private Integer quantity;         // 销售数量
    private BigDecimal unitPrice;     // 销售单价
    private BigDecimal totalPrice;    // 小计
}
```

**事务处理逻辑**：
- 插入 `sale_order` 主表记录
- 获取 `order_id`
- 批量插入 `sale_item` 明细表记录（设置 order_id）
- 如果传入了 `cust_id`，更新 `customer.total_consume` 累加订单总价
- 使用 `@Transactional` 保证所有操作原子性

### 三、核心 API 路径列表

| 模块      | 基础路径               | 说明                   |
| ------- | ------------------ | -------------------- |
| 采购订单    | /api/purchase-order | 采购订单 CRUD + 事务创建  |
| 采购明细    | /api/purchase-item  | 采购明细 CRUD           |
| 销售订单    | /api/sale-order     | 销售订单 CRUD + 事务创建  |
| 销售明细    | /api/sale-item      | 销售明细 CRUD           |

### 四、Session 3 测试方法

#### 前置条件
同 Session 1，确保：
1. MySQL 已启动，数据库 `pms_db` 已创建
2. 数据库中已有基础数据（supplier、user、medicine、customer）
3. 项目已编译通过

#### 测试步骤

##### 步骤 1：编译并启动后端服务
```bash
cd pharmacy-admin
mvn clean compile
mvn spring-boot:run
```

##### 步骤 2：验证基础数据
首先确保以下基础数据存在（可通过对应 CRUD 接口查询）：
- **供应商**：至少有一个 supplier（supplier_id=1）
- **用户**：至少有两个 user（user_id=1, user_id=2）
- **药品**：至少有一个 medicine（med_id=1）
- **顾客**：至少有一个 customer（cust_id=1）

##### 步骤 3：创建采购单测试

**接口**：POST http://localhost:8080/api/purchase-order/create

**请求头**：Content-Type: application/json

**请求体**：
```json
{
  "supplierId": 1,
  "userId": 1,
  "purchaseTime": "2026-04-18T10:00:00",
  "totalAmount": 164.00,
  "payType": "转账",
  "remark": "月度采购",
  "items": [
    {
      "medId": 1,
      "batchNo": "20260418A",
      "productionDate": "2026-01-01",
      "expireDate": "2028-01-01",
      "purchaseNum": 20,
      "purchasePrice": 8.20,
      "totalPrice": 164.00,
      "cabinet": "西药柜",
      "remark": ""
    }
  ]
}
```

**预期结果**：
- 返回 code=200 的成功响应
- data 中包含创建的采购单信息，purchase_id 已自动生成
- 查询 purchase_order 表，新增一条记录
- 查询 purchase_item 表，新增对应的明细记录

**验证查询接口**：
- GET http://localhost:8080/api/purchase-order/list
- GET http://localhost:8080/api/purchase-order/{purchase_id}

---

##### 步骤 4：创建销售单测试（关联顾客）

**接口**：POST http://localhost:8080/api/sale-order/create

**请求头**：Content-Type: application/json

**请求体**：
```json
{
  "orderId": 202604180001,
  "custId": 1,
  "userId": 2,
  "totalPrice": 31.00,
  "payType": "微信",
  "orderType": 1,
  "payStatus": 1,
  "remark": "会员折扣",
  "items": [
    {
      "medId": 1,
      "batchNo": "20250101A",
      "quantity": 2,
      "unitPrice": 15.50,
      "totalPrice": 31.00
    }
  ]
}
```

**预期结果**：
- 返回 code=200 的成功响应
- data 中包含创建的销售单信息
- 查询 sale_order 表，新增一条记录
- 查询 sale_item 表，新增对应的明细记录
- **重要**：查询 customer 表，cust_id=1 的 total_consume 字段增加了 31.00

**验证查询接口**：
- GET http://localhost:8080/api/sale-order/list
- GET http://localhost:8080/api/sale-order/202604180001
- GET http://localhost:8080/api/customer/1 （验证 total_consume 更新）

---

##### 步骤 5：创建销售单测试（不关联顾客）

**请求体**：
```json
{
  "orderId": 202604180002,
  "userId": 1,
  "totalPrice": 12.00,
  "payType": "现金",
  "orderType": 1,
  "payStatus": 1,
  "remark": "散客",
  "items": [
    {
      "medId": 3,
      "batchNo": "20240815C",
      "quantity": 1,
      "unitPrice": 12.00,
      "totalPrice": 12.00
    }
  ]
}
```

**预期结果**：
- 销售单和明细正常创建
- customer 表无更新（因为没有传入 custId）

---

#### 事务回滚测试（可选）

为了验证事务的正确性，可以模拟错误场景：

1. **修改代码测试**：在 PurchaseOrderServiceImpl 的 saveBatch 之前手动抛出异常
2. **验证结果**：purchase_order 和 purchase_item 都不应该有新增记录

#### 测试数据清理

测试完成后，可以通过以下接口清理测试数据：
- DELETE http://localhost:8080/api/purchase-order/{purchase_id}
- DELETE http://localhost:8080/api/sale-order/{order_id}

注意：由于 purchase_item 和 sale_item 有外键约束，删除主表前需要先删除明细表，或者直接在数据库中清理。

---

## Session 4：库存、盘点逻辑与系统日志 AOP ✅ 已完成

### 一、新增目录结构

```
pharmacy-system/
└── pharmacy-admin/
    └── src/main/java/com/pharmacy/
        ├── annotation/
        │   └── OperateLog.java          # 系统日志注解
        ├── aspect/
        │   └── SysLogAspect.java         # 系统日志 AOP 切面
        ├── config/
        │   └── AsyncConfig.java          # 异步任务配置
        ├── dto/
        │   └── StockCheckCreateDTO.java  # 盘点创建 DTO
        ├── entity/
        │   ├── Stock.java                # 库存实体
        │   ├── StockCheck.java           # 盘点记录实体
        │   └── SysLog.java               # 系统日志实体
        ├── mapper/
        │   ├── StockMapper.java
        │   ├── StockCheckMapper.java
        │   └── SysLogMapper.java
        ├── service/
        │   ├── StockService.java
        │   ├── StockCheckService.java
        │   ├── SysLogService.java
        │   └── impl/
        │       ├── StockServiceImpl.java
        │       ├── StockCheckServiceImpl.java
        │       └── SysLogServiceImpl.java
        └── controller/
            ├── StockController.java
            ├── StockCheckController.java
            └── SysLogController.java
```

### 二、核心功能说明

#### 1. 库存管理 (Stock)
- 按药品和批号管理库存
- 记录库存数量、采购价格、效期、药柜位置等信息

#### 2. 盘点管理 (StockCheck)
- **创建盘点单接口**：`POST /api/stock-check/create`
- 自动计算盈亏数量和盈亏金额
- 盈亏数量 = 实际库存 - 系统库存
- 盈亏金额 = 盈亏数量 × 单位成本价
- 支持批量创建盘点记录

#### 3. 系统日志 AOP (SysLog)
- **@SysLog 注解**：用于标记需要记录日志的 Controller 方法
- **AOP 切面**：拦截带有 @SysLog 注解的方法
- **异步保存**：使用 @Async 异步保存日志，不影响主业务性能
- 记录信息包括：操作模块、操作类型、操作内容、IP地址、操作时间等

### 三、@SysLog 注解使用示例

```java
@PostMapping("/create")
@SysLog(module = "库存管理", type = "新增", content = "创建盘点单")
public Result<List<StockCheck>> create(@RequestBody StockCheckCreateDTO dto) {
    // 业务逻辑
}
```

### 四、核心 API 路径列表

| 模块      | 基础路径               | 说明                   |
| ------- | ------------------ | -------------------- |
| 库存管理    | /api/stock          | 库存信息 CRUD         |
| 盘点管理    | /api/stock-check    | 盘点记录 CRUD + 批量创建 |
| 系统日志    | /api/sys-log        | 系统日志查询           |

### 五、Session 4 测试方法

#### 前置条件
同 Session 1，确保数据库已创建并连接正常。

#### 测试步骤

##### 1. 编译并启动后端服务
```bash
cd pharmacy-admin
mvn clean compile
mvn spring-boot:run
```

##### 2. 测试创建盘点单

**接口**：POST http://localhost:8080/api/stock-check/create

**请求头**：Content-Type: application/json

**请求体**：
```json
{
  "checkNo": "CHECK20260419001",
  "checkUser": 1,
  "checkTime": "2026-04-19T10:00:00",
  "remark": "月度盘点",
  "items": [
    {
      "medId": 1,
      "batchNo": "20250101A",
      "systemStock": 100,
      "actualStock": 98,
      "unitPrice": 8.20,
      "reason": "正常损耗",
      "remark": ""
    },
    {
      "medId": 2,
      "batchNo": "20231205B",
      "systemStock": 10,
      "actualStock": 12,
      "unitPrice": 420.00,
      "reason": "入库差异",
      "remark": ""
    }
  ]
}
```

**预期结果**：
- 返回 code=200 的成功响应
- data 中包含创建的盘点记录列表
- 每条记录自动计算了 profit_loss（盈亏数量）和 profit_loss_amount（盈亏金额）
- 第一条记录：profit_loss=-2, profit_loss_amount=-16.40
- 第二条记录：profit_loss=2, profit_loss_amount=840.00

##### 3. 验证系统日志

**接口**：GET http://localhost:8080/api/sys-log/list

**预期结果**：
- 可以查询到刚才创建盘点单时自动记录的系统日志
- 日志包含操作模块、操作类型、操作内容、IP地址等信息

##### 4. 验证库存列表

**接口**：GET http://localhost:8080/api/stock/list

---

## 后端核心业务（Session 4）开发完毕
