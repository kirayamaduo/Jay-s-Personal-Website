import { useState, useEffect } from "react"
import {
  MessageSquare,
  Mail,
  Trash2,
  Check,
  CheckCheck,
  Loader2,
  MoreHorizontal,
  Clock,
  User,
  Reply,
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
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { adminApi, type MessageDTO } from "@/services/api"
import { toast } from "sonner"

export default function AdminMessages() {
  const [messages, setMessages] = useState<MessageDTO[]>([])
  const [loading, setLoading] = useState(true)
  const [selectedMessage, setSelectedMessage] = useState<MessageDTO | null>(null)
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [deleting, setDeleting] = useState(false)

  const fetchMessages = async () => {
    setLoading(true)
    try {
      const data = await adminApi.messages.getAll()
      setMessages(data)
    } catch (error) {
      console.error("Failed to fetch messages:", error)
      toast.error("获取消息列表失败")
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => {
    fetchMessages()
  }, [])

  const handleMarkAsRead = async (id: number) => {
    try {
      const updated = await adminApi.messages.markAsRead(id)
      setMessages((prev) =>
        prev.map((m) => (m.id === id ? { ...m, isRead: true } : m))
      )
      if (selectedMessage?.id === id) {
        setSelectedMessage({ ...selectedMessage, isRead: true })
      }
      toast.success("已标记为已读")
    } catch (error) {
      console.error("Failed to mark as read:", error)
      toast.error("操作失败")
    }
  }

  const handleMarkAsReplied = async (id: number) => {
    try {
      await adminApi.messages.markAsReplied(id)
      setMessages((prev) =>
        prev.map((m) => (m.id === id ? { ...m, isReplied: true, isRead: true } : m))
      )
      if (selectedMessage?.id === id) {
        setSelectedMessage({ ...selectedMessage, isReplied: true, isRead: true })
      }
      toast.success("已标记为已回复")
    } catch (error) {
      console.error("Failed to mark as replied:", error)
      toast.error("操作失败")
    }
  }

  const handleMarkAllAsRead = async () => {
    try {
      await adminApi.messages.markAllAsRead()
      setMessages((prev) => prev.map((m) => ({ ...m, isRead: true })))
      toast.success("已全部标记为已读")
    } catch (error) {
      console.error("Failed to mark all as read:", error)
      toast.error("操作失败")
    }
  }

  const handleDelete = async () => {
    if (!deleteId) return

    setDeleting(true)
    try {
      await adminApi.messages.delete(deleteId)
      setMessages((prev) => prev.filter((m) => m.id !== deleteId))
      if (selectedMessage?.id === deleteId) {
        setSelectedMessage(null)
      }
      toast.success("消息已删除")
    } catch (error) {
      console.error("Failed to delete message:", error)
      toast.error("删除失败")
    } finally {
      setDeleting(false)
      setDeleteId(null)
    }
  }

  const openMessage = async (message: MessageDTO) => {
    setSelectedMessage(message)
    if (!message.isRead) {
      handleMarkAsRead(message.id)
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

  const unreadCount = messages.filter((m) => !m.isRead).length

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
          <h1 className="text-2xl font-bold text-foreground">消息管理</h1>
          <p className="text-muted-foreground text-sm mt-1">
            共 {messages.length} 条消息
            {unreadCount > 0 && (
              <span className="text-primary ml-2">({unreadCount} 未读)</span>
            )}
          </p>
        </div>
        {unreadCount > 0 && (
          <Button
            variant="outline"
            size="sm"
            onClick={handleMarkAllAsRead}
            className="border-white/10"
          >
            <CheckCheck className="w-4 h-4 mr-2" />
            全部标为已读
          </Button>
        )}
      </div>

      {/* Messages List */}
      <div className="space-y-2">
        {messages.length === 0 ? (
          <div className="p-12 rounded-xl bg-black/40 border border-white/10 text-center">
            <MessageSquare className="w-12 h-12 mx-auto text-muted-foreground/50 mb-4" />
            <p className="text-muted-foreground">暂无消息</p>
          </div>
        ) : (
          messages.map((message) => (
            <div
              key={message.id}
              onClick={() => openMessage(message)}
              className={`p-4 rounded-xl border backdrop-blur-md cursor-pointer transition-all ${
                message.isRead
                  ? "bg-black/40 border-white/10 hover:border-white/20"
                  : "bg-primary/5 border-primary/20 hover:border-primary/40"
              }`}
            >
              <div className="flex items-start justify-between gap-4">
                <div className="flex-1 min-w-0">
                  <div className="flex items-center gap-2 mb-1">
                    <User className="w-4 h-4 text-muted-foreground shrink-0" />
                    <span className="font-medium text-foreground truncate">
                      {message.name}
                    </span>
                    {!message.isRead && (
                      <Badge className="bg-primary/20 text-primary text-[10px] px-1.5">
                        新
                      </Badge>
                    )}
                    {message.isReplied && (
                      <Badge
                        variant="secondary"
                        className="text-[10px] px-1.5"
                      >
                        已回复
                      </Badge>
                    )}
                  </div>
                  <p className="text-sm text-muted-foreground mb-1">
                    {message.email}
                  </p>
                  {message.subject && (
                    <p className="font-medium text-foreground/90 text-sm mb-1">
                      {message.subject}
                    </p>
                  )}
                  <p className="text-sm text-muted-foreground line-clamp-2">
                    {message.content}
                  </p>
                  <div className="flex items-center gap-2 mt-2 text-xs text-muted-foreground">
                    <Clock className="w-3 h-3" />
                    {formatDate(message.createdAt)}
                  </div>
                </div>

                <DropdownMenu>
                  <DropdownMenuTrigger
                    asChild
                    onClick={(e) => e.stopPropagation()}
                  >
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
                    {!message.isRead && (
                      <DropdownMenuItem
                        onClick={(e) => {
                          e.stopPropagation()
                          handleMarkAsRead(message.id)
                        }}
                      >
                        <Check className="w-4 h-4 mr-2" />
                        标为已读
                      </DropdownMenuItem>
                    )}
                    {!message.isReplied && (
                      <DropdownMenuItem
                        onClick={(e) => {
                          e.stopPropagation()
                          handleMarkAsReplied(message.id)
                        }}
                      >
                        <Reply className="w-4 h-4 mr-2" />
                        标为已回复
                      </DropdownMenuItem>
                    )}
                    <DropdownMenuItem
                      onClick={(e) => {
                        e.stopPropagation()
                        window.open(`mailto:${message.email}`, "_blank")
                      }}
                    >
                      <Mail className="w-4 h-4 mr-2" />
                      发送邮件
                    </DropdownMenuItem>
                    <DropdownMenuSeparator className="bg-white/10" />
                    <DropdownMenuItem
                      onClick={(e) => {
                        e.stopPropagation()
                        setDeleteId(message.id)
                      }}
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

      {/* Message Detail Dialog */}
      <Dialog
        open={!!selectedMessage}
        onOpenChange={() => setSelectedMessage(null)}
      >
        <DialogContent className="sm:max-w-lg bg-black/90 border-white/10">
          <DialogHeader>
            <DialogTitle className="flex items-center gap-2">
              <MessageSquare className="w-5 h-5 text-primary" />
              消息详情
            </DialogTitle>
          </DialogHeader>
          {selectedMessage && (
            <div className="space-y-4">
              <div className="grid grid-cols-2 gap-4 text-sm">
                <div>
                  <p className="text-muted-foreground mb-1">发送者</p>
                  <p className="font-medium">{selectedMessage.name}</p>
                </div>
                <div>
                  <p className="text-muted-foreground mb-1">邮箱</p>
                  <a
                    href={`mailto:${selectedMessage.email}`}
                    className="font-medium text-primary hover:underline"
                  >
                    {selectedMessage.email}
                  </a>
                </div>
                <div>
                  <p className="text-muted-foreground mb-1">时间</p>
                  <p className="font-medium">
                    {new Date(selectedMessage.createdAt).toLocaleString("zh-CN")}
                  </p>
                </div>
                {selectedMessage.ipAddress && (
                  <div>
                    <p className="text-muted-foreground mb-1">IP 地址</p>
                    <p className="font-medium font-mono text-xs">
                      {selectedMessage.ipAddress}
                    </p>
                  </div>
                )}
              </div>

              {selectedMessage.subject && (
                <div>
                  <p className="text-muted-foreground mb-1 text-sm">主题</p>
                  <p className="font-medium">{selectedMessage.subject}</p>
                </div>
              )}

              <div>
                <p className="text-muted-foreground mb-1 text-sm">消息内容</p>
                <div className="p-4 rounded-lg bg-white/5 border border-white/10">
                  <p className="whitespace-pre-wrap">{selectedMessage.content}</p>
                </div>
              </div>

              <div className="flex gap-2 pt-2">
                <Button
                  className="flex-1"
                  onClick={() =>
                    window.open(`mailto:${selectedMessage.email}`, "_blank")
                  }
                >
                  <Mail className="w-4 h-4 mr-2" />
                  回复邮件
                </Button>
                {!selectedMessage.isReplied && (
                  <Button
                    variant="outline"
                    onClick={() => handleMarkAsReplied(selectedMessage.id)}
                    className="border-white/10"
                  >
                    <Check className="w-4 h-4 mr-2" />
                    标为已回复
                  </Button>
                )}
              </div>
            </div>
          )}
        </DialogContent>
      </Dialog>

      {/* Delete Confirmation Dialog */}
      <AlertDialog open={!!deleteId} onOpenChange={() => setDeleteId(null)}>
        <AlertDialogContent className="bg-black/90 border-white/10">
          <AlertDialogHeader>
            <AlertDialogTitle>确认删除</AlertDialogTitle>
            <AlertDialogDescription>
              此操作将永久删除该消息。确定要继续吗？
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
