package com.example.focusstartrssreader.util.contract;

import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;

import com.example.focusstartrssreader.service.service.FetchFeedService;
import com.example.focusstartrssreader.ui.activity.add.AddNewFeedActivity;
import com.example.focusstartrssreader.ui.activity.detail.FeedDetailActivity;
import com.example.focusstartrssreader.ui.activity.main.MainActivity;
import com.example.focusstartrssreader.ui.activity.settings.SettingsActivity;
import com.example.focusstartrssreader.worker.DoSyncWorker;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;

public class Contract {

    public final static class Settings {

        public final static String USE_DARK_THEME = "use_dark_theme";
        public final static String REQUEST_SYNC_TAG = "sync";

        public static TaskStackBuilder getTaskStackBuilder(Context context) {
            return TaskStackBuilder.create(context)
                    .addParentStack(SettingsActivity.class)
                    .addNextIntent(new Intent(context, SettingsActivity.class));
        }
    }

    public final static class Exception {

        public static final String XML_PULL_PARSER_EXCEPTION = "XmlPullParserException";
        public static final String MALFORMED_URL_EXCEPTION = "MalformedUrlException";
        public static final String IO_EXCEPTION = "IOException";
    }

    public final static class Feed {

        public final static String BROADCAST_FETCH_FEED_TITLE_ACTION = "broadcast_fetch_feed_title_action";
        public final static String BROADCAST_FETCH_FEED_ACTION = "broadcast_fetch_feed_action";

        public final static String FETCH_FEED_ACTION = "fetch_feed_action";
        public final static String FETCH_FEED_TITLE_ACTION = "fetch_feed_title_action";

        public final static String URL_FEED_TAG = "url_feed";
        public final static String URL_FEED_TITLE_TAG = "url_feed_title";
        public final static String FETCH_FEED_SUCCESS_TAG = "fetch_feed_success";

        public static IntentFilter getIntentFilter() {
            // создаем фильтр для BroadcastReceiver
            IntentFilter filter = new IntentFilter();
            filter.addAction(BROADCAST_FETCH_FEED_ACTION);
            filter.addAction(BROADCAST_FETCH_FEED_TITLE_ACTION);
            return filter;
        }

        public static Intent getIntent(Context context, String feedTitle, String link) {
            Intent intent = new Intent(context, FetchFeedService.class);
            intent.setAction(FETCH_FEED_ACTION);
            // кладем в intent ссылку на новостную ленту и заголовок ленты (используется для имени канала)
            intent.putExtra(URL_FEED_TITLE_TAG, feedTitle).putExtra(URL_FEED_TAG, link);
            return intent;
        }

        public static Intent getIntent(Context context, String link) {
            // Создаем Intent для вызова сервиса
            Intent intent = new Intent(context, FetchFeedService.class);
            intent.setAction(FETCH_FEED_TITLE_ACTION);
            // кладем в intent ссылку на новостную ленту
            intent.putExtra(URL_FEED_TAG, link);
            return intent;
        }

        // используется для уведомления AddNewFeedActivity о том,
        // что данные получены (заголовок новостной ленты(заголовок канала))
        public static Intent getIntent(String rssFeedTitle) {
            Intent broadcastIntent = new Intent(BROADCAST_FETCH_FEED_TITLE_ACTION);
            broadcastIntent.putExtra(URL_FEED_TITLE_TAG, rssFeedTitle);
            return broadcastIntent;
        }

        // используется для уведомления AddNewFeedActivity о том,
        // все данные записаны в бд (канал занесен в список каналов,
        // новостная лента канала занесена в бд списка новостей)
        public static Intent getIntent(Boolean success) {
            Intent broadcastIntent = new Intent(BROADCAST_FETCH_FEED_ACTION);
            broadcastIntent.putExtra(FETCH_FEED_SUCCESS_TAG, success);
            return broadcastIntent;
        }
    }

    public final static class Main {

        // main activity
        public final static String CHANNEL_TITLE = "channel_title";
        // FeedDetailActivity
        public final static String NEWS_ID = "news_id";

        // запускает FeedDetailActivity, передавая ей id(id новости в бд) новости,
        // выбранноной юзером
        public static Intent getIntent(Context context, long id) {
            Intent intent = new Intent(context, FeedDetailActivity.class);
            intent.putExtra(NEWS_ID, id);
            return intent;
        }

        // запускает MainActivity, передавая ей заголовок канала,
        // выбранного юзером из списка каналов
        public static Intent getIntent(Context context, String channelTitle) {
            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra(CHANNEL_TITLE, channelTitle);
            return intent;
        }

        public static TaskStackBuilder getTaskStackBuilder(Context context, Uri data) {
            return TaskStackBuilder
                    .create(context)
                    .addParentStack(AddNewFeedActivity.class)
                    .addNextIntent(new Intent(context, AddNewFeedActivity.class).setData(data));
        }
    }


    public static PendingIntent getPendingIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    // возвращаем OneTimeWorkRequest в MainActivity с помощью которого
    // выполняем обновление новостей канала по свайву
    public static OneTimeWorkRequest getOneTimeWorkRequest(String channelTitle) {
        Data data = new Data.Builder()
                .putString("channel_title", channelTitle)
                .build();
        return new OneTimeWorkRequest.Builder(DoSyncWorker.class)
                .setInputData(data)
                .build();
    }


}
