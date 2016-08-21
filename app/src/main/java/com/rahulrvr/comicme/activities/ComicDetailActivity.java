package com.rahulrvr.comicme.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahulrvr.comicme.MyApplication;
import com.rahulrvr.comicme.R;
import com.rahulrvr.comicme.model.comics.Thumbnail;
import com.rahulrvr.comicme.retrofit.ComicService;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by rahulrv on 8/9/15.
 */
public class ComicDetailActivity extends BaseActivity {

    private static final String ARG_URL = "argUrl";
    private static final String ARG_TITLE = "argTitle";
    private static final String ARG_ID = "argId";
    private static final String ARG_IS_COMIC = "argIsComic";

    Thumbnail mThumbnail;
    @Bind(R.id.backdrop)
    ImageView backdrop;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @Bind(R.id.appbar)
    AppBarLayout appbar;
    @Bind(R.id.main_content)
    CoordinatorLayout mainContent;

    @Bind(R.id.row1)
    TextView txtDescription;

    public static Intent newInstance(Context context, Thumbnail url, String title, int id, boolean isComic) {
        Intent intent = new Intent(context, ComicDetailActivity.class);
        intent.putExtra(ARG_URL, url);
        intent.putExtra(ARG_TITLE, title);
        intent.putExtra(ARG_ID, id);
        intent.putExtra(ARG_IS_COMIC, isComic);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comic_detail_layout);

        ButterKnife.bind(this);
        mThumbnail = (Thumbnail) getIntent().getSerializableExtra(ARG_URL);
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getIntent().getStringExtra(ARG_TITLE));
        if (mThumbnail != null) {
            String url = mThumbnail.getPath() + "/landscape_incredible." + mThumbnail.getExtension();
            Picasso.with(this).load(url).into(backdrop);
        }

        int id = getIntent().getIntExtra(ARG_ID, 0);
        if (id > 0) {
            ComicService service = MyApplication.getInstance().getRestAdapter().create(ComicService.class);
            boolean isComic = getIntent().getBooleanExtra(ARG_IS_COMIC, false);
            if (isComic) {
                service.getComic(id).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(comic -> {
                    Timber.d("comic");
                    if (comic.getData().getResult() != null) {
                        txtDescription.setText(comic.getData().getResult().get(0).getDescription());
                    }

                },throwable -> {
                    Timber.e("Error fetcing comics");
                });
            } else {
                service.getCharacter(id).subscribeOn(Schedulers.computation()).observeOn(AndroidSchedulers.mainThread()).subscribe(comic -> {
                    Timber.d("character");
                    if (comic.getData().getResult() != null) {
                        txtDescription.setText(comic.getData().getResult().get(0).getDescription());
                    }

                });
            }
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(base));
    }

}

