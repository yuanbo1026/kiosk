package de.nexxoo.kiosk_app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import de.nexxoo.kiosk_app.db.DatabaseHandler;
import de.nexxoo.kiosk_app.entity.BaseEntity;
import de.nexxoo.kiosk_app.entity.Manual;
import de.nexxoo.kiosk_app.layout.*;
import de.nexxoo.kiosk_app.tools.FileStorageHelper;
import de.nexxoo.kiosk_app.tools.Global;
import de.nexxoo.kiosk_app.tools.Nexxoo;
import de.nexxoo.kiosk_app.webservice.NexxooWebservice;
import de.nexxoo.kiosk_app.webservice.OnJSONResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by b.yuan on 04.08.2015.
 */
public class HistoryActivity extends Activity {

    private SwipeMenuListView listview;
    private List<BaseEntity> mBaseEntityList = new ArrayList<BaseEntity>();
    private HistoryListAdapter listAdapter;

    private Context mContext;

    private FileStorageHelper mFileStorgeHelper;
    private DatabaseHandler mDatabaseHandler;
    private static final int CONTENT_TYPE_MAUNAL = 0;
    private static final int CONTENT_TYPE_CATALOG = 2;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history);
        mContext = this;
        mFileStorgeHelper = new FileStorageHelper(mContext);
        mDatabaseHandler = new DatabaseHandler(mContext);
        listview = (SwipeMenuListView) findViewById(R.id.manual_list);

        getHistoryContetnsFromWebServer();
        initSwipeListView(Global.isNormalScreenSize);

    }

    private Integer[] getContentIdFromDB() {
        List<Integer> integerList = mDatabaseHandler.getAllContents();
        Integer[] ids = new Integer[integerList.size()];
        ids = integerList.toArray(ids);
        return ids;
    }

    private void getHistoryContetnsFromWebServer() {
        Integer[] ids = getContentIdFromDB();
        NexxooWebservice.getContentByIds(true, ids, new OnJSONResponse() {
            @Override
            public void onReceivedJSONResponse(JSONObject json) {
                try {
                    int count = json.getInt("count");
                    Log.d(Nexxoo.TAG, "get manual list size is : " + count);
                    prepareListData(json);

                    listAdapter = new HistoryListAdapter(mContext, Global.isNormalScreenSize ? R.layout
                            .history_listview_item : R.layout.history_listview_item_big, mBaseEntityList);
                    listview.setAdapter(listAdapter);

                } catch (JSONException e) {
                    Log.d("KioskError", "Error!" + e.getMessage());
                }
            }

            @Override
            public void onReceivedError(String msg, int code) {
                Log.d("KioskError", "Error!" + msg);
            }
        });
    }

    private void initSwipeListView(final Boolean isNormal) {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu) {

                SwipeMenuItem download = new SwipeMenuItem(mContext);
                download.setBackground(new ColorDrawable(Color.rgb(0xF3, 0xF3, 0xF3)));
                download.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
                download.setIcon(R.drawable.ic_list_download);
                menu.addMenuItem(download);

                SwipeMenuItem view = new SwipeMenuItem(mContext);
                view.setBackground(new ColorDrawable(Color.rgb(0xE5, 0xF5, 0xFF)));
                view.setWidth(Nexxoo.dp2px(mContext, isNormal ? 90 : 120));
                view.setIcon(R.drawable.ic_list_view);
                menu.addMenuItem(view);

            }
        };
        // set creator
        listview.setMenuCreator(creator);

        // step 2. listener item click event
        listview.setOnMenuItemClickListener(new SwipeMenuListView.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                int contentTypeId = menu.getViewType();
                switch (index) {
                    case 0:
                        if(contentTypeId == CONTENT_TYPE_MAUNAL || contentTypeId == CONTENT_TYPE_CATALOG){
                            // click event for manual and catalog
                            String filename = mBaseEntityList.get(position).getFileName();
                            if (mFileStorgeHelper.isContentDownloaded(filename)) {
                                File file = new File(mFileStorgeHelper.getFileAbsolutePath(filename));
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent i = Intent.createChooser(target, "Open File");
                                startActivity(i);
                            } else {
                                if (listview.getChildAt(position) instanceof SwipeMenuLayout) {
                                    SwipeMenuLayout menuLayout = (SwipeMenuLayout)
                                            listview.getChildAt(position);
                                    menuLayout.smoothCloseMenu();
                                }
                                DownloadAsyncTask task = new DownloadAsyncTask(mContext,
                                        mBaseEntityList
                                                .get(position).getUrl(), mBaseEntityList.get
                                        (position).getFileName(), Global
                                        .DOWNLOAD_TASK_TYPE_PDF);
                                task.execute();

                            }
                        }else{
                            //click event for video
                            DownloadAsyncTask task = new DownloadAsyncTask(mContext,
                                    mBaseEntityList
                                            .get(position).getUrl(), mBaseEntityList.get
                                    (position).getFileName());
                            task.execute();
                        }

                        break;
                    case 1:
                        if(contentTypeId == CONTENT_TYPE_MAUNAL || contentTypeId == CONTENT_TYPE_CATALOG){
                            // click event for manual and catalog
                            String filename1 = mBaseEntityList.get(position).getFileName();
                            if (mFileStorgeHelper.isContentDownloaded(filename1)) {
                                File file = new File(mFileStorgeHelper.getFileAbsolutePath
                                        (filename1));
                                Intent target = new Intent(Intent.ACTION_VIEW);
                                target.setDataAndType(Uri.fromFile(file), "application/pdf");
                                target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                                Intent i = Intent.createChooser(target, "Open File");
                                startActivity(i);
                            } else {
                                if (listview.getChildAt(position) instanceof SwipeMenuLayout) {
                                    SwipeMenuLayout menuLayout = (SwipeMenuLayout)
                                            listview.getChildAt(position);
                                    menuLayout.smoothCloseMenu();
                                }
                                DownloadAsyncTask task1 = new DownloadAsyncTask(mContext,
                                        mBaseEntityList
                                                .get(position).getUrl(), mBaseEntityList.get
                                        (position).getFileName(), Global
                                        .DOWNLOAD_TASK_TYPE_PDF);
                                task1.execute();

                            }
                        }else{
                            //click event for video
                            Boolean isVideoDownloaded = mFileStorgeHelper.isContentDownloaded(mBaseEntityList
                                    .get(position).getFileName());
                            String url = mBaseEntityList.get(position).getUrl();
                            url.replace("www", "nexxoo:wenexxoo4kiosk!@www");
                            Intent i = new Intent(mContext, VideoActivity.class);
                            i.putExtra(mContext.getString(R.string
                                    .video_activity_intent_url_extra), url);
                            String name = mBaseEntityList.get(position).getFileName();
                            i.putExtra("filename", name);
                            i.putExtra("isVideoDownloaded", isVideoDownloaded);
                            mContext.startActivity(i);
                        }

                        break;
                }
                return false;
            }
        });

        // set SwipeListener
        listview.setOnSwipeListener(new SwipeMenuListView.OnSwipeListener() {

            @Override
            public void onSwipeStart(int position) {
                // swipe start
            }

            @Override
            public void onSwipeEnd(int position) {
                // swipe end
            }
        });

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listview.smoothOpenMenu(position);
            }
        });
    }

    private void prepareListData(JSONObject json) {
        try {
            int count = json.getInt("count");
            BaseEntity mBaseEntity = null;
            for (int i = 0; i < count; i++) {
                try {
                    JSONObject jsonContentObj = json.getJSONObject("content" + i);
                    mBaseEntity = new BaseEntity(jsonContentObj);
                    mBaseEntityList.add(mBaseEntity);

                } catch (Exception e) {
                    Log.d(Nexxoo.TAG, e.getMessage());
                }
            }

        } catch (JSONException e) {
            Log.d(Nexxoo.TAG, e.getMessage());
        }
    }


}