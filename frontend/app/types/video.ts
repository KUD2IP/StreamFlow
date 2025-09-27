export interface Video {
  id: string;
  title: string;
  description?: string;
  thumbnailUrl: string;
  videoUrl: string;
  duration: string;
  viewCount: number;
  likeCount: number;
  createdAt: string;
  isLive: boolean;
  channel: Channel;
}

export interface Channel {
  id: string;
  name: string;
  avatarUrl: string;
  isVerified?: boolean;
  subscriberCount?: number;
}

export interface VideoListResponse {
  videos: Video[];
  totalCount: number;
  currentPage: number;
  totalPages: number;
  hasNextPage: boolean;
}

export interface VideoListParams {
  page?: number;
  limit?: number;
  category?: string;
  search?: string;
}
