# 免费部署教程（全程 0 元）

> 所有平台均不需要信用卡，只需要一个 GitHub 账号。
> 预计总耗时：**20-30 分钟**

---

## 第一步：把代码推到 GitHub

> 如果已有 GitHub 仓库，直接跳到第二步。

1. 去 [github.com/new](https://github.com/new) 新建一个仓库（建议设为 Private）
2. 在本地项目根目录执行：

```bash
git init
git add .
git commit -m "initial commit"
git remote add origin https://github.com/你的用户名/你的仓库名.git
git push -u origin main
```

---

## 第二步：创建免费 MySQL 数据库（TiDB Cloud）

TiDB Cloud Serverless 是永久免费的 MySQL 兼容数据库。

1. 访问 [tidbcloud.com](https://tidbcloud.com) → 用 GitHub 注册/登录
2. 点击 **Create Cluster** → 选 **Serverless**（免费版）
3. 选离你最近的地区 → 点 **Create**（约 30 秒完成）
4. 点 **Connect** → 选 **General** → 选 **MySQL CLI** 方式
5. 记下连接信息：
   - **Host**: `xxxxxxxx.tidbcloud.com`
   - **Port**: `4000`
   - **Username**: `xxx.root`
   - **Password**: （点 Generate Password 生成）

6. 点击 **SQL Editor**，把 `dbdesign.sql` 的内容粘贴进去执行，创建所有表

**JDBC URL 格式（后面用）：**
```
jdbc:mysql://你的host:4000/tech_portal_pro?useSSL=true&serverTimezone=UTC&characterEncoding=utf-8
```

---

## 第三步：创建免费 Redis（Upstash）

1. 访问 [upstash.com](https://upstash.com) → 用 GitHub 注册/登录
2. 点击 **Create Database** → 选 **Redis**
3. Name 随意填，Region 选离你近的 → 点 **Create**
4. 创建后记下：
   - **Endpoint**: `xxxxxxx.upstash.io`
   - **Port**: `6379`（或 TLS 端口 `6380`）
   - **Password**: 页面上显示的密码

---

## 第四步：部署后端（Render）

1. 访问 [render.com](https://render.com) → 用 GitHub 注册/登录
2. 点 **New +** → **Web Service**
3. 选择你的 GitHub 仓库 → 点 **Connect**
4. 填写配置：
   - **Name**: `tech-portal-backend`（决定你的访问地址：`tech-portal-backend.onrender.com`）
   - **Root Directory**: 留空（使用根目录，render.yaml 会自动配置）
   - **Branch**: `main`
   - **Runtime**: Docker
   - **Dockerfile Path**: `./tech-portal-backend/Dockerfile`
5. 在 **Environment Variables** 中添加以下变量：

| Key | Value（填你自己的） |
|-----|-----|
| `DB_URL` | `jdbc:mysql://你的tidb_host:4000/tech_portal_pro?useSSL=true&serverTimezone=UTC&characterEncoding=utf-8` |
| `DB_USERNAME` | `你的tidb用户名` |
| `DB_PASSWORD` | `你的tidb密码` |
| `REDIS_HOST` | `你的upstash_endpoint` |
| `REDIS_PORT` | `6379` |
| `REDIS_PASSWORD` | `你的upstash密码` |
| `JWT_SECRET` | 运行 `openssl rand -base64 64` 生成一个随机字符串 |
| `APP_CORS_ALLOWED_ORIGINS` | 先填 `https://tech-portal-frontend.vercel.app`（下一步确认真实 URL 后再改） |

6. 点 **Create Web Service** → 等待构建（首次约 5-8 分钟）
7. 构建完成后，记下你的后端 URL：`https://tech-portal-backend.onrender.com`

---

## 第五步：部署前端（Vercel）

1. 访问 [vercel.com](https://vercel.com) → 用 GitHub 注册/登录
2. 点 **Add New** → **Project** → 选择你的 GitHub 仓库
3. 配置：
   - **Root Directory**: `tech-portal-frontend`（点 Edit 修改）
   - **Framework Preset**: Vite（会自动识别）
4. 在 **Environment Variables** 中添加：

| Key | Value |
|-----|-------|
| `VITE_API_BASE_URL` | `https://tech-portal-backend.onrender.com/api` |

5. 点 **Deploy** → 等待约 2 分钟
6. 部署成功后，记下你的前端 URL（如 `https://tech-portal-frontend.vercel.app`）

---

## 第六步：更新后端 CORS 配置

1. 回到 Render → 你的后端服务 → **Environment**
2. 找到 `APP_CORS_ALLOWED_ORIGINS`，把值改为你 Vercel 的真实 URL
   - 例如：`https://tech-portal-frontend.vercel.app`
3. 点 **Save Changes** → Render 会自动重新部署

---

## 完成！

现在访问你的 Vercel URL，网站就可以正常使用了 🎉

---

## 注意事项

- **冷启动**：Render 免费版 15 分钟无访问后会休眠，首次访问需等 20-30 秒后端才能响应。这是免费版的限制。
- **数据库初始化**：如果 SQL 文件有报错，可以在 TiDB Cloud 的 SQL Editor 中手动分段执行。
- **自定义域名**：Vercel 和 Render 都支持免费绑定自定义域名，在项目设置里添加即可。

---

## 快速排查

| 问题 | 可能原因 | 解决 |
|------|---------|------|
| 页面空白 / 404 | React Router 路由未配置 | 确认 `vercel.json` 已提交 |
| API 请求失败 | CORS 或 URL 配置错误 | 检查 `VITE_API_BASE_URL` 和 `APP_CORS_ALLOWED_ORIGINS` |
| 后端启动失败 | 数据库连接失败 | 检查 TiDB Cloud DB_URL 和 useSSL=true |
| Redis 连接失败 | 密码错误 | 在 Upstash 控制台确认密码 |
