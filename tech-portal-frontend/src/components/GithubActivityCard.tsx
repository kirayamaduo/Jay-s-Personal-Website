import React from 'react';
import { ActivityCalendar, type Activity } from 'react-activity-calendar';
import { GitCommit } from 'lucide-react';
import { subDays } from 'date-fns';

// Helper to generate mock data for the last year
const generateMockData = (): Activity[] => {
  const data: Activity[] = [];
  const now = new Date();
  for (let i = 365; i >= 0; i--) {
    const date = subDays(now, i);
    // Simulating "realistic" commit patterns
    const isWeekend = date.getDay() === 0 || date.getDay() === 6;
    let count = 0;

    if (Math.random() > 0.4) { // 60% chance of committing
      const base = isWeekend ? 2 : 5; // Less commits on weekends
      count = Math.floor(Math.random() * base);
    }

    // Formatting date as YYYY-MM-DD
    const dateStr = date.toISOString().split('T')[0];
    data.push({
      date: dateStr,
      count: count,
      level: count > 3 ? 4 : count > 2 ? 3 : count > 0 ? 1 : 0
    });
  }
  return data;
};

export const GithubActivityCard: React.FC = () => {
  const data = generateMockData();

  return (
    <div className="h-full flex flex-col justify-between bg-[#18181b] border border-zinc-800 rounded-xl p-6 hover:border-zinc-600 hover:bg-zinc-800/50 transition-all duration-300 overflow-hidden">
      <div className="flex items-center justify-between mb-4">
        <h3 className="font-semibold text-zinc-100">GitHub Contributions</h3>
        <GitCommit className="text-emerald-500" size={18} />
      </div>

      <div className="flex flex-col gap-4 items-center">
        {/* We wrap it to ensure it fits the container */}
        <div className="w-full overflow-x-auto scrollbar-none flex justify-center">
          <ActivityCalendar
            data={data}
            theme={{
              light: ['#18181b', '#0e4429', '#006d32', '#26a641', '#39d353'],
              dark: ['#27272a', '#0e4429', '#006d32', '#26a641', '#39d353'], // GitHub Dark Green Scheme
            }}
            labels={{
              totalCount: '{{count}} contributions in the last year',
            }}
            colorScheme="dark"
            blockSize={10}
            blockMargin={4}
            fontSize={12}
          />
        </div>
        <div className="text-xs text-zinc-500 w-full text-right">
          Fake data until real API connected
        </div>
      </div>
    </div>
  );
};
