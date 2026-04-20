import { useState } from "react"
import { useNavigate, useLocation, Link } from "react-router-dom"
import { motion } from "framer-motion"
import { Lock, User, Loader2, ArrowLeft, Eye, EyeOff } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { useAuth } from "@/contexts/AuthContext"
import { toast } from "sonner"
import { Helmet } from "react-helmet-async"

export default function Login() {
  const navigate = useNavigate()
  const location = useLocation()
  const { login, isLoading: authLoading } = useAuth()

  const [formData, setFormData] = useState({
    username: "",
    password: "",
  })
  const [errors, setErrors] = useState<{ username?: string; password?: string }>(
    {}
  )
  const [loading, setLoading] = useState(false)
  const [showPassword, setShowPassword] = useState(false)

  // 登录成功后跳转的目标页面
  const from = (location.state as { from?: { pathname: string } })?.from?.pathname || "/admin"

  const validateForm = (): boolean => {
    const newErrors: { username?: string; password?: string } = {}

    if (!formData.username.trim()) {
      newErrors.username = "请输入用户名"
    }

    if (!formData.password) {
      newErrors.password = "请输入密码"
    }

    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()

    if (!validateForm()) return

    setLoading(true)
    try {
      await login({
        username: formData.username.trim(),
        password: formData.password,
      })
      toast.success("登录成功！", {
        description: "欢迎回来",
      })
      navigate(from, { replace: true })
    } catch (error) {
      console.error("Login failed:", error)
      toast.error("登录失败", {
        description: "用户名或密码错误，请重试",
      })
    } finally {
      setLoading(false)
    }
  }

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target
    setFormData((prev) => ({ ...prev, [name]: value }))
    if (errors[name as keyof typeof errors]) {
      setErrors((prev) => ({ ...prev, [name]: undefined }))
    }
  }

  const isSubmitting = loading || authLoading

  return (
    <>
      <Helmet>
        <title>登录 | Tech Portal</title>
      </Helmet>

      <div className="min-h-screen flex items-center justify-center p-4 relative">
        {/* Background Effects */}
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute top-1/4 left-1/4 w-96 h-96 bg-primary/20 rounded-full blur-3xl" />
          <div className="absolute bottom-1/4 right-1/4 w-96 h-96 bg-purple-500/20 rounded-full blur-3xl" />
        </div>

        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.5 }}
          className="w-full max-w-md relative z-10"
        >
          {/* Back Link */}
          <Link
            to="/"
            className="inline-flex items-center gap-2 text-muted-foreground hover:text-foreground transition-colors mb-8"
          >
            <ArrowLeft className="w-4 h-4" />
            返回首页
          </Link>

          {/* Login Card */}
          <div className="p-8 rounded-2xl bg-black/60 border border-white/10 backdrop-blur-xl">
            {/* Header */}
            <div className="text-center mb-8">
              <div className="w-16 h-16 mx-auto mb-4 rounded-2xl bg-gradient-to-br from-primary to-purple-500 flex items-center justify-center">
                <Lock className="w-8 h-8 text-white" />
              </div>
              <h1 className="text-2xl font-bold text-foreground mb-2">
                管理员登录
              </h1>
              <p className="text-muted-foreground text-sm">
                请输入您的账号信息
              </p>
            </div>

            {/* Form */}
            <form onSubmit={handleSubmit} className="space-y-5">
              <div className="space-y-2">
                <Label htmlFor="username">用户名</Label>
                <div className="relative">
                  <User className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                  <Input
                    id="username"
                    name="username"
                    placeholder="请输入用户名"
                    value={formData.username}
                    onChange={handleInputChange}
                    disabled={isSubmitting}
                    aria-invalid={!!errors.username}
                    className="pl-10 bg-white/5 border-white/10"
                  />
                </div>
                {errors.username && (
                  <p className="text-red-500 text-xs">{errors.username}</p>
                )}
              </div>

              <div className="space-y-2">
                <Label htmlFor="password">密码</Label>
                <div className="relative">
                  <Lock className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
                  <Input
                    id="password"
                    name="password"
                    type={showPassword ? "text" : "password"}
                    placeholder="请输入密码"
                    value={formData.password}
                    onChange={handleInputChange}
                    disabled={isSubmitting}
                    aria-invalid={!!errors.password}
                    className="pl-10 pr-10 bg-white/5 border-white/10"
                  />
                  <button
                    type="button"
                    onClick={() => setShowPassword(!showPassword)}
                    className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground transition-colors"
                  >
                    {showPassword ? (
                      <EyeOff className="w-4 h-4" />
                    ) : (
                      <Eye className="w-4 h-4" />
                    )}
                  </button>
                </div>
                {errors.password && (
                  <p className="text-red-500 text-xs">{errors.password}</p>
                )}
              </div>

              <Button
                type="submit"
                disabled={isSubmitting}
                className="w-full bg-primary hover:bg-primary/90 h-11"
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className="w-4 h-4 mr-2 animate-spin" />
                    登录中...
                  </>
                ) : (
                  "登录"
                )}
              </Button>
            </form>

            {/* Footer */}
            <p className="mt-6 text-center text-xs text-muted-foreground">
              仅限管理员访问
            </p>
          </div>
        </motion.div>
      </div>
    </>
  )
}
