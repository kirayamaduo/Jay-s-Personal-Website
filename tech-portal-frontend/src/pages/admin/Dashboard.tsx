import { useState, useEffect } from "react"
import { Link } from "react-router-dom"
import {
  FileText,
  FolderKanban,
  MessageSquare,
  MessagesSquare,
  TrendingUp,
  ArrowRight,
  Loader2,
  RefreshCw,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { adminApi, type DashboardStats } from "@/services/api"

export default function AdminDashboard() {
  const [stats, setStats] = useState<DashboardStats | null>(null)
  const [loading, setLoading] = useState(true)
  const [refreshing, setRefreshing] = useState(false)

  const fetchStats = async (isRefresh = false) => {
    if (isRefresh) setRefreshing(true)
    else setLoading(true)

    try {
      const data = await adminApi.getStats()
      setStats(data)
    } catch (error) {
      console.error("Failed to fetch stats:", error)
    } finally {
      setLoading(false)
      setRefreshing(false)
    }
  }

  useEffect(() => {
    fetchStats()
  }, [])

  const statCards = [
    {
      label: "文章数",
      value: stats?.postCount ?? 0,
      icon: FileText,
      color: "from-blue-500 to-cyan-500",
      link: "/admin/posts",
    },
    {
      label: "项目数",
      value: stats?.projectCount ?? 0,
      icon: FolderKanban,
      color: "from-purple-500 to-pink-500",
      link: "/admin/projects",
    },
    {
      label: "未读消息",
      value: stats?.unreadMessageCount ?? 0,
      icon: MessageSquare,
      color: "from-green-500 to-emerald-500",
      link: "/admin/messages",
      highlight: (stats?.unreadMessageCount ?? 0) > 0,
    },
    {
      label: "待审评论",
      value: stats?.pendingCommentCount ?? 0,
      icon: MessagesSquare,
      color: "from-orange-500 to-amber-500",
      link: "/admin/comments",
      highlight: (stats?.pendingCommentCount ?? 0) > 0,
    },
  ]

  const quickLinks = [
    { label: "写新文章", path: "/admin/posts", icon: FileText },
    { label: "添加项目", path: "/admin/projects", icon: FolderKanban },
    { label: "查看消息", path: "/admin/messages", icon: MessageSquare },
    { label: "审核评论", path: "/admin/comments", icon: MessagesSquare },
  ]

  if (loading) {
    return (
      <div className="flex items-center justify-center py-20">
        <Loader2 className="w-8 h-8 animate-spin text-primary" />
      </div>
    )
  }

  return (
    <div>
      {/* Header */}
      <div className="flex items-center justify-between mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">仪表盘</h1>
          <p className="text-muted-foreground text-sm mt-1">
            欢迎回来，这是您的网站概览
          </p>
        </div>
        <Button
          variant="outline"
          size="sm"
          onClick={() => fetchStats(true)}
          disabled={refreshing}
          className="border-white/10"
        >
          <RefreshCw
            className={`w-4 h-4 mr-2 ${refreshing ? "animate-spin" : ""}`}
          />
          刷新
        </Button>
      </div>

      {/* Stats Grid */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
        {statCards.map((stat) => (
          <Link
            key={stat.label}
            to={stat.link}
            className="group p-6 rounded-xl bg-black/40 border border-white/10 backdrop-blur-md hover:border-primary/30 transition-all"
          >
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-4">
                <div
                  className={`p-3 rounded-lg bg-gradient-to-br ${stat.color}`}
                >
                  <stat.icon className="w-5 h-5 text-white" />
                </div>
                <div>
                  <p
                    className={`text-2xl font-bold ${
                      stat.highlight ? "text-primary" : "text-foreground"
                    }`}
                  >
                    {stat.value}
                  </p>
                  <p className="text-sm text-muted-foreground">{stat.label}</p>
                </div>
              </div>
              <ArrowRight className="w-5 h-5 text-muted-foreground group-hover:text-primary group-hover:translate-x-1 transition-all" />
            </div>
          </Link>
        ))}
      </div>

      {/* Quick Actions */}
      <div className="p-6 rounded-xl bg-black/40 border border-white/10 backdrop-blur-md">
        <h2 className="text-lg font-semibold text-foreground mb-4 flex items-center gap-2">
          <TrendingUp className="w-5 h-5 text-primary" />
          快捷操作
        </h2>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-3">
          {quickLinks.map((link) => (
            <Link
              key={link.path}
              to={link.path}
              className="flex items-center gap-3 p-4 rounded-lg bg-white/5 border border-white/10 hover:bg-white/10 hover:border-primary/30 transition-all group"
            >
              <link.icon className="w-5 h-5 text-muted-foreground group-hover:text-primary transition-colors" />
              <span className="text-sm font-medium text-foreground">
                {link.label}
              </span>
            </Link>
          ))}
        </div>
      </div>
    </div>
  )
}
