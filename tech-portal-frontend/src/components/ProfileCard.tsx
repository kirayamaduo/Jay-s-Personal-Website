import React from 'react';
import { MapPin, Mail } from 'lucide-react';

export const ProfileCard: React.FC = () => {
  return (
    <div className="h-full flex flex-col justify-between bg-[#18181b] border border-zinc-800 rounded-xl p-6 hover:border-zinc-600 hover:bg-zinc-800/50 transition-all duration-300">
      <div>
        <div className="flex items-start justify-between mb-6">
          <div className="h-24 w-24 rounded-full border-2 border-zinc-700 overflow-hidden bg-zinc-800">
             <img 
              src="https://api.dicebear.com/7.x/avataaars/svg?seed=Jay" 
              alt="Jay Wu" 
              className="h-full w-full object-cover"
            />
          </div>
          <div className="flex flex-col items-end gap-2">
            <span className="px-3 py-1 text-xs font-medium text-zinc-300 bg-zinc-800 rounded-full border border-zinc-700">
              Software Engineer
            </span>
            <span className="px-3 py-1 text-xs font-medium text-emerald-400 bg-emerald-400/10 rounded-full border border-emerald-400/20">
              Full Stack Dev
            </span>
          </div>
        </div>
        
        <h1 className="text-3xl font-bold text-zinc-100 tracking-tight mb-2">Jay Wu</h1>
        <p className="text-zinc-400 leading-relaxed mb-6">
          Passionate about building scalable web applications and exploring AI integration. 
          Turning complex problems into elegant solutions.
        </p>
      </div>

      <div className="flex flex-wrap gap-4 mt-auto pt-6 border-t border-zinc-800/50">
        <div className="flex items-center gap-2 text-sm text-zinc-500">
          <MapPin size={16} />
          <span>Shanghai, CN</span>
        </div>
        <div className="flex items-center gap-2 text-sm text-zinc-500 hover:text-zinc-300 transition-colors cursor-pointer">
          <Mail size={16} />
          <span>contact@jaywu.dev</span>
        </div>
      </div>
    </div>
  );
};
