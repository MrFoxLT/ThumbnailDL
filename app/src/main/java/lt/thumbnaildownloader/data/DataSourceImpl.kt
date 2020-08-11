package lt.thumbnaildownloader.data

import android.util.Log

class DataSourceImpl: IDataSource {

    private var webClient: IYoutubeClient = ClientGenerator.createService(IYoutubeClient::class.java)

    override suspend fun searchForVideos(request: VideoListRequest): Resource<VideoListResponse> {

        val response = webClient.searchForVideos(request.part, request.maxResults,
            request.searchWord, request.pageToken, request.key)

        Log.d("test", request.toString())

        return try {
            if(response.isSuccessful) {
                Resource.Success(response.body()!!)
            }
            else {
                if(response.message().isNotBlank())
                    Resource.Failure(response.message())
                else
                    Resource.Failure(response.errorBody()!!.string())
            }
        }
        catch (e: Throwable) {
            Resource.Failure(e.message!!)
        }
    }

}