package com.example.newss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.MenuItemCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.SearchManager;
//import android.widget.SearchView.OnQueryTextListener;
import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
    }
    public void loadJson(final String keyword){
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
                    Toast.makeText(MainActivity.this, "No Result!", Toast.LENGTH_SHORT).show();
                    topHeadLine.setVisibility(View.INVISIBLE);
                    swipeRefreshLayout.setRefreshing(false);
                }
            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                topHeadLine.setVisibility(View.INVISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }


    private void initListener(){
        adapter.setOnItemClickListener(new Adapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(MainActivity.this, NewsDetailActivity.class);
                Articles article = articles.get(position);
                intent.putExtra("url", article.getUrl());
                intent.putExtra("img", article.getUrlToImage());
                intent.putExtra("title", article.getTitle());
                intent.putExtra("date", article.getPublishedAt());
                intent.putExtra("source", article.getSource().getName());
                intent.putExtra("author", article.getAuthor());

                startActivity(intent);

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
}
