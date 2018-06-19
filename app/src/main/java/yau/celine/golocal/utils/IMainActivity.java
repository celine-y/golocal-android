package yau.celine.golocal.utils;

import android.os.Parcelable;

/**
 * Created by Celine on 2018-06-13.
 */

public interface IMainActivity {

    void setToolbarTitle(String fragmentTitle);

    void inflateFragment(String fragmentTag, String message);

    void inflateFragment(String fragmentTag, Parcelable object);
}
