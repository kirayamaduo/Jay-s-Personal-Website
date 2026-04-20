# 部署指南

本项目使用 Docker Compose 一键部署，包含：前端（Nginx）、后端（Spring Boot）、MySQL 8 、Redis 7。

## 一、购买服务器

推荐选择：
- **腾讯云**轻量应用服务器 2核2G（约 ¥50-80/月）
- **阿里云** ECS 2核2G
- 海外访问可选 DigitalOcean / Vultr（$6/月起）

操作系统选 **Ubuntu 22.04 LTS**。

---

## 二、服务器环境准备

SSH 登录服务器后，运行以下命令安装 Docker：

```bash
# 安装 Docker
curl -fsSL https://get.docker.com | sh

# 将当前用户加入 docker 组（免 sudo）
sudo usermod -aG docker $USER
newgrp docker

# 验证安装
docker --version
docker compose version
```

---

## 三、上传项目代码

**方式 A：Git（推荐）**

先把代码推送到 GitHub/Gitee，然后在服务器上：

```bash
git clone https://github.com/你的用户名/你的仓库.git
cd 你的仓库目录
```

**方式 B：直接上传**

在本地用 `scp` 或 FileZilla 把整个项目目录传到服务器。

---

## 四、配置环境变量

```bash
# 复制配置模板
cp .env.example .env

# 编辑配置（必填项：DB_PASSWORD 和 JWT_SECRET）
nano .env
```

`.env` 关键配置：

| 变量 | 说明 | 是否必填 |
|------|------|---------|
| `DB_PASSWORD` | MySQL root 密码，自己设一个强密码 | 必填 |
| `JWT_SECRET` | JWT 签名密钥，建议用下面命令生成 | 必填 |
| `REDIS_PASSWORD` | Redis 密码，可留空 | 可选 |
| `CORS_ALLOWED_ORIGINS` | 你的域名，如 `https://example.com` | 有域名时填 |

生成强随机 JWT 密钥：
```bash
openssl rand -base64 64
```

---

## 五、启动服务

```bash
# 首次启动（会自动构建镜像，需要几分钟）
docker compose up -d --build

# 查看启动日志
docker compose logs -f

# 查看各服务状态
docker compose ps
```

启动成功后，访问 `http://你的服务器IP` 即可看到网站。

---

## 六、绑定域名（可选）

1. 在域名服务商处添加 **A 记录**，指向你的服务器 IP
2. 等 DNS 生效后（5-30 分钟），用域名访问即可

如需 HTTPS，推荐安装 **Nginx Proxy Manager** 或 **Caddy** 作为反向代理，自动申请 Let's Encrypt 证书。

---

## 七、常用运维命令

```bash
# 重启所有服务
docker compose restart

# 停止所有服务
docker compose down

# 更新代码后重新部署
git pull
docker compose up -d --build

# 查看后端日志
docker compose logs -f backend

# 查看前端日志
docker compose logs -f frontend

# 进入 MySQL 命令行
docker compose exec mysql mysql -u root -p

# 备份数据库
docker compose exec mysql mysqldump -u root -p${DB_PASSWORD} tech_portal_pro > backup.sql
```

---

## 八、项目目录结构

```
Personal Site/
├── docker-compose.yml          # Docker Compose 编排配置
├── .env.example                # 环境变量模板
├── .env                        # 实际环境变量（不提交到 Git！）
├── dbdesign.sql                # 数据库初始化脚本
├── tech-portal-frontend/
│   ├── Dockerfile              # 前端镜像（Nginx）
│   └── nginx.conf              # Nginx 配置（含 API 反向代理）
└── tech-portal-backend/
    └── Dockerfile              # 后端镜像（Spring Boot）
```

---

## 注意事项

- `.env` 文件包含密码，**不要提交到 Git**（确认 `.gitignore` 中有 `.env`）
- 首次启动时 MySQL 需要约 30 秒初始化，后端会自动等待
- 如果 `dbdesign.sql` 中有 `CREATE DATABASE` 语句，请删除，Docker 会自动创建数据库
