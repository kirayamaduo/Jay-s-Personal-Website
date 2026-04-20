import React from 'react';
import { Code2, Database, Layout, Server, Cpu } from 'lucide-react';

export const TechStackCard: React.FC = () => {
  const techs = [
    { name: 'Java', icon: <Server size={18} className="text-red-400" /> },
    { name: 'Spring', icon: <Cpu size={18} className="text-emerald-400" /> },
    { name: 'React', icon: <Layout size={18} className="text-blue-400" /> },
    { name: 'MySQL', icon: <Database size={18} className="text-blue-300" /> },
    { name: 'Redis', icon: <Database size={18} className="text-red-500" /> },
  ];

  return (
    <div className="h-full flex flex-col bg-[#18181b] border border-zinc-800 rounded-xl p-6 hover:border-zinc-600 hover:bg-zinc-800/50 transition-all duration-300">
      <div className="flex items-center gap-2 mb-6">
        <Code2 className="text-zinc-400" size={20} />
        <h3 className="font-semibold text-zinc-100">Tech Stack</h3>
      </div>
      
      <div className="flex flex-wrap gap-2">
        {techs.map((tech) => (
          <div 
            key={tech.name} 
            className="flex items-center gap-2 rounded-lg bg-zinc-900 border border-zinc-800 px-3 py-2 text-sm text-zinc-300 transition-colors hover:border-zinc-600"
          >
            {tech.icon}
            <span>{tech.name}</span>
          </div>
        ))}
      </div>
    </div>
  );
};
