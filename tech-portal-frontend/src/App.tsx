import { BrowserRouter, Routes, Route } from "react-router-dom"
import { Toaster } from "sonner"
import MainLayout from "@/layouts/MainLayout"
import ScrollToTop from "@/components/ScrollToTop"
import { AuthProvider } from "@/contexts/AuthContext"
import { ProtectedRoute } from "@/components/ProtectedRoute"

// Public Pages
import Home from "@/pages/Home"
import Blog from "@/pages/Blog"
import BlogPost from "@/pages/BlogPost"
import Projects from "@/pages/Projects"
import ProjectDetail from "@/pages/ProjectDetail"
import About from "@/pages/About"
import Login from "@/pages/Login"
import NotFound from "@/pages/NotFound"

// Admin Pages (lazy loaded)
import { lazy, Suspense } from "react"
import { Loader2 } from "lucide-react"

const AdminLayout = lazy(() => import("@/layouts/AdminLayout"))
const AdminDashboard = lazy(() => import("@/pages/admin/Dashboard"))
const AdminPosts = lazy(() => import("@/pages/admin/Posts"))
const AdminPostEditor = lazy(() => import("@/pages/admin/PostEditor"))
const AdminProjects = lazy(() => import("@/pages/admin/Projects"))
const AdminProjectEditor = lazy(() => import("@/pages/admin/ProjectEditor"))
const AdminMessages = lazy(() => import("@/pages/admin/Messages"))
const AdminComments = lazy(() => import("@/pages/admin/Comments"))

// Loading component for lazy loaded pages
function PageLoader() {
  return (
    <div className="min-h-screen flex items-center justify-center">
      <Loader2 className="w-8 h-8 animate-spin text-primary" />
    </div>
  )
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <ScrollToTop />
        <Toaster
          position="top-center"
          toastOptions={{
            style: {
              background: "rgba(0, 0, 0, 0.9)",
              border: "1px solid rgba(255, 255, 255, 0.1)",
              color: "#fff",
            },
          }}
        />
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<MainLayout />}>
            <Route index element={<Home />} />
            <Route path="blog" element={<Blog />} />
            <Route path="blog/:id" element={<BlogPost />} />
            <Route path="projects" element={<Projects />} />
            <Route path="projects/:id" element={<ProjectDetail />} />
            <Route path="about" element={<About />} />
          </Route>

          {/* Login Route */}
          <Route path="/login" element={<Login />} />

          {/* Admin Routes (Protected) */}
          <Route
            path="/admin"
            element={
              <ProtectedRoute requiredRole="ADMIN">
                <Suspense fallback={<PageLoader />}>
                  <AdminLayout />
                </Suspense>
              </ProtectedRoute>
            }
          >
            <Route index element={<Suspense fallback={<PageLoader />}><AdminDashboard /></Suspense>} />
            <Route path="posts" element={<Suspense fallback={<PageLoader />}><AdminPosts /></Suspense>} />
            <Route path="posts/new" element={<Suspense fallback={<PageLoader />}><AdminPostEditor /></Suspense>} />
            <Route path="posts/:id/edit" element={<Suspense fallback={<PageLoader />}><AdminPostEditor /></Suspense>} />
            <Route path="projects" element={<Suspense fallback={<PageLoader />}><AdminProjects /></Suspense>} />
            <Route path="projects/new" element={<Suspense fallback={<PageLoader />}><AdminProjectEditor /></Suspense>} />
            <Route path="projects/:id/edit" element={<Suspense fallback={<PageLoader />}><AdminProjectEditor /></Suspense>} />
            <Route path="messages" element={<Suspense fallback={<PageLoader />}><AdminMessages /></Suspense>} />
            <Route path="comments" element={<Suspense fallback={<PageLoader />}><AdminComments /></Suspense>} />
          </Route>

          {/* 404 */}
          <Route path="*" element={<NotFound />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  )
}
