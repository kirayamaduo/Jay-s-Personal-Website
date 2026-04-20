import {
  createContext,
  useContext,
  useState,
  useEffect,
  useCallback,
  type ReactNode,
} from "react"
import {
  authApi,
  type UserInfo,
  type LoginRequest,
  type AuthResponse,
} from "@/services/api"

interface AuthContextType {
  user: UserInfo | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (data: LoginRequest) => Promise<void>
  logout: () => Promise<void>
  refreshAuth: () => Promise<boolean>
}

const AuthContext = createContext<AuthContextType | null>(null)

const TOKEN_KEY = "accessToken"
const REFRESH_TOKEN_KEY = "refreshToken"
const USER_KEY = "user"

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserInfo | null>(() => {
    // 从 localStorage 恢复用户信息
    const savedUser = localStorage.getItem(USER_KEY)
    return savedUser ? JSON.parse(savedUser) : null
  })
  const [isLoading, setIsLoading] = useState(true)

  // 保存认证信息到 localStorage
  const saveAuthData = useCallback((data: AuthResponse) => {
    localStorage.setItem(TOKEN_KEY, data.accessToken)
    localStorage.setItem(REFRESH_TOKEN_KEY, data.refreshToken)
    localStorage.setItem(USER_KEY, JSON.stringify(data.user))
    setUser(data.user)
  }, [])

  // 清除认证信息
  const clearAuthData = useCallback(() => {
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(REFRESH_TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
    setUser(null)
  }, [])

  // 刷新认证
  const refreshAuth = useCallback(async (): Promise<boolean> => {
    const refreshToken = localStorage.getItem(REFRESH_TOKEN_KEY)
    if (!refreshToken) {
      clearAuthData()
      return false
    }

    try {
      const response = await authApi.refresh({ refreshToken })
      saveAuthData(response)
      return true
    } catch (error) {
      console.error("Failed to refresh token:", error)
      clearAuthData()
      return false
    }
  }, [saveAuthData, clearAuthData])

  // 初始化时验证 token
  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem(TOKEN_KEY)
      if (!token) {
        setIsLoading(false)
        return
      }

      try {
        // 尝试获取当前用户信息来验证 token
        const userInfo = await authApi.getCurrentUser()
        setUser(userInfo)
        localStorage.setItem(USER_KEY, JSON.stringify(userInfo))
      } catch (error) {
        console.error("Token validation failed:", error)
        // Token 失效，尝试刷新
        const refreshed = await refreshAuth()
        if (!refreshed) {
          clearAuthData()
        }
      } finally {
        setIsLoading(false)
      }
    }

    initAuth()
  }, [refreshAuth, clearAuthData])

  // 登录
  const login = useCallback(
    async (data: LoginRequest) => {
      const response = await authApi.login(data)
      saveAuthData(response)
    },
    [saveAuthData]
  )

  // 登出
  const logout = useCallback(async () => {
    try {
      await authApi.logout()
    } catch (error) {
      console.error("Logout error:", error)
    } finally {
      clearAuthData()
    }
  }, [clearAuthData])

  const value: AuthContextType = {
    user,
    isAuthenticated: !!user,
    isLoading,
    login,
    logout,
    refreshAuth,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (!context) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}
