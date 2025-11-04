export interface Video {
  id: string;
  title: string;
  description: string;
  visibility: 'PUBLIC' | 'PRIVATE' | 'SUBSCRIBERS';
  filename?: string;
  videoUrl?: string;
  previewUrl?: string;
  status: 'UPLOADING' | 'PROCESSING' | 'READY' | 'FAILED';
  createdAt: string;
  updatedAt: string;
  user: Channel;
  category: {
    id: string;
    name: string;
  };
  tags?: Array<{
    id: string;
    name: string;
  }>;
  analytics?: {
    viewCount: number;
    likeCount: number;
  };
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

export interface CreateVideoRequest {
  title: string;
  description: string;
  visibility: 'PUBLIC' | 'PRIVATE' | 'SUBSCRIBERS';
  categoryId?: string;
  tagIds?: string[];
}

export interface CreateVideoResponse {
  id: string;
  title: string;
  description: string;
  visibility: 'PUBLIC' | 'PRIVATE' | 'SUBSCRIBERS';
  status: 'UPLOADING' | 'PROCESSING' | 'READY' | 'FAILED';
  createdAt: string;
  category: {
    id: string;
    name: string;
  };
  tags?: Array<{
    id: string;
    name: string;
  }>;
}

export interface VideoUploadResponse {
  videoId: string;
  status: 'UPLOADING' | 'PROCESSING' | 'READY' | 'FAILED';
  message: string;
}
