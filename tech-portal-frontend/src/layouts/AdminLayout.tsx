import { Outlet, Link, useLocation, useNavigate } from "react-router-dom"
import {
  LayoutDashboard,
  FileText,
  FolderKanban,
  MessageSquare,
  MessagesSquare,
  LogOut,
  Menu,
  X,
  ChevronLeft,
} from "lucide-react"
import { useState } from "react"
import { Button } from "@/components/ui/button"
import { useAuth } from "@/contexts/AuthContext"
import { cn } from "@/lib/utils"

const navItems = [
  { path: "/admin", icon: LayoutDashboard, label: "仪表盘", exact: true },
  { path: "/admin/posts", icon: FileText, label: "文章管理" },
  { path: "/admin/projects", icon: FolderKanban, label: "项目管理" },
  { path: "/admin/messages", icon: MessageSquare, label: "消息管理" },
  { path: "/admin/comments", icon: MessagesSquare, label: "评论管理" },
]

export default function AdminLayout() {
  const location = useLocation()
  const navigate = useNavigate()
  const { user, logout } = useAuth()
  const [sidebarOpen, setSidebarOpen] = useState(false)

  const handleLogout = async () => {
    await logout()
    navigate("/login")
  }

  const isActive = (path: string, exact?: boolean) => {
    if (exact) {
      return location.pathname === path
    }
    return location.pathname.startsWith(path)
  }

  return (
    <div className="min-h-screen bg-background">
      {/* Mobile Header */}
      <div className="lg:hidden fixed top-0 left-0 right-0 h-16 bg-black/80 backdrop-blur-xl border-b border-white/10 z-50 flex items-center justify-between px-4">
        <button
          onClick={() => setSidebarOpen(true)}
          className="p-2 rounded-lg hover:bg-white/10"
        >
          <Menu className="w-5 h-5" />
        </button>
        <span className="font-semibold">管理后台</span>
        <div className="w-9" />
      </div>

      {/* Mobile Sidebar Overlay */}
      {sidebarOpen && (
        <div
          className="lg:hidden fixed inset-0 bg-black/50 z-50"
          onClick={() => setSidebarOpen(false)}
        />
      )}

      {/* Sidebar */}
      <aside
        className={cn(
          "fixed top-0 left-0 h-full w-64 bg-black/80 backdrop-blur-xl border-r border-white/10 z-50 transition-transform duration-300",
          "lg:translate-x-0",
          sidebarOpen ? "translate-x-0" : "-translate-x-full"
        )}
      >
        {/* Sidebar Header */}
        <div className="h-16 flex items-center justify-between px-4 border-b border-white/10">
          <Link to="/admin" className="flex items-center gap-2">
            <div className="w-8 h-8 rounded-lg bg-gradient-to-br from-primary to-purple-500 flex items-center justify-center">
              <LayoutDashboard className="w-4 h-4 text-white" />
            </div>
            <span className="font-semibold">Admin</span>
          </Link>
          <button
            onClick={() => setSidebarOpen(false)}
            className="lg:hidden p-2 rounded-lg hover:bg-white/10"
          >
            <X className="w-5 h-5" />
          </button>
        </div>

        {/* Navigation */}
        <nav className="p-4 space-y-1">
          {navItems.map((item) => (
            <Link
              key={item.path}
              to={item.path}
              onClick={() => setSidebarOpen(false)}
              className={cn(
                "flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors",
                isActive(item.path, item.exact)
                  ? "bg-primary/20 text-primary"
                  : "text-muted-foreground hover:bg-white/5 hover:text-foreground"
              )}
            >
              <item.icon className="w-5 h-5" />
              {item.label}
            </Link>
          ))}
        </nav>

        {/* User Section */}
        <div className="absolute bottom-0 left-0 right-0 p-4 border-t border-white/10">
          <div className="flex items-center gap-3 mb-3">
            <div className="w-10 h-10 rounded-full bg-gradient-to-br from-primary to-purple-500 flex items-center justify-center text-white font-medium">
              {user?.username?.charAt(0).toUpperCase() || "A"}
            </div>
            <div className="flex-1 min-w-0">
              <p className="font-medium text-sm truncate">{user?.username}</p>
              <p className="text-xs text-muted-foreground truncate">
                {user?.email || "管理员"}
              </p>
            </div>
          </div>
          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              className="flex-1 border-white/10"
              onClick={() => navigate("/")}
            >
              <ChevronLeft className="w-4 h-4 mr-1" />
              返回前台
            </Button>
            <Button
              variant="outline"
              size="sm"
              className="border-white/10 text-red-400 hover:text-red-300 hover:bg-red-500/10"
              onClick={handleLogout}
            >
              <LogOut className="w-4 h-4" />
            </Button>
          </div>
        </div>
      </aside>

      {/* Main Content */}
      <main className="lg:ml-64 pt-16 lg:pt-0 min-h-screen">
        <div className="p-4 md:p-6 lg:p-8">
          <Outlet />
        </div>
      </main>
    </div>
  )
}
