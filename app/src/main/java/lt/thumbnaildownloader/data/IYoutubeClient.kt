package lt.thumbnaildownloader.data

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface IYoutubeClient {

    //https://stackoverflow.com/questions/2068344/how-do-i-get-a-youtube-video-thumbnail-from-the-youtube-api

    @GET("search")
    suspend fun searchForVideos (
        @Query("part") part: String,
        @Query("maxResults") maxResults: Int,
        @Query("q") searchWord: String,
        @Query("pageToken") pageToken: String,
        @Query("key") key: String
    ): Response<VideoListResponse>

}