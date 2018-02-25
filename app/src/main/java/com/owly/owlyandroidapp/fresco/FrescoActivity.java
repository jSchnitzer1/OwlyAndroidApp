//package com.owly.owlyandroidapp.fresco;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.RequiresApi;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.StaggeredGridLayoutManager;
//import android.widget.TextView;
//
//import com.facebook.drawee.backends.pipeline.Fresco;
//
//import com.owly.owlyandroidapp.ItemsSingleton;
//import com.owly.owlyandroidapp.R;
//import com.owly.owlyandroidapp.bean.Item;
//import com.owly.owlyandroidapp.view.EndlessRecyclerViewScrollListener;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import butterknife.BindView;
//import butterknife.ButterKnife;
//
//public class FrescoActivity extends AppCompatActivity {
//
//  @BindView(R.id.rvList) RecyclerView rvList;
//  private FrescoAdapter itemAdapter;
//  private List<Item> listItem = new ArrayList<>();
//
//  private EndlessRecyclerViewScrollListener endlessRecyclerViewScrollListener;
//
//  @RequiresApi(api = Build.VERSION_CODES.N)
//  @Override protected void onCreate(Bundle savedInstanceState) {
//    super.onCreate(savedInstanceState);
//    Fresco.initialize(this);
//    setContentView(R.layout.activity_fresco);
//
//    ButterKnife.bind(this);
//
//    StaggeredGridLayoutManager staggeredGridLayoutManager =
//        new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//    staggeredGridLayoutManager.setGapStrategy(
//        StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
//
//    final GridLayoutManager manager = new GridLayoutManager(this, 6);
//    manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//      @Override public int getSpanSize(int position) {
//        int index = position % 5;
//        switch (index) {
//          case 0:
//            return 2;
//          case 1:
//            return 2;
//          case 2:
//            return 2;
//          case 3:
//            return 3;
//          case 4:
//            return 3;
//        }
//        return -1;
//      }
//    });
//
//    rvList.setLayoutManager(staggeredGridLayoutManager);
//    itemAdapter = new FrescoAdapter(this);
//    for (int i = 0; i < 10; i++) {
//      listItem.add(ItemsSingleton.INSTANCE.getITEMS().get(i));
//    }
//    itemAdapter.setListItem(listItem);
//    rvList.setAdapter(itemAdapter);
//    rvList.setItemAnimator(null);
//
//    endlessRecyclerViewScrollListener =
//        new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
//
//          @Override public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
//           /* Log.e("", "onLoadMore: ---------------------------------------->" + page);*/
//            List<Item> localList = new ArrayList<>();
//            if (page == 1) {
//              for (int i = 10; i < 20; i++) {
//                localList.add(ItemsSingleton.INSTANCE.getITEMS().get(i));
//              }
//            } else if (page == 2) {
//              for (int i = 21; i < 30; i++) {
//                localList.add(ItemsSingleton.INSTANCE.getITEMS().get(i));
//              }
//            }
//            listItem.addAll(localList);
//            itemAdapter.setListItem(listItem);
//          }
//
//          @Override public void lastVisibleItem(int lastVisibleItem) {
////            Log.e("", "lastVisibleItem: -------------------------------------->" + lastVisibleItem);
////            tvLastItem.setText("With in " + lastVisibleItem);
//          }
//        };
//    rvList.addOnScrollListener(endlessRecyclerViewScrollListener);
//  }
//
//
//}
