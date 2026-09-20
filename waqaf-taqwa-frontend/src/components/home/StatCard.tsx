import React from 'react';

interface StatCardProps {
  value: string | number;
  label: string;
  color?: string;
}

export const StatCard: React.FC<StatCardProps> = ({ value, label, color = 'text-[#1A8C4E]' }) => {
  return (
    <div className="flex-1 bg-white rounded-2xl p-4 border border-gray-100 shadow-[0_2px_8px_rgba(0,0,0,0.04)] flex flex-col justify-between">
      <span className={`text-2xl font-black ${color}`}>{value}</span>
      <span className="text-[11px] text-gray-500 font-medium leading-tight mt-2">{label}</span>
    </div>
  );
};