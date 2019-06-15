package csie.aad.ast_album.Models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SpacePhoto implements Parcelable {

    public String mpath;
    public String mtitle;

    public SpacePhoto(String path, String title){
        mpath = path;
        mtitle = title;
    }

    protected SpacePhoto(Parcel in){
        mpath = in.readString();
        mtitle = in.readString();
    }

    public static final Creator<SpacePhoto> CREATOR = new Creator<SpacePhoto>() {
        @Override
        public SpacePhoto createFromParcel(Parcel source) {
            return new SpacePhoto(source);
        }

        @Override
        public SpacePhoto[] newArray(int size) {
            return new SpacePhoto[size];
        }
    };


    public static ArrayList getSpacePhotos() {

        ArrayList mList = new ArrayList();
        mList.add(new SpacePhoto("http://i.imgur.com/zuG2bGQ.jpg", "Galaxy"));
        mList.add(new SpacePhoto("http://i.imgur.com/ovr0NAF.jpg", "Space Shuttle"));
        mList.add(new SpacePhoto("http://i.imgur.com/n6RfJX2.jpg", "Galaxy Orion"));
        mList.add(new SpacePhoto("http://i.imgur.com/qpr5LR2.jpg", "Earth"));
        mList.add(new SpacePhoto("http://i.imgur.com/pSHXfu5.jpg", "Astronaut"));
        mList.add(new SpacePhoto("http://i.imgur.com/3wQcZeY.jpg", "Satellite"));

        return mList;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mpath);
        dest.writeString(mtitle);
    }
}
