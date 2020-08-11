package lt.thumbnaildownloader.data.models

import com.google.gson.annotations.SerializedName

// todo not used, think of something to do
data class PageInfo(
        @SerializedName("totalResults") val totalResults: Int,
        @SerializedName("resultsPerPage") val resultsPerPage: Int
)
