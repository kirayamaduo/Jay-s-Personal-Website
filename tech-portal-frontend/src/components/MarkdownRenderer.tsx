import ReactMarkdown from "react-markdown"
import remarkGfm from "remark-gfm"
import rehypeRaw from "rehype-raw"
import rehypeSlug from "rehype-slug"
import { Prism as SyntaxHighlighter } from "react-syntax-highlighter"
import { oneDark } from "react-syntax-highlighter/dist/esm/styles/prism"
import type { Components } from "react-markdown"
import type { ExtraProps } from "react-markdown"

interface MarkdownRendererProps {
  content: string
}

export function MarkdownRenderer({ content }: MarkdownRendererProps) {
  const components: Components = {
    // 自定义代码块渲染
    code(props: React.ComponentProps<"code"> & ExtraProps) {
      const { children, node, style: _style, ...rest } = props
      // 从 node.properties 获取 className
      const className = node?.properties?.className
      const classNameStr = Array.isArray(className) ? className.join(" ") : String(className || "")
      const match = /language-(\w+)/.exec(classNameStr)

      if (match) {
        return (
          <div className="relative group">
            <button
              onClick={() => navigator.clipboard.writeText(String(children))}
              className="absolute right-2 top-2 p-1.5 rounded-md bg-white/10 hover:bg-white/20 text-muted-foreground hover:text-white transition-opacity opacity-0 group-hover:opacity-100"
              title="Copy code"
            >
              <svg xmlns="http://www.w3.org/2000/svg" width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><rect x="9" y="9" width="13" height="13" rx="2" ry="2"></rect><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"></path></svg>
            </button>
            <SyntaxHighlighter
              style={oneDark}
              language={match[1]}
              PreTag="div"
              customStyle={{
                borderRadius: "0.75rem",
                backgroundColor: "rgba(24, 24, 27, 0.8)",
                padding: "1rem",
                margin: "1.5rem 0",
                overflowX: "auto",
              }}
              showLineNumbers
              wrapLines
            >
              {String(children).replace(/\n$/, "")}
            </SyntaxHighlighter>
          </div>
        )
      }

      return (
        <code
          className="px-1.5 py-0.5 rounded bg-zinc-800 text-primary font-mono text-sm"
          {...rest}
        >
          {children}
        </code>
      )
    },
    // 自定义标题样式
    h1(props: React.ComponentProps<"h1"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <h1 className="text-3xl font-bold text-foreground mt-12 mb-6 pb-3 border-b border-white/10" {...rest} />
      )
    },
    h2(props: React.ComponentProps<"h2"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <h2 className="text-2xl font-semibold text-foreground mt-10 mb-4" {...rest} />
      )
    },
    h3(props: React.ComponentProps<"h3"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <h3 className="text-xl font-semibold text-foreground mt-8 mb-3" {...rest} />
      )
    },
    // 自定义段落
    p(props: React.ComponentProps<"p"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <p className="text-muted-foreground leading-relaxed mb-6" {...rest} />
      )
    },
    // 自定义链接
    a(props: React.ComponentProps<"a"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <a
          target="_blank"
          rel="noopener noreferrer"
          className="text-primary hover:underline"
          {...rest}
        />
      )
    },
    // 自定义列表
    ul(props: React.ComponentProps<"ul"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <ul className="list-disc list-inside space-y-2 text-muted-foreground mb-6 ml-4" {...rest} />
      )
    },
    ol(props: React.ComponentProps<"ol"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <ol className="list-decimal list-inside space-y-2 text-muted-foreground mb-6 ml-4" {...rest} />
      )
    },
    li(props: React.ComponentProps<"li"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return <li className="text-muted-foreground" {...rest} />
    },
    // 自定义引用块
    blockquote(props: React.ComponentProps<"blockquote"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <blockquote
          className="border-l-4 border-primary/50 pl-4 py-2 my-6 bg-primary/5 rounded-r-lg italic text-muted-foreground"
          {...rest}
        />
      )
    },
    // 自定义图片
    img(props: React.ComponentProps<"img"> & ExtraProps) {
      const { node: _node, src, alt, ...rest } = props
      return (
        <figure className="my-8">
          <img
            src={src}
            alt={alt}
            className="rounded-xl w-full cursor-zoom-in hover:opacity-90 transition-opacity"
            {...rest}
          />
          {alt && (
            <figcaption className="text-center text-sm text-muted-foreground mt-3">
              {alt}
            </figcaption>
          )}
        </figure>
      )
    },
    // 自定义表格
    table(props: React.ComponentProps<"table"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <div className="overflow-x-auto my-6">
          <table className="w-full border-collapse" {...rest} />
        </div>
      )
    },
    th(props: React.ComponentProps<"th"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <th
          className="border border-white/10 bg-white/5 px-4 py-2 text-left font-semibold text-foreground"
          {...rest}
        />
      )
    },
    td(props: React.ComponentProps<"td"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return (
        <td className="border border-white/10 px-4 py-2 text-muted-foreground" {...rest} />
      )
    },
    // 自定义分割线
    hr(props: React.ComponentProps<"hr"> & ExtraProps) {
      const { node: _node, ...rest } = props
      return <hr className="my-8 border-white/10" {...rest} />
    },
  }

  return (
    <ReactMarkdown
      remarkPlugins={[remarkGfm]}
      rehypePlugins={[rehypeRaw, rehypeSlug]}
      components={components}
    >
      {content}
    </ReactMarkdown>
  )
}
