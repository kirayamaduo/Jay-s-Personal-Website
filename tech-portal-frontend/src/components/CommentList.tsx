import { useState } from "react"
import { MessageCircle, Reply, ExternalLink, Clock } from "lucide-react"
import { Button } from "@/components/ui/button"
import type { CommentDTO } from "@/services/api"

interface CommentListProps {
  comments: CommentDTO[]
  onReply: (comment: { id: number; nickname: string }) => void
}

interface CommentItemProps {
  comment: CommentDTO
  onReply: (comment: { id: number; nickname: string }) => void
  depth?: number
}

function formatDate(dateStr: string): string {
  const date = new Date(dateStr)
  const now = new Date()
  const diffMs = now.getTime() - date.getTime()
  const diffMins = Math.floor(diffMs / 60000)
  const diffHours = Math.floor(diffMs / 3600000)
  const diffDays = Math.floor(diffMs / 86400000)

  if (diffMins < 1) return "刚刚"
  if (diffMins < 60) return `${diffMins} 分钟前`
  if (diffHours < 24) return `${diffHours} 小时前`
  if (diffDays < 7) return `${diffDays} 天前`

  return date.toLocaleDateString("zh-CN", {
    year: "numeric",
    month: "short",
    day: "numeric",
  })
}

function getAvatarColor(nickname: string): string {
  const colors = [
    "from-blue-500 to-cyan-500",
    "from-purple-500 to-pink-500",
    "from-green-500 to-emerald-500",
    "from-orange-500 to-amber-500",
    "from-red-500 to-rose-500",
    "from-indigo-500 to-violet-500",
  ]
  const index = nickname.charCodeAt(0) % colors.length
  return colors[index]
}

function CommentItem({ comment, onReply, depth = 0 }: CommentItemProps) {
  const maxDepth = 3 // 最大嵌套层级

  return (
    <div className={depth > 0 ? "ml-8 md:ml-12" : ""}>
      <div className="flex gap-3 md:gap-4">
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
          <div className="flex flex-wrap items-center gap-2 mb-1">
            <span className="font-medium text-foreground">
              {comment.website ? (
                <a
                  href={comment.website}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="hover:text-primary transition-colors inline-flex items-center gap-1"
                >
                  {comment.nickname}
                  <ExternalLink className="w-3 h-3" />
                </a>
              ) : (
                comment.nickname
              )}
            </span>

            {comment.replyToNickname && (
              <span className="text-muted-foreground text-sm">
                回复{" "}
                <span className="text-primary">@{comment.replyToNickname}</span>
              </span>
            )}

            <span className="text-muted-foreground text-xs flex items-center gap-1">
              <Clock className="w-3 h-3" />
              {formatDate(comment.createdAt)}
            </span>
          </div>

          <p className="text-foreground/90 text-sm md:text-base leading-relaxed whitespace-pre-wrap break-words">
            {comment.content}
          </p>

          <Button
            variant="ghost"
            size="sm"
            onClick={() => onReply({ id: comment.id, nickname: comment.nickname })}
            className="mt-2 h-auto py-1 px-2 text-xs text-muted-foreground hover:text-primary"
          >
            <Reply className="w-3 h-3 mr-1" />
            回复
          </Button>
        </div>
      </div>

      {/* Replies */}
      {comment.replies && comment.replies.length > 0 && (
        <div className="mt-4 space-y-4 border-l-2 border-white/10 pl-2">
          {comment.replies.map((reply) => (
            <CommentItem
              key={reply.id}
              comment={reply}
              onReply={onReply}
              depth={Math.min(depth + 1, maxDepth)}
            />
          ))}
        </div>
      )}
    </div>
  )
}

export function CommentList({ comments, onReply }: CommentListProps) {
  if (comments.length === 0) {
    return (
      <div className="text-center py-12">
        <MessageCircle className="w-12 h-12 mx-auto text-muted-foreground/50 mb-4" />
        <p className="text-muted-foreground">还没有评论，来发表第一条吧！</p>
      </div>
    )
  }

  return (
    <div className="space-y-6">
      {comments.map((comment) => (
        <CommentItem key={comment.id} comment={comment} onReply={onReply} />
      ))}
    </div>
  )
}
