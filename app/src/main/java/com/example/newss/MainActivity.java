package com.example.newss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.newss.api.NewsClient;
import com.example.newss.api.NewsService;
import com.example.newss.models.Articles;
import com.example.newss.models.News;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    public static final String API_KEY = "d979123e488e4f059c493588fd175f8d";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private List<Articles> articles = new ArrayList<>();
    private Adapter adapter;
    private String TAG = MainActivity.class.getSimpleName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView topHeadLine;
    private RelativeLayout relativeErrorLayout;
    private ImageView errorImage;
    private TextView errorTitle, errorMessage;
    private Button btnRetry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        recyclerView = findViewById(R.id.recyclerView);
        topHeadLine = findViewById(R.id.topHeadline);
        layoutManager = new LinearLayoutManager(MainActivity.this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        onLoadingSwipeRefresh("");


        relativeErrorLayout = findViewById(R.id.errorLayout);
        errorImage = findViewById(R.id.errorImage);
        errorTitle = findViewById(R.id.errorTitle);
        errorMessage = findViewById(R.id.errorMessage);
        btnRetry = findViewById(R.id.retry_button);




    }
    public void loadJson(final String keyword){

        relativeErrorLayout.setVisibility(View.GONE);
        topHeadLine.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(true);
        NewsClient apiInterface = NewsService.getApiClient().create(NewsClient.class);
        String country = Utils.getCountry();
        String language = Utils.getLanguage();
        Call<News> call;

        if(keyword.length() > 0){
            call = apiInterface.getNewsSearch(keyword, language, "publishedAt", API_KEY);
        }else{
            call = apiInterface.getNews(country, API_KEY);
        }

        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if(response.isSuccessful() && response.body().getArticle() != null){
                    if(!articles.isEmpty()){
                        articles.clear();
                    }
                    articles = response.body().getArticle();
                    adapter = new Adapter(articles, MainActivity.this);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    initListener();


                    topHeadLine.setVisibility(View.VISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }else{
                    String errorCode;
                    switch (response.code()){
                        case 404: errorCode = "404 not found";
                                  break;
                        case 500: errorCode = "500 Server error";
                            break;
                        default: errorCode = "Something unexpected happened";
                            break;
                    }
                    showErrorMessage(R.drawable.no_result, "No result", "Please try again\n"
                            + errorCode);
                    topHeadLine.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                topHeadLine.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
                showErrorMessage(R.drawable.no_result, "Oops...",
                        "Network failure. Please check your connection\n"
                        + t.toString());
            }
        });
    }


    private void initListener(){
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ImageView imageView = view.findViewById(R.id.img);
                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
                Articles article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("img", article.getUrlToImage());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("date", article.getPublishedAt());
                intent.putExtra("source", article.getSource().getName());
                intent.putExtra("author", article.getAuthor());

                Pair<View, String> pair = androidx.core.util.Pair.create((View)imageView, ViewCompat.getTransitionName(imageView));

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat optionsCompat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(MainActivity.this, pair);
                    ActivityCompat.startActivity(MainActivity.this, intent, optionsCompat.toBundle());

                }
                else{
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        MenuItem searchViewItem
                = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchViewItem.getActionView();
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Search Latest News...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(query.length() > 2){
//                    loadJson(query);
                    onLoadingSwipeRefresh(query);

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
//                loadJson(newText);
                return false;
            }
        });
        searchViewItem.getIcon().setVisible(false, false);

        return true;
    }

    @Override
    public void onRefresh() {
        loadJson("");
    }
    private void onLoadingSwipeRefresh(final String keyword){
        swipeRefreshLayout.post(
                new Runnable() {
                    @Override
                    public void run() {
                        loadJson(keyword);
                    }
                }
        );
    }

    private void showErrorMessage(int imageView, String title, String message){
        if(relativeErrorLayout.getVisibility() == View.GONE) {
            relativeErrorLayout.setVisibility(View.VISIBLE);
        }
        errorImage.setImageResource(imageView);
        errorTitle.setText(title);
        errorMessage.setText(message);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onLoadingSwipeRefresh("");
            }
        });


    }
}
