import { useState, useRef, useEffect } from "react"
import { MessageCircle, Loader2 } from "lucide-react"
import { CommentForm } from "./CommentForm"
import { CommentList } from "./CommentList"
import { useComments } from "@/hooks/useData"
import { toast } from "sonner"
import type { CreateCommentRequest } from "@/services/api"

interface CommentSectionProps {
  postId: number
}

export function CommentSection({ postId }: CommentSectionProps) {
  const {
    comments,
    loading,
    error,
    submitting,
    submitComment,
    commentCount,
  } = useComments(postId)

  const [replyTo, setReplyTo] = useState<{ id: number; nickname: string } | null>(
    null
  )
  const formRef = useRef<HTMLDivElement>(null)

  // 点击回复时滚动到表单
  const handleReply = (target: { id: number; nickname: string }) => {
    setReplyTo(target)
    // 滚动到表单
    setTimeout(() => {
      formRef.current?.scrollIntoView({ behavior: "smooth", block: "center" })
    }, 100)
  }

  const handleCancelReply = () => {
    setReplyTo(null)
  }

  const handleSubmit = async (data: CreateCommentRequest): Promise<boolean> => {
    const success = await submitComment(data)
    if (success) {
      toast.success("评论发表成功！", {
        description: replyTo ? `已回复 @${replyTo.nickname}` : "感谢你的评论",
      })
    } else {
      toast.error("评论发表失败", {
        description: "请稍后重试",
      })
    }
    return success
  }

  return (
    <section className="mt-16">
      {/* Section Header */}
      <div className="flex items-center gap-3 mb-8">
        <MessageCircle className="w-6 h-6 text-primary" />
        <h2 className="text-2xl font-bold text-foreground">
          评论
          {commentCount > 0 && (
            <span className="ml-2 text-base font-normal text-muted-foreground">
              ({commentCount})
            </span>
          )}
        </h2>
      </div>

      {/* Comment Form */}
      <div
        ref={formRef}
        className="p-6 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md mb-8"
      >
        <h3 className="text-lg font-semibold text-foreground mb-4">
          {replyTo ? "回复评论" : "发表评论"}
        </h3>
        <CommentForm
          onSubmit={handleSubmit}
          submitting={submitting}
          replyTo={replyTo || undefined}
          onCancelReply={handleCancelReply}
        />
      </div>

      {/* Comments List */}
      <div className="p-6 rounded-2xl bg-black/40 border border-white/10 backdrop-blur-md">
        {loading ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="w-6 h-6 animate-spin text-primary" />
            <span className="ml-2 text-muted-foreground">加载评论中...</span>
          </div>
        ) : error ? (
          <div className="text-center py-12">
            <p className="text-red-400 mb-2">加载评论失败</p>
            <p className="text-muted-foreground text-sm">{error}</p>
          </div>
        ) : (
          <CommentList comments={comments} onReply={handleReply} />
        )}
      </div>
    </section>
  )
}
