import { useState, useEffect } from "react"
import { Link, useLocation } from "react-router-dom"
import { motion, AnimatePresence } from "framer-motion"
import { Home, FileText, Folder, User, Menu, X } from "lucide-react"

const navItems = [
  { path: "/", label: "首页", icon: Home },
  { path: "/blog", label: "博客", icon: FileText },
  { path: "/projects", label: "项目", icon: Folder },
  { path: "/about", label: "关于", icon: User },
]

export function Navbar() {
  const location = useLocation()
  const [isScrolled, setIsScrolled] = useState(false)
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false)

  useEffect(() => {
    const handleScroll = () => {
      setIsScrolled(window.scrollY > 20)
    }
    window.addEventListener("scroll", handleScroll)
    return () => window.removeEventListener("scroll", handleScroll)
  }, [])

  // 路由变化时关闭移动菜单
  useEffect(() => {
    setIsMobileMenuOpen(false)
  }, [location.pathname])

  return (
    <>
      {/* Desktop Navbar */}
      <motion.header
        initial={{ y: -100 }}
        animate={{ y: 0 }}
        className={`fixed top-4 left-1/2 -translate-x-1/2 z-50 transition-all duration-300 hidden md:block ${
          isScrolled ? "top-2" : "top-4"
        }`}
      >
        <nav
          className={`flex items-center gap-1 px-2 py-2 rounded-2xl border backdrop-blur-xl transition-all duration-300 ${
            isScrolled
              ? "bg-black/60 border-white/20 shadow-lg shadow-black/20"
              : "bg-black/40 border-white/10"
          }`}
        >
          {navItems.map((item) => {
            const isActive = location.pathname === item.path
            const Icon = item.icon

            return (
              <Link
                key={item.path}
                to={item.path}
                className="relative"
              >
                <motion.div
                  className={`flex items-center gap-2 px-4 py-2 rounded-xl text-sm font-medium transition-colors ${
                    isActive
                      ? "text-primary"
                      : "text-muted-foreground hover:text-foreground"
                  }`}
                  whileHover={{ scale: 1.05 }}
                  whileTap={{ scale: 0.95 }}
                >
                  <Icon className="w-4 h-4" />
                  <span>{item.label}</span>

                  {/* Active Indicator */}
                  {isActive && (
                    <motion.div
                      layoutId="navbar-indicator"
                      className="absolute inset-0 bg-primary/10 rounded-xl border border-primary/30"
                      transition={{ type: "spring", bounce: 0.2, duration: 0.6 }}
                    />
                  )}
                </motion.div>
              </Link>
            )
          })}
        </nav>
      </motion.header>

      {/* Mobile Navbar */}
      <div className="md:hidden fixed top-4 right-4 z-50">
        <motion.button
          whileTap={{ scale: 0.9 }}
          onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
          className="p-3 rounded-xl bg-black/60 border border-white/20 backdrop-blur-xl"
        >
          {isMobileMenuOpen ? (
            <X className="w-5 h-5 text-foreground" />
          ) : (
            <Menu className="w-5 h-5 text-foreground" />
          )}
        </motion.button>
      </div>

      {/* Mobile Menu */}
      <AnimatePresence>
        {isMobileMenuOpen && (
          <motion.div
            initial={{ opacity: 0, y: -20 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -20 }}
            className="md:hidden fixed top-20 left-4 right-4 z-50 p-4 rounded-2xl bg-black/80 border border-white/20 backdrop-blur-xl"
          >
            <nav className="flex flex-col gap-2">
              {navItems.map((item) => {
                const isActive = location.pathname === item.path
                const Icon = item.icon

                return (
                  <Link
                    key={item.path}
                    to={item.path}
                    className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-colors ${
                      isActive
                        ? "text-primary bg-primary/10"
                        : "text-muted-foreground hover:text-foreground hover:bg-white/5"
                    }`}
                  >
                    <Icon className="w-5 h-5" />
                    <span>{item.label}</span>
                  </Link>
                )
              })}
            </nav>
          </motion.div>
        )}
      </AnimatePresence>
    </>
  )
}
