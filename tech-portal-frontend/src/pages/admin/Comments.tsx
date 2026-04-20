import { useState, useEffect } from "react"
import {
  MessagesSquare,
  Check,
  Trash2,
  AlertTriangle,
  Loader2,
  MoreHorizontal,
  Clock,
  User,
  FileText,
  ExternalLink,
} from "lucide-react"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
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
import { adminApi, type CommentDTO } from "@/services/api"
import { toast } from "sonner"

export default function AdminComments() {
  const [comments, setComments] = useState<CommentDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [spamId, setSpamId] = useState<number | null>(null)
  const [processing, setProcessing] = useState(false)

  const fetchComments = async () => {
    setLoading(true)
    try {
      const data = await adminApi.comments.getPending()
      setComments(data)
    } catch (error) {
      console.error("Failed to fetch comments:", error)
      toast.error("获取评论列表失败")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchComments()
  }, [])

  const handleApprove = async (id: number) => {
    setProcessing(true)
    try {
      await adminApi.comments.approve(id)
      setComments((prev) => prev.filter((c) => c.id !== id))
      toast.success("评论已通过审核")
    } catch (error) {
      console.error("Failed to approve comment:", error)
      toast.error("操作失败")
    } finally {
      setProcessing(false)
    }
  }

  const handleMarkAsSpam = async () => {
    if (!spamId) return

    setProcessing(true)
    try {
      await adminApi.comments.markAsSpam(spamId)
      setComments((prev) => prev.filter((c) => c.id !== spamId))
      toast.success("已标记为垃圾评论")
    } catch (error) {
      console.error("Failed to mark as spam:", error)
      toast.error("操作失败")
    } finally {
      setProcessing(false)
      setSpamId(null)
    }
  }

  const handleDelete = async () => {
    if (!deleteId) return

    setProcessing(true)
    try {
      await adminApi.comments.delete(deleteId)
      setComments((prev) => prev.filter((c) => c.id !== deleteId))
      toast.success("评论已删除")
    } catch (error) {
      console.error("Failed to delete comment:", error)
      toast.error("删除失败")
    } finally {
      setProcessing(false)
      setDeleteId(null)
    }
  }

  const formatDate = (dateStr: string) => {
    const date = new Date(dateStr)
    const now = new Date()
    const diffMs = now.getTime() - date.getTime()
    const diffHours = Math.floor(diffMs / 3600000)
    const diffDays = Math.floor(diffMs / 86400000)

    if (diffHours < 24) {
      return diffHours < 1 ? "刚刚" : `${diffHours} 小时前`
    }
    if (diffDays < 7) {
      return `${diffDays} 天前`
    }
    return date.toLocaleDateString("zh-CN", {
      month: "short",
      day: "numeric",
    })
  }

  const getAvatarColor = (nickname: string): string => {
    const colors = [
      "from-blue-500 to-cyan-500",
      "from-purple-500 to-pink-500",
      "from-green-500 to-emerald-500",
      "from-orange-500 to-amber-500",
      "from-red-500 to-rose-500",
    ]
    const index = nickname.charCodeAt(0) % colors.length
    return colors[index]
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
          <h1 className="text-2xl font-bold text-foreground">评论管理</h1>
          <p className="text-muted-foreground text-sm mt-1">
            {comments.length > 0
              ? `${comments.length} 条评论待审核`
              : "暂无待审核评论"}
          </p>
        </div>
      </div>

      {/* Comments List */}
      <div className="space-y-3">
        {comments.length === 0 ? (
          <div className="p-12 rounded-xl bg-black/40 border border-white/10 text-center">
            <MessagesSquare className="w-12 h-12 mx-auto text-muted-foreground/50 mb-4" />
            <p className="text-muted-foreground">暂无待审核的评论</p>
            <p className="text-muted-foreground/70 text-sm mt-1">
              所有评论都已处理完毕
            </p>
          </div>
        ) : (
          comments.map((comment) => (
            <div
              key={comment.id}
              className="p-4 rounded-xl bg-black/40 border border-white/10 backdrop-blur-md"
            >
              <div className="flex items-start gap-4">
                {/* Avatar */}
                <div
                  className={`w-10 h-10 rounded-full bg-gradient-to-br ${getAvatarColor(
                    comment.nickname
                  )} flex items-center justify-center text-white font-medium text-sm shrink-0`}
                >
                  {comment.nickname.charAt(0).toUpperCase()}
                </div>

                {/* Content */}
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <span className="font-medium text-foreground">
                      {comment.nickname}
                    </span>
                    {comment.replyToNickname && (
                      <span className="text-muted-foreground text-sm">
                        回复{" "}
                        <span className="text-primary">
                          @{comment.replyToNickname}
                        </span>
                      </span>
                    )}
                    <Badge
                      variant="outline"
                      className="border-yellow-500/50 text-yellow-500 text-[10px]"
                    >
                      待审核
                    </Badge>
                  </div>

                  <p className="text-foreground/90 mb-2 whitespace-pre-wrap">
                    {comment.content}
                  </p>

                  <div className="flex flex-wrap items-center gap-3 text-xs text-muted-foreground">
                    <span className="flex items-center gap-1">
                      <Clock className="w-3 h-3" />
                      {formatDate(comment.createdAt)}
                    </span>
                    {comment.postId && (
                      <a
                        href={`/blog/${comment.postId}`}
                        target="_blank"
                        rel="noopener noreferrer"
                        className="flex items-center gap-1 hover:text-primary transition-colors"
                      >
                        <FileText className="w-3 h-3" />
                        查看文章
                        <ExternalLink className="w-3 h-3" />
                      </a>
                    )}
                  </div>
                </div>

                {/* Actions */}
                <div className="flex items-center gap-2 shrink-0">
                  <Button
                    size="sm"
                    onClick={() => handleApprove(comment.id)}
                    disabled={processing}
                    className="bg-green-500/20 text-green-400 hover:bg-green-500/30 border-0"
                  >
                    <Check className="w-4 h-4 mr-1" />
                    通过
                  </Button>

                  <DropdownMenu>
                    <DropdownMenuTrigger asChild>
                      <Button
                        variant="ghost"
                        size="sm"
                        className="h-8 w-8 p-0"
                      >
                        <MoreHorizontal className="w-4 h-4" />
                      </Button>
                    </DropdownMenuTrigger>
                    <DropdownMenuContent
                      align="end"
                      className="bg-black/90 border-white/10"
                    >
                      <DropdownMenuItem
                        onClick={() => setSpamId(comment.id)}
                        className="text-yellow-400 focus:text-yellow-400"
                      >
                        <AlertTriangle className="w-4 h-4 mr-2" />
                        标为垃圾
                      </DropdownMenuItem>
                      <DropdownMenuSeparator className="bg-white/10" />
                      <DropdownMenuItem
                        onClick={() => setDeleteId(comment.id)}
                        className="text-red-400 focus:text-red-400"
                      >
                        <Trash2 className="w-4 h-4 mr-2" />
                        删除
                      </DropdownMenuItem>
                    </DropdownMenuContent>
                  </DropdownMenu>
                </div>
              </div>
            </div>
          ))
        )}
      </div>

      {/* Spam Confirmation Dialog */}
      <AlertDialog open={!!spamId} onOpenChange={() => setSpamId(null)}>
        <AlertDialogContent className="bg-black/90 border-white/10">
          <AlertDialogHeader>
            <AlertDialogTitle>标记为垃圾评论</AlertDialogTitle>
            <AlertDialogDescription>
              此操作将把该评论标记为垃圾评论并隐藏。确定要继续吗？
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel className="border-white/10">
              取消
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={handleMarkAsSpam}
              disabled={processing}
              className="bg-yellow-500 hover:bg-yellow-600 text-black"
            >
              {processing ? (
                <Loader2 className="w-4 h-4 animate-spin" />
              ) : (
                "确认"
              )}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={!!deleteId} onOpenChange={() => setDeleteId(null)}>
        <AlertDialogContent className="bg-black/90 border-white/10">
          <AlertDialogHeader>
            <AlertDialogTitle>确认删除</AlertDialogTitle>
            <AlertDialogDescription>
              此操作将永久删除该评论。确定要继续吗？
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel className="border-white/10">
              取消
            </AlertDialogCancel>
            <AlertDialogAction
              onClick={handleDelete}
              disabled={processing}
              className="bg-red-500 hover:bg-red-600"
            >
              {processing ? (
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
