# Android Lesson RestAPI Solution

## 作业完成情况记录

### classwork #1
Create App RandomCatPics, an Android App that requests TheCatApi for 5 random
cat pictures and displays them。

1. Implement a Cat Bean according to the response json

```java
    @SerializedName("id")
    private String id;

    @SerializedName("url")
    private String url;

    //get and set method
```
2. Implement a Cat Request , url: https://api.thecatapi.com/v1/images/search?limit=5, use @Query annotation for  query parameter

```java
    @GET("v1/images/search")
    Call<List<Cat>> searchImages(@Query("limit") int limit);
```

3. Send request for getting 5 random cats , use {@link retrofit2.Call#enqueue} for asynchronous network request

```java
        //use RetrofitManager to manager Retrofit object for
        //avoiding creating Retrofit object frequently
        Retrofit retrofit = RetrofitManager.get(HOST);

        Call<List<Cat>> call = retrofit.create(ICatService.class).searchImages(5);
        call.enqueue(new Callback<List<Cat>>() {
            @Override
            public void onResponse(Call<List<Cat>> call, Response<List<Cat>> response) {
                if (response.isSuccessful()) {
                    loadPics(response.body());
                    Log.d(TAG, "onResponse: requesting data succeeded!");
                } else {
                    Log.d(TAG, "onResponse: requesting data failed!");
                }
                restoreBtn();
            }

            @Override
            public void onFailure(Call<List<Cat>> call, Throwable t) {
                restoreBtn();
                Log.d(TAG, "onFailure: requesting data failed!");
            }
        });
```

4. assign image url of Cat to url variable, and use Glide to set image url to ImageView

```java
        String url = mCats.get(i).getUrl();
        Glide.with(iv.getContext()).load(url).into(iv);
```

5. finish above jobs,run app successfully as below image:

<p align="center">
    <img src="./pic/chapter4classwork1.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>app demo</em>
    </p>
</p>

log info as follows:

```log
2019-01-22 21:52:58.519 8719-8719/com.bytedance.android.lesson.restapi.solution D/Solution2C1Activity: onResponse: requesting data succeeded!
2019-01-22 21:52:58.575 8719-8719/com.bytedance.android.lesson.restapi.solution D/Solution2C1Activity: bindImage:Cat{id='1g5', url='https://cdn2.thecatapi.com/images/1g5.jpg'}
2019-01-22 21:52:58.578 8719-8719/com.bytedance.android.lesson.restapi.solution D/Solution2C1Activity: bindImage:Cat{id='1s2', url='https://cdn2.thecatapi.com/images/1s2.jpg'}
2019-01-22 21:52:58.579 8719-8719/com.bytedance.android.lesson.restapi.solution D/Solution2C1Activity: bindImage:Cat{id='2fq', url='https://cdn2.thecatapi.com/images/2fq.gif'}
2019-01-22 21:52:58.580 8719-8719/com.bytedance.android.lesson.restapi.solution D/Solution2C1Activity: bindImage:Cat{id='33t', url='https://cdn2.thecatapi.com/images/33t.gif'}
2019-01-22 21:52:58.582 8719-8719/com.bytedance.android.lesson.restapi.solution D/Solution2C1Activity: bindImage:Cat{id='3lj', url='https://cdn2.thecatapi.com/images/3lj.jpg'}

```

### classwork #2
Create App MiniDouyinApi, an Android App that can upload pictures from your album,request the “feed” interface for picture urls and display them as App RandomCatPics does.

1.  Implement a Feed Bean here according to the response json

```java
public class Feed {
    @SerializedName("student_id")
    private String studentId;

    @SerializedName("user_name")
    private String username;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("video_url")
    private String videoUrl;

    // get and set method
}
```
2. Implement a FeedResponse Bean here according to the response json

```java
public class FeedResponse {

    @SerializedName("feeds")
    private List<Feed> feeds;

    @SerializedName("success")
    private boolean success;

    // get and set method
}
```
3.  Implement a PostVideoResponse Bean here according to the response json
and add a ReponseItem Bean
```java
public class PostVideoResponse {

    @SerializedName("success")
    private boolean success;

    @SerializedName("item")
    private ResponseItem item;
   
    // get and set method
}
```


```java
public class ResponseItem {

    @SerializedName("student_id")
    private String studentId;

    @SerializedName("user_name")
    private String username;

    @SerializedName("image_url")
    private String imageUrl;

    @SerializedName("video_url")
    private String videoUrl;
  
    // get and set method
}
```

4. Start Activity to select an image
```java
    public void chooseImage() {
        //隐式的Intent，定义规则，交给系统去选择activity执行操作
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }
```

5. Start Activity to select a video
```java
    public void chooseVideo() {
        // TODO-C2 (5) 
        //隐式的Intent，定义规则，交给系统去选择activity执行操作
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }
```

6. Implement MiniDouyin PostVideo Request here, url: (POST) http://10.108.10.39:8080/minidouyin/video; Implement  MiniDouyin Feed Request here, url: http://10.108.10.39:8080/minidouyin/feed
```java
public interface IMiniDouyinService {

    @Multipart
    @POST("minidouyin/video")
    Call<PostVideoResponse> postVideo(
            @Query("student_id") String studentId, @Query("user_name") String username,
            @Part MultipartBody.Part coverImage,
            @Part MultipartBody.Part video
    );

    @GET("minidouyin/feed")
    Call<FeedResponse> feed();
}
```

7. Send Request to post a video with its cover image
```java
 private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);

        // if success, make a text Toast and show
        Retrofit retrofit = RetrofitManager.get(HOST);

        MultipartBody.Part imagePart = getMultipartFromUri(IMAGE_NAME, mSelectedImage);

        MultipartBody.Part videoPart = getMultipartFromUri(VIDEO_NAME, mSelectedVideo);

        Call<PostVideoResponse> call = retrofit.create(IMiniDouyinService.class)
                .postVideo(STUDENT_ID, USER_NAME, imagePart, videoPart);

        call.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                if (response.isSuccessful()) {
                    PostVideoResponse postVideoResponse = response.body();
                    if (postVideoResponse != null && postVideoResponse.isSuccess()) {
                        // refresh feed
                        mBtnRefresh.performClick();
                        resetBtnAndToast(R.string.success_try_refresh);
                        Log.d(TAG, "onResponse(): upload successfully");
                    } else {
                        resetBtnAndToast(R.string.fail_try_refresh);
                        Log.d(TAG, "onResponse(): fail in uploading");
                    }
                } else {
                    resetBtnAndToast(R.string.fail_try_refresh);
                    Log.d(TAG, "onResponse: fail in uploading");
                }

            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                resetBtnAndToast(R.string.fail_try_refresh);
                Log.d(TAG, "onFailure: network request failure");
            }
        });
    }

     private void resetBtnAndToast(int id) {
        mBtn.setText(id);
        mBtn.setEnabled(true);
        if (id == R.string.success_try_refresh) {
            Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "upload failure", Toast.LENGTH_LONG).show();
        }
    }
```

8. Send Request to fetch feed

```java
  public void fetchFeed(View view) {
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setEnabled(false);

        // if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        // don't forget to call resetRefreshBtn() after response received
        Retrofit retrofit = RetrofitManager.get(HOST);

        Call<FeedResponse> call = retrofit.create(IMiniDouyinService.class)
                .feed();

        call.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                if (response.isSuccessful()) {
                    FeedResponse feedResponse = response.body();
                    if (feedResponse != null && feedResponse.isSuccess()) {
                        List<Feed> feeds = feedResponse.getFeeds();
                        loadFeeds(feeds);
                    }
                }
                resetRefreshBtn();
            }

            @Override
            public void onFailure(Call<FeedResponse> call, Throwable t) {
                resetRefreshBtn();
            }
        });
    }

    private void loadFeeds(List<Feed> feeds) {
        mFeeds = feeds;
        mRv.getAdapter().notifyDataSetChanged();
    }
```

9. assign image url of Feed to this url variable

```java
        String url = mFeeds.get(i).getImageUrl();
        Glide.with(iv.getContext()).load(url).into(iv);
```

10. finish above jobs and run app successfully as follows:

<p align="center">
    <img src="https://s23.aconvert.com/convert/p3r68-cdx67/yhr9b-lfboi.gif" alt="Sample"  width="300" height="500">
    <p align="center">
        <em>app demo</em>
    </p>
</p>


log info as follows:
```java
2019-01-22 22:47:07.206 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: onActivityResult() called with: requestCode = [1], resultCode = [-1], data = [Intent { dat=content://com.android.providers.media.documents/document/image:1448855 flg=0x1 }]
2019-01-22 22:47:07.207 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: selectedImage = content://com.android.providers.media.documents/document/image%3A1448855
2019-01-22 22:47:11.243 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: onActivityResult() called with: requestCode = [2], resultCode = [-1], data = [Intent { dat=content://com.android.providers.media.documents/document/video:1448889 flg=0x1 }]
2019-01-22 22:47:11.243 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: mSelectedVideo = content://com.android.providers.media.documents/document/video%3A1448889
2019-01-22 22:47:20.057 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: onResponse(): upload successfully
2019-01-22 22:47:36.684 12159-12159/com.bytedance.android.lesson.restapi.solution I/Timeline: Timeline: Activity_launch_request time:15803024 intent:Intent { cmp=com.bytedance.android.lesson.restapi.solution/.Solution2C2Activity }
2019-01-22 22:47:36.713 12159-12182/com.bytedance.android.lesson.restapi.solution I/ContentCatcher: Interceptor : Catcher list invalid for com.bytedance.android.lesson.restapi.solution@com.bytedance.android.lesson.restapi.solution.Solution2C2Activity@53644462
2019-01-22 22:47:41.602 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: onActivityResult() called with: requestCode = [1], resultCode = [-1], data = [Intent { dat=content://com.android.providers.media.documents/document/image:1448887 flg=0x1 }]
2019-01-22 22:47:41.602 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: selectedImage = content://com.android.providers.media.documents/document/image%3A1448887
2019-01-22 22:47:44.394 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: onActivityResult() called with: requestCode = [2], resultCode = [-1], data = [Intent { dat=content://com.android.providers.media.documents/document/video:1448878 flg=0x1 }]
2019-01-22 22:47:44.394 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: mSelectedVideo = content://com.android.providers.media.documents/document/video%3A1448878
2019-01-22 22:47:50.351 12159-12159/com.bytedance.android.lesson.restapi.solution D/Solution2C2Activity: onResponse(): upload successfully

```