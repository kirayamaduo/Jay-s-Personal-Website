import React, { useState } from 'react';
import { Send, Sparkles, User, Bot } from 'lucide-react';

export const AIChatCard: React.FC = () => {
  const [inputValue, setInputValue] = useState('');

  return (
    <div className="h-full flex flex-col bg-[#18181b] border border-zinc-800 rounded-xl overflow-hidden hover:border-zinc-600 transition-all duration-300">
      {/* Minimal Header */}
      <div className="flex items-center gap-2 px-5 py-4 border-b border-zinc-800 bg-zinc-900/50">
        <Sparkles size={18} className="text-emerald-500" />
        <span className="font-semibold text-zinc-200">AI Assistant</span>
      </div>

      {/* Chat Area */}
      <div className="flex-1 p-5 space-y-5 overflow-y-auto">
        {/* User Message */}
        <div className="flex gap-4">
          <div className="h-8 w-8 shrink-0 rounded-full bg-zinc-800 flex items-center justify-center border border-zinc-700">
            <User size={14} className="text-zinc-400" />
          </div>
          <div className="flex-1">
            <div className="bg-zinc-800/50 text-zinc-300 text-sm px-4 py-3 rounded-2xl rounded-tl-none inline-block">
              What are Jay's strongest technical skills?
            </div>
          </div>
        </div>

        {/* AI Message */}
        <div className="flex gap-4">
          <div className="h-8 w-8 shrink-0 rounded-full bg-emerald-500/10 flex items-center justify-center border border-emerald-500/20">
            <Bot size={14} className="text-emerald-500" />
          </div>
          <div className="flex-1">
            <div className="text-zinc-300 text-sm leading-relaxed">
              <span className="block mb-2 text-xs text-emerald-500/80 font-mono">&gt; Analyzing repository data...</span>
              Based on my analysis, Jay specializes in <strong className="text-zinc-100">Java Backend Architecture</strong> (Spring Boot, Microservices) and <strong className="text-zinc-100">React Frontend Development</strong>.
            </div>
          </div>
        </div>
      </div>

      {/* Input Area */}
      <div className="p-4 bg-zinc-900/30 border-t border-zinc-800">
        <div className="relative flex items-center">
          <input
            type="text"
            value={inputValue}
            onChange={(e) => setInputValue(e.target.value)}
            placeholder="Ask AI anything about me..."
            className="w-full bg-[#09090b] border border-zinc-800 text-sm text-zinc-200 placeholder-zinc-600 rounded-lg px-4 py-3 pr-10 focus:outline-none focus:border-zinc-600 transition-colors"
          />
          <button className="absolute right-3 text-zinc-500 hover:text-emerald-500 transition-colors">
            <Send size={16} />
          </button>
        </div>
      </div>
    </div>
  );
};
