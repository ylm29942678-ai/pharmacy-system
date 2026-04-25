# 乡镇药店管理系统 - 开发进度

---

## Session 1-4：后端开发 ✅
- Session1：工程初始化、统一响应、异常处理
- Session2：基础档案CRUD（顾客、药品、供应商、用户）
- Session3：采购销售双表联动
- Session4：库存盘点、系统日志AOP

**API模块**：顾客、药品、供应商、用户、采购订单、采购明细、销售订单、销售明细、库存、盘点、日志

---

## Session5-7：前端开发 ✅
- Session5：Vue3+Vite+ElementPlus工程、Layout布局、路由配置
- Session6：供应商、顾客、用户管理页面
- Session7：药品管理页面（筛选、排序、停售Tag）

**已完成页面（4/12）**：
✅ 供应商 ✅ 顾客 ✅ 用户 ✅ 药品

**Tag规范**：状态1=绿色(success)，状态0=红色(danger)

---

## Session8：采购销售单据联动 ✅

### 新增API文件
- `pharmacy-ui/src/api/purchase-order.js` - 采购订单API
- `pharmacy-ui/src/api/sale-order.js` - 销售订单API
- `pharmacy-ui/src/api/purchase-item.js` - 采购明细API
- `pharmacy-ui/src/api/sale-item.js` - 销售明细API

### 新增/更新页面
- `pharmacy-ui/src/views/purchase-order/index.vue` - 采购订单主表页面
- `pharmacy-ui/src/views/sale-order/index.vue` - 销售订单主表页面
- `pharmacy-ui/src/views/purchase-item/index.vue` - 采购明细页面
- `pharmacy-ui/src/views/sale-item/index.vue` - 销售明细页面

### 后端接口更新
- `PurchaseItemController.list()` - 新增可选参数 purchaseId，支持按订单ID筛选明细
- `SaleItemController.list()` - 新增可选参数 orderId，支持按订单ID筛选明细

### 路由参数规则
- **采购订单跳转采购明细**：`/purchase-item?purchase_id={订单ID}`
  - 主表页面点击「查看明细」按钮时，通过 `router.push` 传递 purchase_id 参数
  - 明细页面 `onMounted` 时解析 `route.query.purchase_id`，并通过 `getPurchaseItemList({ purchaseId })` 获取对应订单的明细数据
  - 新增明细时，自动填充订单ID并禁用编辑

- **销售订单跳转销售明细**：`/sale-item?order_id={订单ID}`
  - 主表页面点击「查看明细」按钮时，通过 `router.push` 传递 order_id 参数
  - 明细页面 `onMounted` 时解析 `route.query.order_id`，并通过 `getSaleItemList({ orderId })` 获取对应订单的明细数据
  - 新增明细时，自动填充订单ID并禁用编辑

- **返回主表**：明细页面提供「返回订单列表」按钮，跳转回对应的主表页面

## Session9：采购销售单录入功能 ✅

### 功能说明
实现了完整的采购单和销售单录入功能，支持主表信息填写和明细逐条添加，实时计算总金额，后端使用事务保证数据一致性。

### 后端排序调整
- **采购订单、销售订单**：按创建时间升序排列
- **采购明细、销售明细**：按创建时间降序排列

### 新增页面
- `pharmacy-ui/src/views/purchase-order/form.vue` - 采购单新增/编辑页面
- `pharmacy-ui/src/views/sale-order/form.vue` - 销售单新增/编辑页面

### 新增API接口
- `purchase-order.js`：新增 `createPurchaseOrder` 接口，用于创建采购单（包含主表和明细）
- `sale-order.js`：新增 `createSaleOrder` 接口，用于创建销售单（包含主表和明细）

### 路由配置更新
- 新增 `/purchase-order/form` 路由 - 采购单表单页
- 新增 `/sale-order/form` 路由 - 销售单表单页

### 采购单录入功能
1. 主表信息：供应商、采购时间、支付方式、备注
2. 明细功能：
   - 选择药品自动填充名称和进价
   - 填写批号、数量、单价、有效期、存放药柜
   - 实时计算单条明细小计和总金额
   - 支持添加/删除明细
3. 保存：调用后端事务接口，同时插入主表和明细
4. 总金额由明细自动汇总，无需手动输入

### 销售单录入功能
1. 主表信息：客户（可选）、支付方式、订单类型、支付状态、备注
2. 明细功能：
   - 选择药品自动填充名称和售价
   - 填写批号、数量、单价
   - 实时计算单条明细小计和总金额
   - 支持添加/删除明细
3. 保存：调用后端事务接口，同时插入主表和明细
4. 总金额由明细自动汇总，无需手动输入

### 后端事务处理
- 采购单创建：`PurchaseOrderServiceImpl.createPurchaseOrder()` 使用 `@Transactional` 保证数据一致性
- 销售单创建：`SaleOrderServiceImpl.createSaleOrder()` 使用 `@Transactional` 保证数据一致性

---

## 已完成页面（8/12）
✅ 供应商 ✅ 顾客 ✅ 用户 ✅ 药品 ✅ 采购订单 ✅ 销售订单 ✅ 采购明细 ✅ 销售明细

---

## Session10：采购销售单录入功能修复 ✅

### 修复问题1：字段名不匹配
**问题**：前端字段与后端DTO字段不统一，导致数据保存失败
- 采购单表单：
  - `quantity` → `purchaseNum`
  - `unitPrice` → `purchasePrice`
  - `expiryDate` → `expireDate`
  - `location` → `cabinet`
- 销售单表单：
  - 增加 `medId` 药品ID必填字段

### 修复问题2：类型转换错误
**问题**：`toFixed()` 将数字转为字符串，导致后续计算和 `reduce()` 报错
- 明细的价格和数量保持为数字类型
- 只在UI显示时使用 `Number().toFixed(2)` 格式化
- `totalAmount/totalPrice` 计算属性中使用 `parseFloat()` 确保类型安全

### 修复问题3：销售单ID生成
**问题**：`SaleOrder.orderId` 使用 `IdType.INPUT` 需要手动设置ID
- 修改 `SaleOrder.orderId` 为 `IdType.AUTO` 自增生成

### 采购单明细更新
- 增加药品下拉选择框（用户要求改为文本框后又改回来）
- 批号改为非必填
- 增加生产日期（选填）字段
- 实时显示明细列表的价格（格式化两位小数）

### 销售单明细更新
- 增加药品下拉选择框
- 批号改为非必填
- 实时显示明细列表的价格（格式化两位小数）

---

## 已完成页面（10/12）
✅ 供应商 ✅ 顾客 ✅ 用户 ✅ 药品 ✅ 采购订单 ✅ 销售订单 ✅ 采购明细 ✅ 销售明细 ✅ 采购单录入 ✅ 销售单录入

---

## 待开发页面（2个）
- 库存、盘点、系统日志、控制台
