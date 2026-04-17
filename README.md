# AI-ToolBox 项目说明

## 项目简介

AI-ToolBox 是一个基于 Spring Boot 的 AI 工具网站，提供了文案生成、会员管理、积分系统等功能。该项目集成了豆包 AI API，支持用户通过积分抵扣的方式使用 AI 服务。

## 主要功能

### 1. AI 服务

- **文案生成**：根据用户提供的用途、关键词、风格等参数生成文案
- **Token 消耗统计**：实时统计 AI 模型的 Token 消耗情况
- **积分扣减**：根据实际 Token 消耗扣减用户积分
- **AI 模型管理**：支持添加新的 AI 模型

### 2. 用户管理

- **用户注册/登录**：支持用户注册和登录功能
- **个人信息查询**：查询用户基本信息、会员状态、积分余额等

### 3. 会员系统

- **会员购买**：支持用户购买会员套餐
- **会员状态管理**：自动管理会员有效期和状态

### 4. 积分系统

- **卡密兑换**：支持通过卡密兑换积分
- **积分扣减**：使用 AI 服务时自动扣减积分
- **积分变动记录**：记录积分的增减历史

## 技术栈

- **后端**：Spring Boot 3.2.5、MyBatis-Plus、MySQL
- **AI 集成**：豆包 AI SDK (volcengine-java-sdk-ark-runtime)
- **认证**：JWT
- **API 文档**：Swagger/OpenAPI
- **缓存**：Redis

## 项目结构

```
AI-ToolBox/
├── src/
│   ├── main/
│   │   ├── java/com/example/aitoolbox/
│   │   │   ├── controller/        # 控制器
│   │   │   ├── entity/           # 实体类
│   │   │   ├── mapper/           # MyBatis 映射器
│   │   │   ├── service/           # 服务层
│   │   │   ├── util/             # 工具类
│   │   │   ├── vo/               # 视图对象
│   │   │   └── AiToolBoxApplication.java  # 应用主类
│   │   └── resources/
│   │       ├── mapper/            # XML 映射文件
│   │       ├── sql/               # SQL 脚本
│   │       └── application.properties  # 配置文件
│   └── test/                     # 测试代码
├── pom.xml                       # Maven 依赖
└── README.md                     # 项目说明
```

## 快速开始

### 1. 环境要求

- JDK 17+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.8+

### 2. 安装步骤

1. **克隆项目**
   ```bash
   git clone <项目地址>
   cd AI-ToolBox
   ```
2. **配置数据库**
   - 创建数据库 `ToolBox`
   - 执行 `src/main/resources/sql/all.sql` 脚本初始化数据库结构和数据
3. **配置 application.properties**
   - 配置数据库连接信息
   - 配置 Redis 连接信息
   - 配置豆包 AI API Key
4. **构建项目**
   ```bash
   mvn clean install
   ```
5. **启动项目**
   ```bash
   mvn spring-boot:run
   ```

### 3. 访问地址

- API 文档：[http://localhost:8080/doc.html](http://localhost:8080/swagger-ui.html)
- 接口基础路径：<http://localhost:8080/api>

## API 接口

### AI 服务接口

| 接口路径                   | 方法   | 功能描述          |
| ---------------------- | ---- | ------------- |
| `/api/ai/copywriting`  | POST | 生成文案          |
| `/api/ai/history`      | GET  | 获取生成历史        |
| `/api/ai/history/{id}` | GET  | 获取对话详情        |
| `/api/ai/models`       | GET  | 获取可用的 AI 模型列表 |
| `/api/ai/models`       | POST | 新增 AI 模型      |

### 用户管理接口

| 接口路径         | 方法   | 功能描述     |
| ------------ | ---- | -------- |
| `/user/info` | GET  | 获取用户个人信息 |
| `/login`     | POST | 用户登录     |
| `/reg`       | POST | 用户注册     |

### 支付管理接口

| 接口路径                          | 方法   | 功能描述   |
| ----------------------------- | ---- | ------ |
| `/api/pay/redeem`             | POST | 卡密兑换积分 |
| `/api/pay/buy-membership`     | POST | 购买会员   |
| `/api/pay/card-key`           | POST | 新增卡密   |
| `/api/pay/card-key/remaining` | GET  | 查询剩余卡密 |

## 使用示例

### 1. 生成文案

**请求**：

```http
POST /api/ai/copywriting
Content-Type: application/x-www-form-urlencoded

userId=1&purpose=产品推广&keywords=智能手表,健康监测,时尚&style=时尚简约&wordCount=200&count=3&model=doubao-1-5-pro-32k-250115
```

**响应**：

```json
{
  "success": true,
  "message": "文案生成成功",
  "content": "1. 【时尚智能手表，健康生活新主张】\n   这款智能手表不仅外观时尚简约，更具备强大的健康监测功能...",
  "prompt": "你是专业文案助手，请根据以下信息生成文案：...",
  "tokens": 1500,
  "promptTokens": 500,
  "completionTokens": 1000,
  "cost": 1500,
  "balance": 8500
}
```

### 2. 获取用户信息

**请求**：

```http
GET /user/info?userId=1
```

**响应**：

```json
{
  "success": true,
  "message": "获取用户信息成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "email": "test@example.com",
    "membershipLevel": "普通用户",
    "membershipExpireTime": null,
    "isMember": false,
    "points": 10000
  }
}
```

## 配置说明

### 1. 数据库配置

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ToolBox?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=123456
```

### 2. Redis 配置

```properties
spring.redis.host=localhost
spring.redis.port=6379
spring.redis.password=
spring.redis.database=0
```

### 3. 豆包 AI 配置

```properties
doubao.ai.api.key=your_api_key
```

## 注意事项

1. **API Key 配置**：请在 `application.properties` 中配置有效的豆包 AI API Key
2. **积分管理**：使用 AI 服务会消耗积分，确保用户有足够的积分余额
3. **数据库初始化**：首次运行前请执行 SQL 脚本初始化数据库
4. **Redis 依赖**：项目依赖 Redis 进行缓存和会话管理，请确保 Redis 服务正常运行

## 常见问题

1. **API Key 无效**：请检查 `application.properties` 中的 API Key 是否正确
2. **积分不足**：用户积分不足时，AI 服务会返回积分不足的提示
3. **数据库连接失败**：请检查数据库配置和服务状态
4. **Redis 连接失败**：请检查 Redis 配置和服务状态

## 联系我们

如有任何问题或建议，欢迎联系我们。

***

**版本**：1.0.0
**更新时间**：2026-04-16
