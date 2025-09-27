import type { Route } from "./+types/home";
import { Layout } from "~/components/layout/Layout";
import type { Video } from "~/types/video";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "StreamFlow - Главная" },
    { name: "description", content: "Смотрите лучшие видео на StreamFlow" },
  ];
}

export default function Home() {
  const handleVideoClick = (video: Video) => {
    // TODO: Navigate to video player page
    console.log("Video clicked:", video);
  };

  return (
    <Layout onVideoClick={handleVideoClick} />
  );
}
