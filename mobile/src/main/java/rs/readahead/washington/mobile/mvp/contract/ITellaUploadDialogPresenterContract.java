package rs.readahead.washington.mobile.mvp.contract;

import android.content.Context;

import java.util.List;

import rs.readahead.washington.mobile.domain.entity.TellaUploadServer;

public class ITellaUploadDialogPresenterContract {
    public interface IView {
        Context getContext();
        void onServersLoaded(List<TellaUploadServer> servers);
        void onServersLoadError(Throwable throwable);
    }

    public interface IPresenter extends IBasePresenter {
        void loadServers();
    }
}