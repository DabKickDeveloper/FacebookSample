package com.dabkick.sdk;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by SHWETHA RAO on 04-04-2016.
 */
public class FacebookFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.facebook_friends, container,
                false);

        ListView fbFriends = (ListView)view.findViewById(R.id.fb_contacts);

        final ArrayAdapter fbFriendsAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, FacebookManager.friends);

        fbFriends.setAdapter(fbFriendsAdapter);

        fbFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.e("Dabkick_lib","DISPLAY_NAME " + fbFriendsAdapter.getItem(position));
                DabKick_Agent.checkInStatusMsg(
                        "DISPLAY_NAME " + fbFriendsAdapter.getItem(position));

                DabKick_Agent.goToWaitingScreen(fbFriendsAdapter.getItem(position).toString());
                getActivity().finish();
            }
        });

        return view;
    }
}
