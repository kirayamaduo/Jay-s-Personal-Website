import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import {
  FileText,
  Plus,
  Search,
  Trash2,
  Eye,
  Star,
  Loader2,
  MoreHorizontal,
  ExternalLink,
  Pencil,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog"
import { adminApi, type PostListDTO } from "@/services/api"
import { toast } from "sonner"

export default function AdminPosts() {
  const navigate = useNavigate()
  const [posts, setPosts] = useState<PostListDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState("")
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [deleting, setDeleting] = useState(false)

  const fetchPosts = async () => {
    setLoading(true)
    try {
      const data = await adminApi.posts.getAll()
      setPosts(data)
    } catch (error) {
      console.error("Failed to fetch posts:", error)
      toast.error("获取文章列表失败")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchPosts()
  }, [])

  const handleDelete = async () => {
    if (!deleteId) return

    setDeleting(true)
    try {
      await adminApi.posts.delete(deleteId)
      setPosts((prev) => prev.filter((p) => p.id !== deleteId))
      toast.success("文章已删除")
    } catch (error) {
      console.error("Failed to delete post:", error)
      toast.error("删除失败")
    } finally {
      setDeleting(false)
      setDeleteId(null)
    }
  }

  const filteredPosts = posts.filter(
    (post) =>
      post.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      post.slug.toLowerCase().includes(searchTerm.toLowerCase())
  )

  const formatDate = (dateStr: string) => {
    return new Date(dateStr).toLocaleDateString("zh-CN", {
      year: "numeric",
      month: "short",
      day: "numeric",
    })
  }

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
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 mb-6">
        <div>
          <h1 className="text-2xl font-bold text-foreground">文章管理</h1>
          <p className="text-muted-foreground text-sm mt-1">
            共 {posts.length} 篇文章
          </p>
        </div>
        <Button 
          className="bg-primary hover:bg-primary/90"
          onClick={() => navigate("/admin/posts/new")}
        >
          <Plus className="w-4 h-4 mr-2" />
          写新文章
        </Button>
      </div>

      {/* Search */}
      <div className="relative mb-6">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
        <Input
          placeholder="搜索文章..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10 bg-white/5 border-white/10"
        />
      </div>

      {/* Posts List */}
      <div className="space-y-3">
        {filteredPosts.length === 0 ? (
          <div className="p-12 rounded-xl bg-black/40 border border-white/10 text-center">
            <FileText className="w-12 h-12 mx-auto text-muted-foreground/50 mb-4" />
            <p className="text-muted-foreground">
              {searchTerm ? "没有找到匹配的文章" : "暂无文章"}
            </p>
          </div>
        ) : (
          filteredPosts.map((post) => (
            <div
              key={post.id}
              className="p-4 rounded-xl bg-black/40 border border-white/10 backdrop-blur-md hover:border-white/20 transition-colors"
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <h3 className="font-semibold text-foreground truncate">
                      {post.title}
                    </h3>
                    {post.isFeatured && (
                      <Star className="w-4 h-4 text-yellow-500 fill-yellow-500 shrink-0" />
                    )}
                  </div>
                  <p className="text-sm text-muted-foreground line-clamp-1 mb-2">
                    {post.excerpt || "无摘要"}
                  </p>
                  <div className="flex flex-wrap items-center gap-2 text-xs text-muted-foreground">
                    <span>{formatDate(post.publishedAt)}</span>
                    <span>•</span>
                    <span className="flex items-center gap-1">
                      <Eye className="w-3 h-3" />
                      {post.viewCount}
                    </span>
                    {post.tags.length > 0 && (
                      <>
                        <span>•</span>
                        <div className="flex gap-1">
                          {post.tags.slice(0, 3).map((tag) => (
                            <Badge
                              key={tag.id}
                              variant="secondary"
                              className="text-[10px] px-1.5 py-0"
                            >
                              {tag.name}
                            </Badge>
                          ))}
                          {post.tags.length > 3 && (
                            <span className="text-muted-foreground">
                              +{post.tags.length - 3}
                            </span>
                          )}
                        </div>
                      </>
                    )}
                  </div>
                </div>

                <DropdownMenu>
                  <DropdownMenuTrigger asChild>
                    <Button
                      variant="ghost"
                      size="sm"
                      className="h-8 w-8 p-0 shrink-0"
                    >
                      <MoreHorizontal className="w-4 h-4" />
                    </Button>
                  </DropdownMenuTrigger>
                  <DropdownMenuContent
                    align="end"
                    className="bg-black/90 border-white/10"
                  >
                    <DropdownMenuItem
                      onClick={() => navigate(`/admin/posts/${post.id}/edit`)}
                    >
                      <Pencil className="w-4 h-4 mr-2" />
                      编辑
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      onClick={() =>
                        window.open(`/blog/${post.slug || post.id}`, "_blank")
                      }
                    >
                      <ExternalLink className="w-4 h-4 mr-2" />
                      查看文章
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      onClick={() => setDeleteId(post.id)}
                      className="text-red-400 focus:text-red-400"
                    >
                      <Trash2 className="w-4 h-4 mr-2" />
                      删除
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={!!deleteId} onOpenChange={() => setDeleteId(null)}>
        <AlertDialogContent className="bg-black/90 border-white/10">
          <AlertDialogHeader>
            <AlertDialogTitle>确认删除</AlertDialogTitle>
            <AlertDialogDescription>
              此操作将删除该文章，删除后无法恢复。确定要继续吗？
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel className="border-white/10">
              取消
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              disabled={deleting}
              className="bg-red-500 hover:bg-red-600"
            >
              {deleting ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                "删除"
              )}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}
