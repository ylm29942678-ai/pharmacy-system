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
