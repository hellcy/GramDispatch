package au.com.gramline.gramdispatch;

import au.com.gramline.gramdispatch.pojo.CollectedOrderList;
import au.com.gramline.gramdispatch.pojo.JobOrderList;
import au.com.gramline.gramdispatch.pojo.JobOrderListTest;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface APIInterface {

    // testing a job order
    @GET("/Scanner/api/JobOrder/get?HDR_SEQNO=1182")
    Call<JobOrderListTest> doGetJobOrderListTest();

    // get the specific job order details
    @GET("/Scanner/api/JobOrder/get?")
    Call<JobOrderList> doGetJobOrderList(@Query("HDR_SEQNO") String HDR_SEQNO);

    // create list of orders
    @POST("/Scanner/api/JobOrder")
    Call<CollectedOrderList> createCollectedOrderList(@Body CollectedOrderList collectedOrderList);

}
