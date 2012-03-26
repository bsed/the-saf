package cn.salesuite.saf.weibo;

import android.os.Parcel;
import android.os.Parcelable;

public class OAuthToken implements Parcelable {
	private final String secret;
	private final String token;	
	
	@SuppressWarnings("unused")
	private OAuthToken(Parcel parcel) {
		this.token = parcel.readString();
		this.secret =  parcel.readString();
	}
	public OAuthToken(String token, String secret) {
		this.token = token;
		this.secret = secret;
	}

	public String getSecret() {
		return this.secret;
	}

	public String getToken() {
		return this.token;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(token);
		dest.writeString(secret);
	}
	public static final Parcelable.Creator<OAuthToken> CREATOR = new Parcelable.Creator<OAuthToken>() {

		@Override
		public OAuthToken createFromParcel(Parcel source) {
			return new OAuthToken(source.readString(), source.readString());
		}

		@Override
		public OAuthToken[] newArray(int size) {
			return new OAuthToken[size];
		}   
		
	};
}
