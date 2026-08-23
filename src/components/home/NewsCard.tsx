import React from 'react';

interface NewsCardProps {
  date: string;
  title: string;
  snippet: string;
  imageUrl: string;
}

export const NewsCard: React.FC<NewsCardProps> = ({ date, title, snippet, imageUrl }) => {
  return (
    <div className="bg-white rounded-2xl overflow-hidden border border-gray-100 shadow-sm flex flex-col flex-shrink-0 w-56">
      <div className="h-28 w-full bg-gray-100 overflow-hidden">
        <img src={imageUrl} alt={title} className="w-full h-full object-cover" />
      </div>
      <div className="p-3 flex flex-col gap-1">
        <span className="text-[10px] text-gray-400 font-medium">{date}</span>
        <h4 className="font-bold text-xs text-[#0F2028] leading-tight line-clamp-2">{title}</h4>
        <p className="text-[11px] text-gray-500 leading-snug line-clamp-2 mt-0.5">{snippet}</p>
      </div>
    </div>
  );
};