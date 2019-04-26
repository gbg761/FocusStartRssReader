package com.example.focusstartrssreader.storage;

import android.util.Log;

import com.example.focusstartrssreader.domain.model.RssFeedModel;
import com.example.focusstartrssreader.domain.repository.FeedRepository;
import com.example.focusstartrssreader.network.NetworkConnection;
import com.example.focusstartrssreader.parser.RssFeedParser;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class FeedRepositoryImpl implements FeedRepository {

    private static final String IO_EXCEPTION = "IOException";

    private NetworkConnection connection;
    private RssFeedParser parser;
    private RssFeedDatabase database;

    public FeedRepositoryImpl(NetworkConnection connection, RssFeedParser parser, RssFeedDatabase database) {
        this.connection = connection;
        this.parser = parser;
        this.database = database;
    }

    @Override
    public void uploadData(String urlString) {

        InputStream is = connection.getInputStream(urlString);
        if(is != null) {

            try {
                // парсим ленту
                List<RssFeedModel> rssFeedModelList = parser.parseFeed(is);

                // Из Database объекта получаем Dao
                RssFeedModelDao feedModelDao = database.rssFeedModelDao();

                // записываем ленту в бд
                feedModelDao.insert(rssFeedModelList);
            }
            catch (IOException ex) {
                Log.d(IO_EXCEPTION, ex.getMessage());
            }
        }
    }
}
