import type { Video } from "~/types/video";
import { formatViewCount, formatTimeAgo } from "~/utils/formatters";

interface VideoCardProps {
  video: Video;
  onClick?: (video: Video) => void;
}

export function VideoCard({ video, onClick }: VideoCardProps) {

  const handleClick = () => {
    onClick?.(video);
  };

  return (
    <div 
      className="group cursor-pointer transition-transform duration-200 hover:scale-[1.02] w-full"
      onClick={handleClick}
    >
      {/* Thumbnail Container */}
      <div className="relative aspect-video w-full max-w-full overflow-hidden rounded-lg bg-surface flex items-center justify-center">
        {/* Video Icon */}
        <div className="text-6xl text-primary opacity-60">
          🎥
        </div>
        
        {/* Duration Badge */}
        <div className="absolute bottom-2 right-2 rounded bg-black/20 bg-opacity-80 px-2 py-1 text-xs font-medium text-white">
          {video.duration}
        </div>
        
        {/* Play Button Overlay */}
        <div className="absolute inset-0 flex items-center justify-center opacity-0 transition-opacity duration-200 group-hover:opacity-100">
          <div className="rounded-full bg-black/40 bg-opacity-60 p-3">
            <svg className="h-6 w-6 text-white" fill="currentColor" viewBox="0 0 24 24">
              <path d="M8 5v14l11-7z"/>
            </svg>
          </div>
        </div>
      </div>
      
      {/* Video Info */}
      <div className="mt-3 flex gap-3">
        {/* Channel Avatar */}
        <div className="flex-shrink-0">
          <div className="h-10 w-10 rounded-full bg-surface flex items-center justify-center text-lg">
            👤
          </div>
        </div>
        
        {/* Video Details */}
        <div className="flex-1 min-w-0">
          <h3 className="line-clamp-2 text-sm font-medium text-primary group-hover:text-white">
            {video.title}
          </h3>
          
          <div className="mt-1 flex items-center gap-1">
            <span className="text-xs text-secondary">{video.channel.name}</span>
            {video.channel.isVerified && (
              <svg className="h-3 w-3 text-blue-500" fill="currentColor" viewBox="0 0 24 24">
                <path d="M9 16.17L4.83 12l-1.42 1.41L9 19 21 7l-1.41-1.41z"/>
              </svg>
            )}
          </div>
          
          <div className="mt-1 text-xs text-muted">
            {formatViewCount(video.viewCount)} • {formatTimeAgo(video.createdAt)}
          </div>
        </div>
      </div>
    </div>
  );
}
