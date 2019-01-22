package com.bytedance.android.lesson.restapi.solution;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bytedance.android.lesson.restapi.solution.bean.Feed;
import com.bytedance.android.lesson.restapi.solution.bean.FeedResponse;
import com.bytedance.android.lesson.restapi.solution.bean.PostVideoResponse;
import com.bytedance.android.lesson.restapi.solution.newtork.IMiniDouyinService;
import com.bytedance.android.lesson.restapi.solution.newtork.RetrofitManager;
import com.bytedance.android.lesson.restapi.solution.utils.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Solution2C2Activity extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final String TAG = "Solution2C2Activity";
    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList<>();
    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    public Button mBtn;
    private Button mBtnRefresh;

    private static final String HOST = "http://10.108.10.39:8080/";
    private static final String IMAGE_NAME = "cover_image";
    private static final String VIDEO_NAME = "video";
    private static final String STUDENT_ID = "3220180750";
    private static final String USER_NAME = "wenjiahao";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_solution2_c2);
        initRecyclerView();
        initBtns();
    }

    private void initBtns() {
        mBtn = findViewById(R.id.btn);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                    chooseImage();
                } else if (getString(R.string.select_a_video).equals(s)) {
                    chooseVideo();
                } else if (getString(R.string.post_it).equals(s)) {
                    if (mSelectedVideo != null && mSelectedImage != null) {
                        postVideo();
                    } else {
                        throw new IllegalArgumentException("error data uri, mSelectedVideo = " + mSelectedVideo + ", mSelectedImage = " + mSelectedImage);
                    }
                } else if ((getString(R.string.success_try_refresh).equals(s))) {
                    mBtn.setText(R.string.select_an_image);
                }
            }
        });

        mBtnRefresh = findViewById(R.id.btn_refresh);
    }

    private void initRecyclerView() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
                ImageView imageView = new ImageView(viewGroup.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new Solution2C1Activity.MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                ImageView iv = (ImageView) viewHolder.itemView;

                // TODO-C2 (10) Uncomment these 2 lines, assign image url of Feed to this url variable
//                String url = mFeeds.get(i).;
//                Glide.with(iv.getContext()).load(url).into(iv);
                String url = mFeeds.get(i).getImageUrl();
                Glide.with(iv.getContext()).load(url).into(iv);
            }

            @Override
            public int getItemCount() {
                return mFeeds.size();
            }
        });
    }

    public void chooseImage() {
        // TODO-C2 (4) Start Activity to select an image
        //隐式的Intent，定义规则，交给系统去选择activity执行操作
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }


    public void chooseVideo() {
        // TODO-C2 (5) Start Activity to select a video
        //隐式的Intent，定义规则，交给系统去选择activity执行操作
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent, "Select Video"), PICK_VIDEO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(Solution2C2Activity.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo() {
        mBtn.setText("POSTING...");
        mBtn.setEnabled(false);

        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show
        Retrofit retrofit = RetrofitManager.get(HOST);

        MultipartBody.Part image = getMultipartFromUri(IMAGE_NAME, mSelectedImage);

        MultipartBody.Part video = getMultipartFromUri(VIDEO_NAME, mSelectedVideo);

        Call<PostVideoResponse> call = retrofit.create(IMiniDouyinService.class)
                .postVideo(STUDENT_ID, USER_NAME, image, video);

        call.enqueue(new Callback<PostVideoResponse>() {
            @Override
            public void onResponse(Call<PostVideoResponse> call, Response<PostVideoResponse> response) {
                if (response.code() == 200) {
                    PostVideoResponse postVideoResponse = response.body();
                    if (postVideoResponse.isSuccess()) {
                        Toast.makeText(getApplicationContext(), "upload success", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onResponse(): upload success");
                    }
                }
                resetBtn();
                //刷新feed流
                mBtnRefresh.performClick();

                Log.d(TAG, "onResponse: " + response.toString());
            }

            @Override
            public void onFailure(Call<PostVideoResponse> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "upload failure", Toast.LENGTH_LONG).show();
                Log.d(TAG, "onFailure: network request failure");
            }
        });


    }

    public void fetchFeed(View view) {
        mBtnRefresh.setText("requesting...");
        mBtnRefresh.setEnabled(false);

        // TODO-C2 (9) Send Request to fetch feed
        // if success, assign data to mFeeds and call mRv.getAdapter().notifyDataSetChanged()
        // don't forget to call resetRefreshBtn() after response received
        Retrofit retrofit = RetrofitManager.get(HOST);

        Call<FeedResponse> call = retrofit.create(IMiniDouyinService.class)
                .feed();

        call.enqueue(new Callback<FeedResponse>() {
            @Override
            public void onResponse(Call<FeedResponse> call, Response<FeedResponse> response) {
                if (response.code() == 200) {
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

    private void resetRefreshBtn() {
        mBtnRefresh.setText(R.string.refresh_feed);
        mBtnRefresh.setEnabled(true);
    }

    //重置选择图片、上传等一系列功能的按钮
    private void resetBtn() {
        mBtn.setText(R.string.success_try_refresh);
        mBtn.setEnabled(true);
    }
}
