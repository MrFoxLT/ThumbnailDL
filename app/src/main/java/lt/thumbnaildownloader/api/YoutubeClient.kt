package lt.thumbnaildownloader.api

import lt.thumbnaildownloader.models.VideoListRequest
import lt.thumbnaildownloader.models.VideoListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface YoutubeClient {

    //https://stackoverflow.com/questions/2068344/how-do-i-get-a-youtube-video-thumbnail-from-the-youtube-api

    @GET("search")
    fun searchForVideos(
        @Query("part") part: String,
        @Query("maxResults") maxResults: Int,
        @Query("q") searchWord: String,
        @Query("pageToken") pageToken: String,
        @Query("key") key: String
    ): Call<VideoListResponse>

}