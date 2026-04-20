import React from 'react';
import { ArrowUpRight, Sparkles } from 'lucide-react';

export const CoreProjectCard: React.FC = () => {
  return (
    <div className="h-full group relative flex flex-col justify-between bg-[#18181b] border border-zinc-800 rounded-xl p-6 overflow-hidden hover:border-zinc-600 hover:bg-zinc-800/50 transition-all duration-300">
      
      <div className="flex items-start justify-between mb-4">
        <div className="rounded-lg bg-zinc-900 border border-zinc-800 p-2 text-indigo-400 group-hover:text-indigo-300 group-hover:border-indigo-500/30 transition-colors">
          <Sparkles size={20} />
        </div>
        <button className="rounded-full p-2 text-zinc-500 hover:text-zinc-100 hover:bg-zinc-800 transition-colors">
          <ArrowUpRight size={20} />
        </button>
      </div>

      <div>
        <h3 className="text-xl font-bold text-zinc-100 mb-2">AI Enhanced Edu System</h3>
        <p className="text-sm text-zinc-400 line-clamp-2 leading-relaxed">
          Intelligent educational management platform leveraging LLMs for personalized learning paths and automated tasks.
        </p>
      </div>
    </div>
  );
};
