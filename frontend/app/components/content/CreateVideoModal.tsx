import { useState, useEffect } from 'react';
import { videoApi } from '~/services/videoApi';
import { useAuth } from '~/contexts/AuthContext';

interface CreateVideoModalProps {
  isOpen: boolean;
  onClose: () => void;
}

export function CreateVideoModal({ isOpen, onClose }: CreateVideoModalProps) {
  const { isAuthenticated, token } = useAuth();
  const [currentStep, setCurrentStep] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [videoFile, setVideoFile] = useState<File | null>(null);
  const [thumbnailFile, setThumbnailFile] = useState<File | null>(null);
  const [createdVideoId, setCreatedVideoId] = useState<string | null>(null);
  
  const [formData, setFormData] = useState({
    title: '',
    description: '',
    visibility: 'PUBLIC' as 'PUBLIC' | 'PRIVATE' | 'SUBSCRIBERS',
    tagIds: [] as string[]
  });
  
  const [availableTags, setAvailableTags] = useState<{ id: string; name: string }[]>([]);
  const [isLoadingTags, setIsLoadingTags] = useState(false);

  // Загружаем теги при открытии модального окна
  useEffect(() => {
    if (isOpen) {
      loadTags();
    }
  }, [isOpen]);

  const loadTags = async () => {
    setIsLoadingTags(true);
    try {
      const tags = await videoApi.getTags();
      setAvailableTags(tags);
    } catch (error) {
      console.error('Ошибка загрузки тегов:', error);
      // Не показываем ошибку пользователю, так как теги не критичны
    } finally {
      setIsLoadingTags(false);
    }
  };

  const handleInputChange = (field: string, value: string | string[]) => {
    setFormData(prev => ({
      ...prev,
      [field]: value
    }));
  };

  const handleTagToggle = (tagId: string) => {
    setFormData(prev => ({
      ...prev,
      tagIds: prev.tagIds.includes(tagId)
        ? prev.tagIds.filter(id => id !== tagId)
        : [...prev.tagIds, tagId]
    }));
  };

  const handleNext = () => {
    if (currentStep < 3) {
      setCurrentStep(currentStep + 1);
    }
  };

  const handlePrevious = () => {
    if (currentStep > 1) {
      setCurrentStep(currentStep - 1);
    }
  };

  const handleSubmit = async () => {
    if (!isAuthenticated || !token) {
      setError('Необходимо войти в систему для создания видео');
      return;
    }

    if (!videoFile) {
      setError('Пожалуйста, выберите видео файл');
      return;
    }

    // Проверяем размер файла (10GB = 10 * 1024 * 1024 * 1024 bytes)
    const maxFileSize = 10 * 1024 * 1024 * 1024; // 10GB
    if (videoFile.size > maxFileSize) {
      setError('Размер файла превышает максимально допустимый лимит (10GB). Выберите файл меньшего размера.');
      return;
    }

    setIsLoading(true);
    setError(null);

    try {
      // Шаг 1: Создаем видео с метаданными
      const videoResponse = await videoApi.createVideo({
        title: formData.title,
        description: formData.description,
        visibility: formData.visibility,
        tagIds: formData.tagIds.length > 0 ? formData.tagIds : undefined
      }, token || undefined);

      setCreatedVideoId(videoResponse.id);

      // Шаг 2: Загружаем видео файл
      await videoApi.uploadVideo(videoResponse.id, videoFile, token || undefined);

      // Шаг 3: Загружаем превью (если выбрано)
      if (thumbnailFile) {
        await videoApi.uploadThumbnail(videoResponse.id, thumbnailFile, token || undefined);
      }

      // Успешно создано
      onClose();
      
      // Сбрасываем форму
      resetForm();
      
    } catch (error: any) {
      // Обработка различных типов ошибок
      if (error.message?.includes('413') || error.message?.includes('Payload Too Large')) {
        setError('Размер файла превышает максимально допустимый лимит (10GB). Выберите файл меньшего размера.');
      } else if (error.message?.includes('401') || error.message?.includes('Unauthorized')) {
        setError('Ошибка аутентификации. Пожалуйста, войдите в систему заново.');
      } else if (error.message?.includes('400') || error.message?.includes('Bad Request')) {
        setError('Некорректные данные. Проверьте заполненные поля.');
      } else {
        setError(error instanceof Error ? error.message : 'Произошла ошибка при создании видео');
      }
    } finally {
      setIsLoading(false);
    }
  };

  const resetForm = () => {
    setCurrentStep(1);
    setFormData({
      title: '',
      description: '',
      visibility: 'PUBLIC',
      tagIds: []
    });
    setVideoFile(null);
    setThumbnailFile(null);
    setCreatedVideoId(null);
    setError(null);
    setAvailableTags([]);
  };

  const handleClose = () => {
    if (!isLoading) {
      resetForm();
      onClose();
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4">
      {/* Backdrop */}
      <div 
        className="absolute inset-0 bg-black/50 backdrop-blur-sm"
        onClick={handleClose}
      />
      
      {/* Modal */}
      <div className="relative bg-surface border border-custom rounded-2xl shadow-2xl w-full max-w-2xl max-h-[90vh] flex flex-col">
        {/* Header */}
        <div className="flex items-center justify-between p-6 border-b border-custom">
          <h2 className="text-xl font-semibold text-primary">Создать видео</h2>
          <button
            onClick={handleClose}
            className="p-2 rounded-lg hover:bg-surface-hover transition-colors"
          >
            <svg className="h-5 w-5 text-muted" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M6 18L18 6M6 6l12 12" />
            </svg>
          </button>
        </div>

        {/* Progress Steps */}
        <div className="px-4 sm:px-6 py-4 border-b border-custom">
          <div className="flex items-start justify-center">
            {[1, 2, 3].map((step) => (
              <div key={step} className="flex items-center">
                <div className="flex flex-col items-center">
                  <div className={`w-6 h-6 sm:w-8 sm:h-8 rounded-full flex items-center justify-center text-xs sm:text-sm font-medium ${
                    step <= currentStep 
                      ? 'bg-primary text-white' 
                      : 'bg-surface-hover text-muted'
                  }`}>
                    {step}
                  </div>
                  <span className="text-xs text-muted mt-1 sm:mt-2 text-center max-w-16 sm:max-w-20 leading-tight">
                    {step === 1 && (
                      <>
                        <span className="hidden sm:inline">Основная информация</span>
                        <span className="sm:hidden">Основная</span>
                      </>
                    )}
                    {step === 2 && (
                      <>
                        <span className="hidden sm:inline">Загрузка видео</span>
                        <span className="sm:hidden">Видео</span>
                      </>
                    )}
                    {step === 3 && 'Превью'}
                  </span>
                </div>
                {step < 3 && (
                  <div className={`w-8 sm:w-16 h-0.5 mx-2 sm:mx-4 mt-3 sm:mt-4 ${
                    step < currentStep ? 'bg-primary' : 'bg-surface-hover'
                  }`} />
                )}
              </div>
            ))}
          </div>
        </div>

        {/* Content */}
        <div className="p-6 overflow-y-auto flex-1 min-h-0">
          {currentStep === 1 && (
            <div className="space-y-6">
              <h3 className="text-lg font-medium text-primary">Основная информация</h3>
              
              {/* Title */}
              <div>
                <label className="block text-sm font-medium text-primary mb-2">
                  Название видео *
                </label>
                <input
                  type="text"
                  value={formData.title}
                  onChange={(e) => handleInputChange('title', e.target.value)}
                  placeholder="Введите название видео"
                  className="w-full px-4 py-3 border border-custom rounded-lg bg-surface text-primary placeholder-muted focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                  maxLength={500}
                />
                <div className="text-xs text-muted mt-1">
                  {formData.title.length}/500 символов
                </div>
              </div>

              {/* Description */}
              <div>
                <label className="block text-sm font-medium text-primary mb-2">
                  Описание *
                </label>
                <textarea
                  value={formData.description}
                  onChange={(e) => handleInputChange('description', e.target.value)}
                  placeholder="Опишите ваше видео"
                  rows={4}
                  className="w-full px-4 py-3 border border-custom rounded-lg bg-surface text-primary placeholder-muted focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent resize-none"
                  maxLength={5000}
                />
                <div className="text-xs text-muted mt-1">
                  {formData.description.length}/5000 символов
                </div>
              </div>

              {/* Visibility */}
              <div>
                <label className="block text-sm font-medium text-primary mb-2">
                  Видимость
                </label>
                <select
                  value={formData.visibility}
                  onChange={(e) => handleInputChange('visibility', e.target.value)}
                  className="w-full px-4 py-3 border border-custom rounded-lg bg-surface text-primary focus:outline-none focus:ring-2 focus:ring-primary focus:border-transparent"
                >
                  <option value="PUBLIC">Публичное</option>
                  <option value="PRIVATE">Приватное</option>
                  <option value="SUBSCRIBERS">Только подписчики</option>
                </select>
              </div>

              {/* Tags */}
              <div>
                <label className="block text-sm font-medium text-primary mb-2">
                  Теги (необязательно)
                </label>
                {isLoadingTags ? (
                  <div className="text-muted text-sm">Загрузка тегов...</div>
                ) : (
                  <div className="flex flex-wrap gap-2">
                    {availableTags.map((tag) => (
                      <button
                        key={tag.id}
                        type="button"
                        onClick={() => handleTagToggle(tag.id)}
                        className={`px-3 py-1.5 rounded-full text-sm transition-colors ${
                          formData.tagIds.includes(tag.id)
                            ? 'bg-primary text-white'
                            : 'bg-surface-hover text-muted hover:bg-primary/10 hover:text-primary'
                        }`}
                      >
                        {tag.name}
                      </button>
                    ))}
                  </div>
                )}
                {formData.tagIds.length > 0 && (
                  <div className="text-xs text-muted mt-2">
                    Выбрано тегов: {formData.tagIds.length}
                  </div>
                )}
              </div>
            </div>
          )}

          {currentStep === 2 && (
            <div className="space-y-6">
              <h3 className="text-lg font-medium text-primary">Загрузка видео</h3>
              
              <div 
                className={`border-2 border-dashed rounded-lg p-8 text-center transition-colors ${
                  videoFile 
                    ? 'border-primary bg-primary/5' 
                    : 'border-custom hover:border-primary/50'
                }`}
                onDragOver={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                }}
                onDrop={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  const files = e.dataTransfer.files;
                  if (files.length > 0) {
                    setVideoFile(files[0]);
                  }
                }}
              >
                {videoFile ? (
                  <div>
                    <svg className="mx-auto h-12 w-12 text-primary mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                   <p className="text-primary font-medium mb-2">Файл выбран</p>
                   <p className="text-muted text-sm mb-2">{videoFile.name}</p>
                   <p className="text-muted text-xs mb-4">
                     Размер: {(videoFile.size / (1024 * 1024 * 1024)).toFixed(2)} GB
                   </p>
                    <button 
                      onClick={() => setVideoFile(null)}
                      className="px-4 py-2 border border-custom rounded-lg text-primary hover:bg-surface-hover transition-colors"
                    >
                      Удалить файл
                    </button>
                  </div>
                ) : (
                  <div>
                    <svg className="mx-auto h-12 w-12 text-muted mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M7 16a4 4 0 01-.88-7.903A5 5 0 1115.9 6L16 6a5 5 0 011 9.9M15 13l-3-3m0 0l-3 3m3-3v12" />
                    </svg>
                    <p className="text-primary font-medium mb-2">Перетащите видео файл сюда</p>
                    <p className="text-muted text-sm mb-4">или нажмите для выбора файла</p>
                    <input
                      type="file"
                      accept="video/*"
                      onChange={(e) => {
                        const file = e.target.files?.[0];
                        if (file) setVideoFile(file);
                      }}
                      className="hidden"
                      id="video-upload"
                    />
                    <label 
                      htmlFor="video-upload"
                      className="px-6 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors cursor-pointer inline-block"
                    >
                      Выбрать файл
                    </label>
                  </div>
                )}
                <p className="text-xs text-muted mt-4">
                  Поддерживаемые форматы: MP4, AVI, MOV, WMV (макс. 2GB)
                </p>
              </div>
            </div>
          )}

          {currentStep === 3 && (
            <div className="space-y-6">
              <h3 className="text-lg font-medium text-primary">Превью (необязательно)</h3>
              
              <div 
                className={`border-2 border-dashed rounded-lg p-8 text-center transition-colors ${
                  thumbnailFile 
                    ? 'border-primary bg-primary/5' 
                    : 'border-custom hover:border-primary/50'
                }`}
                onDragOver={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                }}
                onDrop={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  const files = e.dataTransfer.files;
                  if (files.length > 0) {
                    setThumbnailFile(files[0]);
                  }
                }}
              >
                {thumbnailFile ? (
                  <div>
                    <svg className="mx-auto h-12 w-12 text-primary mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12l2 2 4-4m6 2a9 9 0 11-18 0 9 9 0 0118 0z" />
                    </svg>
                    <p className="text-primary font-medium mb-2">Изображение выбрано</p>
                    <p className="text-muted text-sm mb-4">{thumbnailFile.name}</p>
                    <button 
                      onClick={() => setThumbnailFile(null)}
                      className="px-4 py-2 border border-custom rounded-lg text-primary hover:bg-surface-hover transition-colors"
                    >
                      Удалить файл
                    </button>
                  </div>
                ) : (
                  <div>
                    <svg className="mx-auto h-12 w-12 text-muted mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                    </svg>
                    <p className="text-primary font-medium mb-2">Загрузите превью</p>
                    <p className="text-muted text-sm mb-4">или пропустите этот шаг</p>
                    <input
                      type="file"
                      accept="image/*"
                      onChange={(e) => {
                        const file = e.target.files?.[0];
                        if (file) setThumbnailFile(file);
                      }}
                      className="hidden"
                      id="thumbnail-upload"
                    />
                    <label 
                      htmlFor="thumbnail-upload"
                      className="px-6 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors cursor-pointer inline-block"
                    >
                      Выбрать изображение
                    </label>
                  </div>
                )}
                <p className="text-xs text-muted mt-4">
                  Поддерживаемые форматы: JPG, PNG, GIF (макс. 10MB)
                </p>
              </div>
            </div>
          )}
        </div>

        {/* Error Message */}
        {error && (
          <div className="px-6 py-3 bg-red-500/10 border border-red-500/20 text-red-500 text-sm">
            {error}
          </div>
        )}


        {/* Footer */}
        <div className="flex items-center justify-between p-6 border-t border-custom flex-shrink-0">
          <button
            onClick={handlePrevious}
            disabled={currentStep === 1 || isLoading}
            className="px-6 py-2 border border-custom rounded-lg text-primary hover:bg-surface-hover transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
          >
            Назад
          </button>
          
          <div className="flex gap-3">
            <button
              onClick={handleClose}
              disabled={isLoading}
              className="px-6 py-2 border border-custom rounded-lg text-primary hover:bg-surface-hover transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
            >
              Отмена
            </button>
            
            {currentStep < 3 ? (
              <button
                onClick={handleNext}
                disabled={
                  isLoading || 
                  (currentStep === 1 && (!formData.title || !formData.description)) ||
                  (currentStep === 2 && !videoFile)
                }
                className="px-6 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
              >
                Далее
              </button>
            ) : (
              <button
                onClick={handleSubmit}
                disabled={isLoading || !videoFile}
                className="px-6 py-2 bg-primary text-white rounded-lg hover:bg-primary/90 transition-colors disabled:opacity-50 disabled:cursor-not-allowed flex items-center gap-2"
              >
                {isLoading ? (
                  <>
                    <svg className="animate-spin h-4 w-4" fill="none" viewBox="0 0 24 24">
                      <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                      <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                    </svg>
                    Создание...
                  </>
                ) : (
                  'Создать видео'
                )}
              </button>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
