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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String POST_KEY = "POST_KEY";
    private static final String PAGE_KEY = "PAGE_KEY";
    private TextView tvContent;
    private TextView tvDate;
    private TextView tvExternal;
    private TextView tvPage;
    private TextView tvPostNum;

    private LoadAsync loadAsync;

    private PostBook book;

    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initBook(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(this);
        tvContent = (TextView) findViewById(R.id.tvContent);
        tvDate = (TextView) findViewById(R.id.tvDate);
        tvExternal = (TextView) findViewById(R.id.tvExternal);
        tvPage = (TextView) findViewById(R.id.tvPage);
        tvPostNum = (TextView) findViewById(R.id.tvPostNum);
        showNext();
    }

    private void initBook(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            book = (PostBook) savedInstanceState.getSerializable(POST_KEY);
        } else {
            book = new PostBook();
        }
    }

    private void showNext() {
        if (book.hasNext()) {
            showPost(book);
        } else {
            loadData();
        }
    }

    private void loadData() {
        if (loadAsync == null) {
            fab.setEnabled(false);
            loadAsync = new LoadAsync();
            loadAsync.execute(getPage());
            loadAsync = null;
        }
    }

    @Override
    public void onClick(View view) {
        showNext();
    }

    public Integer getPage() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        return sharedPref.getInt(PAGE_KEY, 1);
    }

    public void setPage(Integer page) {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(PAGE_KEY, page);
        editor.apply();
    }


    private void showPost(PostBook book) {
        Post post = book.next();
        tvPage.setText(String.format(Locale.getDefault(), "%d", getPage()));
        tvPostNum.setText(book.positionLaBel());
        if (post != null) {
            tvContent.setText(post.getContent());
            tvDate.setText(post.getDate());
            tvExternal.setText(String.format(Locale.getDefault(), "#%d", post.getExternal()));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(POST_KEY, book);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onDestroy() {
        if (loadAsync != null)
            loadAsync.cancel(false);
        super.onDestroy();
    }

    public class LoadAsync extends AsyncTask<Integer, Void, Void> {

        private List<Post> current;
        private Integer page;

        @Override
        protected Void doInBackground(Integer... voids) {
            page = voids[0];
            PostSource source = new PostSource();
            try {
                current = source.getPosts(page);
            } catch (IOException e) {
                current = new ArrayList<>();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            book.setPosts(current);
            if (book.hasNext()) {
                setPage(page + 1);
                showPost(book);
            }
            fab.setEnabled(true);
        }
    }
}
