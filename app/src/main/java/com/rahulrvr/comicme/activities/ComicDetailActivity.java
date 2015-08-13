package com.rahulrvr.comicme.activities;

import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.rahulrvr.comicme.MyApplication;
import com.rahulrvr.comicme.R;
import com.rahulrvr.comicme.model.comics.Thumbnail;
import com.rahulrvr.comicme.retrofit.ComicService;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by rahulrv on 8/9/15.
 */
public class ComicDetailActivity extends AppCompatActivity {

    Thumbnail mThumbnail;
    @InjectView(R.id.backdrop)
    ImageView backdrop;
    @InjectView(R.id.toolbar)
    Toolbar toolbar;
    @InjectView(R.id.collapsing_toolbar)
    CollapsingToolbarLayout collapsingToolbar;
    @InjectView(R.id.appbar)
    AppBarLayout appbar;
    @InjectView(R.id.main_content)
    CoordinatorLayout mainContent;

    @InjectView(R.id.txtDescription)
    TextView txtDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comic_detail_layout);
        ButterKnife.inject(this);
        mThumbnail = (Thumbnail) getIntent().getSerializableExtra("comic");
        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle(getIntent().getStringExtra("comicTitle"));
        if (mThumbnail != null) {
            String url = mThumbnail.getPath() + "/landscape_incredible." + mThumbnail.getExtension();
            Picasso.with(this).load(url).into(backdrop);
        }

        int id = getIntent().getIntExtra("id", 0);
        if (id > 0) {
            ComicService service = MyApplication.getInstance().getRestAdapter().create(ComicService.class);
            service.getComic(id).observeOn(AndroidSchedulers.mainThread()).subscribe(comic -> {
                Log.d("xx", "dsf");
                if(comic.getData().getResult()!=null) {
                    txtDescription.setText(comic.getData().getResult().get(0).getDescription());
                }

            });
        }

    }
}
