package com.said.xenogar.data.remote;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PdfApiService {

    @POST("/generate-pdf")
    Call<ResponseBody> generatePdfFromJson(
            @Body RequestBody jsonBody
    );
}