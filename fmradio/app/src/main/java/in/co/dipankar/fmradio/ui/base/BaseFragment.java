package in.co.dipankar.fmradio.ui.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public abstract class BaseFragment extends Fragment {
    public Navigation getNavigation(){
       return  ((BaseNavigationActivity)getActivity()).getNavigation();
    }
}
