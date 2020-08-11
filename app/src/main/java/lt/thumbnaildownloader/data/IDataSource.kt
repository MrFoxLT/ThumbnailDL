package lt.thumbnaildownloader.data

interface IDataSource {
    suspend fun searchForVideos(request: VideoListRequest): Resource<VideoListResponse>
}