package yau.celine.golocal.utils.interfaces;

import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by Celine on 2018-06-13.
 */

public interface IMainActivity {

    void setToolbarTitle(String fragmentTitle);

    void inflateFragment(String fragmentTag, String message);

    void inflateFragment(String fragmentTag, Parcelable object);

    void inflateFragment(String fragmentTag, ArrayList<Parcelable> objects);

    void lockDrawer();

    void lockDrawerBottom();

    void unlockDrawer();

    void unlockDrawerBottom();

}
