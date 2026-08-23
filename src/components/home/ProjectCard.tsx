import React from 'react';
import { useNavigate } from 'react-router-dom';

interface ProjectCardProps {
  id: string;
  tag: string;
  title: string;
  description: string;
  collectedAmount: number;
  targetAmount: number;
  imageUrl: string;
}

export const ProjectCard: React.FC<ProjectCardProps> = ({
  id,
  tag,
  title,
  description,
  collectedAmount,
  targetAmount,
  imageUrl,
}) => {
  const navigate = useNavigate();
  const percentage = Math.min(Math.round((collectedAmount / targetAmount) * 100), 100);

  return (
    <div className="bg-white rounded-3xl overflow-hidden border border-gray-100 shadow-sm flex flex-col">
      {/* Campaign Image */}
      <div className="h-44 w-full relative overflow-hidden bg-gray-100">
        <img
          src={imageUrl}
          alt={title}
          className="w-full h-full object-cover"
          loading="lazy"
        />
      </div>

      {/* Details Container */}
      <div className="p-4 flex flex-col gap-2">
        {/* Category Tag */}
        <span className="inline-block px-2.5 py-0.5 text-[10px] font-bold tracking-wider text-[#1A8C4E] bg-emerald-50 rounded-md uppercase self-start">
          {tag}
        </span>

        {/* Title & Desc */}
        <h3 className="font-bold text-sm text-[#0F2028] leading-snug">{title}</h3>
        <p className="text-xs text-gray-500 line-clamp-2 leading-relaxed">{description}</p>

        {/* Progress Metrics */}
        <div className="mt-2">
          <div className="flex justify-between items-baseline text-xs mb-1.5">
            <span className="font-bold text-[#1A8C4E]">
              RM{collectedAmount.toLocaleString()}{' '}
              <span className="text-[11px] font-normal text-gray-400">terkumpul</span>
            </span>
            <span className="font-bold text-[#1A8C4E]">{percentage}%</span>
          </div>

          {/* Progress Bar Track */}
          <div className="w-full h-2 bg-gray-100 rounded-full overflow-hidden">
            <div
              className="h-full bg-[#1A8C4E] rounded-full transition-all duration-500"
              style={{ width: `${percentage}%` }}
            />
          </div>

          <div className="flex justify-between items-center text-[11px] text-gray-400 mt-1.5">
            <span>Sasaran keseluruhan:</span>
            <span className="font-bold text-gray-700">RM{targetAmount.toLocaleString()}</span>
          </div>
        </div>

        {/* Action Buttons */}
        <div className="grid grid-cols-2 gap-2 mt-3 pt-2">
          <button
            onClick={() => navigate(`/projek/${id}`)}
            className="h-10 text-xs font-semibold text-gray-700 border border-gray-200 rounded-xl hover:bg-gray-50 transition"
          >
            Info Projek
          </button>
          <button
            onClick={() => navigate(`/projek/${id}`)}
            className="h-10 text-xs font-semibold text-white bg-[#1A8C4E] hover:bg-[#15703E] rounded-xl transition shadow-sm"
          >
            Waqaf Sekarang
          </button>
        </div>
      </div>
    </div>
  );
};