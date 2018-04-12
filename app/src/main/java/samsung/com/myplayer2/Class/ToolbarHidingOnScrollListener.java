package samsung.com.myplayer2.Class;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class ToolbarHidingOnScrollListener extends RecyclerView.OnScrollListener {

    Context mContext;
    private final View tabContainer;
    private final View tabbar;
    private final View parallaxScrollingView;
    private final View lastTabView;

    private float parallaxScrollingFactor = 0.7f;

    public ToolbarHidingOnScrollListener(Context context, View tabContainer, View tabbar, View lastTabView, View parallaxScrollingView) {
        this.mContext = context;
        this.tabContainer = tabContainer;
        this.tabbar = tabbar;
        this.parallaxScrollingView = parallaxScrollingView;
        this.lastTabView = lastTabView;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        switch (newState) {
            case RecyclerView.SCROLL_STATE_IDLE:
                if (Math.abs(tabContainer.getTranslationY()) > tabbar.getHeight()) {
                    hideToolbar();
                } else {
                    showToolbar();
                }
                //Glide.with(mContext).resumeRequests();
                break;
            case RecyclerView.SCROLL_STATE_DRAGGING:
                //Glide.with(mContext).pauseRequests();
                tabContainer.clearAnimation();
                break;
            case RecyclerView.SCROLL_STATE_SETTLING:
                //Glide.with(mContext).pauseRequests();
                tabContainer.clearAnimation();
                break;
        }
    }

    protected void showToolbar() {
        tabContainer.clearAnimation();
        tabContainer
                .animate()
                .translationY(0)
                .start();

    }

    private void hideToolbar() {
        tabContainer.clearAnimation();
        tabContainer
                .animate()
                .translationY(-lastTabView.getBottom())
                .start();

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        scrollColoredViewParallax(dy);

        if (dy > 0) {
            hideToolbarBy(dy);
        } else {
            showToolbarBy(dy);
        }
    }

    private void scrollColoredViewParallax(int dy) {
        if (parallaxScrollingView != null) {
            int absoluteTranslationY = (int) (parallaxScrollingView.getTranslationY() - dy * parallaxScrollingFactor);
            parallaxScrollingView.setTranslationY(Math.min(absoluteTranslationY, 0));
        }
    }


    private void hideToolbarBy(int dy) {
        if (cannotHideMore(dy)) {
            tabContainer.setTranslationY(-lastTabView.getBottom());
        } else {
            tabContainer.setTranslationY(tabContainer.getTranslationY() - dy);
        }
    }

    private boolean cannotHideMore(int dy) {
        return Math.abs(tabContainer.getTranslationY() - dy) > lastTabView.getBottom();
    }


    protected void showToolbarBy(int dy) {
        if (cannotShowMore(dy)) {
            tabContainer.setTranslationY(0);
        } else {
            tabContainer.setTranslationY(tabContainer.getTranslationY() - dy);
        }
    }

    private boolean cannotShowMore(int dy) {
        return tabContainer.getTranslationY() - dy > 0;
    }
}