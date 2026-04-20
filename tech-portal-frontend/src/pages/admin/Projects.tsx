import { useState, useEffect } from "react"
import { useNavigate } from "react-router-dom"
import {
  FolderKanban,
  Plus,
  Search,
  Trash2,
  Star,
  Loader2,
  MoreHorizontal,
  ExternalLink,
  Github,
  Globe,
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
import { adminApi, type ProjectListDTO } from "@/services/api"
import { toast } from "sonner"

export default function AdminProjects() {
  const navigate = useNavigate()
  const [projects, setProjects] = useState<ProjectListDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState("")
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [deleting, setDeleting] = useState(false)

  const fetchProjects = async () => {
    setLoading(true)
    try {
      const data = await adminApi.projects.getAll()
      setProjects(data)
    } catch (error) {
      console.error("Failed to fetch projects:", error)
      toast.error("获取项目列表失败")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchProjects()
  }, [])

  const handleDelete = async () => {
    if (!deleteId) return

    setDeleting(true)
    try {
      await adminApi.projects.delete(deleteId)
      setProjects((prev) => prev.filter((p) => p.id !== deleteId))
      toast.success("项目已删除")
    } catch (error) {
      console.error("Failed to delete project:", error)
      toast.error("删除失败")
    } finally {
      setDeleting(false)
      setDeleteId(null)
    }
  }

  const filteredProjects = projects.filter(
    (project) =>
      project.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      project.description.toLowerCase().includes(searchTerm.toLowerCase())
  )

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
          <h1 className="text-2xl font-bold text-foreground">项目管理</h1>
          <p className="text-muted-foreground text-sm mt-1">
            共 {projects.length} 个项目
          </p>
        </div>
        <Button 
          className="bg-primary hover:bg-primary/90"
          onClick={() => navigate("/admin/projects/new")}
        >
          <Plus className="w-4 h-4 mr-2" />
          添加项目
        </Button>
      </div>

      {/* Search */}
      <div className="relative mb-6">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-4 h-4 text-muted-foreground" />
        <Input
          placeholder="搜索项目..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          className="pl-10 bg-white/5 border-white/10"
        />
      </div>

      {/* Projects Grid */}
      {filteredProjects.length === 0 ? (
        <div className="p-12 rounded-xl bg-black/40 border border-white/10 text-center">
          <FolderKanban className="w-12 h-12 mx-auto text-muted-foreground/50 mb-4" />
          <p className="text-muted-foreground">
            {searchTerm ? "没有找到匹配的项目" : "暂无项目"}
          </p>
        </div>
      ) : (
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {filteredProjects.map((project) => (
            <div
              key={project.id}
              className="p-5 rounded-xl bg-black/40 border border-white/10 backdrop-blur-md hover:border-white/20 transition-colors"
            >
              <div className="flex items-start justify-between gap-3 mb-3">
                <div className="flex items-center gap-3">
                  {project.icon && (
                    <span className="text-2xl">{project.icon}</span>
                  )}
                  <div>
                    <div className="flex items-center gap-2">
                      <h3 className="font-semibold text-foreground">
                        {project.title}
                      </h3>
                      {project.isFeatured && (
                        <Star className="w-4 h-4 text-yellow-500 fill-yellow-500" />
                      )}
                    </div>
                    {project.starsCount !== null && project.starsCount > 0 && (
                      <p className="text-xs text-muted-foreground flex items-center gap-1">
                        <Star className="w-3 h-3" />
                        {project.starsCount} stars
                      </p>
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
                      onClick={() => navigate(`/admin/projects/${project.id}/edit`)}
                    >
                      <Pencil className="w-4 h-4 mr-2" />
                      编辑
                    </DropdownMenuItem>
                    <DropdownMenuItem
                      onClick={() =>
                        window.open(
                          `/projects/${project.slug || project.id}`,
                          "_blank"
                        )
                      }
                    >
                      <ExternalLink className="w-4 h-4 mr-2" />
                      查看详情
                    </DropdownMenuItem>
                    {project.githubUrl && (
                      <DropdownMenuItem
                        onClick={() =>
                          window.open(project.githubUrl!, "_blank")
                        }
                      >
                        <Github className="w-4 h-4 mr-2" />
                        GitHub
                      </DropdownMenuItem>
                    )}
                    {project.demoUrl && (
                      <DropdownMenuItem
                        onClick={() => window.open(project.demoUrl!, "_blank")}
                      >
                        <Globe className="w-4 h-4 mr-2" />
                        演示
                      </DropdownMenuItem>
                    )}
                    <DropdownMenuItem
                      onClick={() => setDeleteId(project.id)}
                      className="text-red-400 focus:text-red-400"
                    >
                      <Trash2 className="w-4 h-4 mr-2" />
                      删除
                    </DropdownMenuItem>
                  </DropdownMenuContent>
                </DropdownMenu>
              </div>

              <p className="text-sm text-muted-foreground line-clamp-2 mb-3">
                {project.description}
              </p>

              {project.tags.length > 0 && (
                <div className="flex flex-wrap gap-1">
                  {project.tags.slice(0, 4).map((tag) => (
                    <Badge
                      key={tag.id}
                      variant="secondary"
                      className="text-[10px] px-1.5 py-0"
                    >
                      {tag.name}
                    </Badge>
                  ))}
                  {project.tags.length > 4 && (
                    <span className="text-xs text-muted-foreground">
                      +{project.tags.length - 4}
                    </span>
                  )}
                </div>
              )}
            </div>
          ))}
        </div>
      )}

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={!!deleteId} onOpenChange={() => setDeleteId(null)}>
        <AlertDialogContent className="bg-black/90 border-white/10">
          <AlertDialogHeader>
            <AlertDialogTitle>确认删除</AlertDialogTitle>
            <AlertDialogDescription>
              此操作将删除该项目，删除后无法恢复。确定要继续吗？
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
