package ru.andsp.readbash;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String POST_KEY = "POST_KEY";
    private TextView tvContent;

    private TextView tvDate;

    private TextView tvExternal;

    private LinkedList<Post> posts;

    LoadAsync loadAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            posts = (LinkedList<Post>) savedInstanceState.getSerializable(POST_KEY);
        }
        setContentView(R.layout.activity_main);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvExternal = (TextView) findViewById(R.id.tvExternal);

        startRead();
    }

    private void startRead() {
        if (posts == null || posts.isEmpty()) {
            posts = new LinkedList<>();
            int page = getPage();
            if (page > 1)
                setPage(page - 1);
        }
        showNext();
    }

    private void showNext() {
        if (posts.isEmpty()) {
            loadAsync = new LoadAsync();
            loadAsync.execute(getPage());
            loadAsync = null;
        } else {
            showPost(posts.removeLast());
        }
    }

    @Override
    public void onClick(View view) {
        showNext();
    }

    private int getPage() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(getString(R.string.current_page), 1);
    }

    private void setPage(int page) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(getString(R.string.current_page), page);
        editor.apply();
    }


    private void showPost(Post post) {
        if (post != null) {
            tvContent.setText(post.getContent());
            tvDate.setText(post.getDate());
            tvExternal.setText(String.format(Locale.getDefault(), "#%d", post.getExternal()));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(POST_KEY, posts);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        if (loadAsync != null)
            loadAsync.cancel(false);
        super.onDestroy();
    }

    public class LoadAsync extends AsyncTask<Integer, Void, Void> {

        private LinkedList<Post> current;
        private Integer page;

        @Override
        protected Void doInBackground(Integer... voids) {
            page = voids[0];
            PostSource source = new PostSource();
            try {
                current = source.getPosts(page);
            } catch (IOException e) {
                current = new LinkedList<>();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!current.isEmpty()) {
                setPage(page + 1);
                posts = current;
                showPost(posts.removeLast());
            } else {
                Post post = new Post();
                post.setExternal(0);
                post.setDate("00.00.0000 00:00");
                post.setContent("Ошибка получения новй порции постов. Проверьте соединение с интернетом.");
                showPost(post);
            }
        }
    }
}
