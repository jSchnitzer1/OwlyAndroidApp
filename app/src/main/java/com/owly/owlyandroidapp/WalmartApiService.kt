import com.owly.owlyandroidapp.Search
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WalmartApiService {

        @GET("search?format=json&sort=price&order=desc")
        fun search(@Query("query") query: String, @Query("apiKey") apiKey: String = "5ky6bnsja5usjshxjazenpdx"): Observable<Search>

        @GET("search?format=json&sort=price&order=desc&apiKey=5ky6bnsja5usjshxjazenpdx")
        fun searchWith(@Query("query") query: String, @Query("cat_id") category: Int): Observable<Search>


  @GET("trends?format=json&sort=price&order=asc")
        fun trends(@Query("apiKey") apiKey: String = "5ky6bnsja5usjshxjazenpdx"): Observable<Search>

        /**
         * Companion object to create the WalmartApiService
         */
        companion object Factory {
            fun create(): WalmartApiService {
                val retrofit = Retrofit.Builder()
                        .baseUrl("http://api.walmartlabs.com/v1/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(MoshiConverterFactory.create())
                        .build()
                return retrofit.create(WalmartApiService::class.java)
            }
        }
    }