import { Navigate, useLocation } from "react-router-dom"
import { Loader2 } from "lucide-react"
import { useAuth } from "@/contexts/AuthContext"

interface ProtectedRouteProps {
  children: React.ReactNode
  requiredRole?: string
}

export function ProtectedRoute({ children, requiredRole }: ProtectedRouteProps) {
  const { user, isAuthenticated, isLoading } = useAuth()
  const location = useLocation()

  // 显示加载状态
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <Loader2 className="w-8 h-8 animate-spin text-primary mx-auto mb-4" />
          <p className="text-muted-foreground">验证身份中...</p>
        </div>
      </div>
    )
  }

  // 未登录，重定向到登录页
  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />
  }

  // 检查角色权限
  if (requiredRole && user?.role !== requiredRole) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold text-foreground mb-4">权限不足</h1>
          <p className="text-muted-foreground mb-4">
            您没有权限访问此页面
          </p>
          <Navigate to="/" replace />
        </div>
      </div>
    )
  }

  return <>{children}</>
}
