package com.varkasa.varkasa.varkasa;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.widget.Switch;

/**
 * Created by A7718 on 11/9/2017.
 */

class SectionsPageAdapter extends FragmentPagerAdapter {
    public SectionsPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
switch (position)
        {
            case 0:
                RequestFragment requestFragment=new RequestFragment();
                return  requestFragment;
            case 1:
                ChatFragment chatFragment=new ChatFragment();
                return chatFragment;

            case 2:

                FriendsFragment friendsFragment=new FriendsFragment();
                return friendsFragment;

            default:
            return null;
        }

    }

    @Override
    public int getCount() {
        return 3;
    }


    public CharSequence getPageTitle(int position)
    {
             switch (position)
             {


                 case 0:

                     return "REQUEST";

                 case 1:
                     return "CHAT";

                 case 2:
                     return  "FRIENDS";

                 default:

                     return  null;

             }



    }
}
