package kz.dodix.sample.data.interseptors;

import kz.dodix.sample.data.remote.TokenHolder;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class TokenInterceptor implements Interceptor {

	private String token;
	private String tokenFirebse;

	private static final String KEY_AUTH = "Authorization";
	private static final String  TOKEN_FIREBASE = "FireToken";

	@Override
	public Response intercept(Chain chain) throws IOException {
		token = "Bearer " + TokenHolder.INSTANCE.getToken();
		tokenFirebse = TokenHolder.INSTANCE.getTokenFirebase();

		Request originalRequest = chain.request();
		Request tokenRequest = originalRequest.newBuilder()
				.addHeader(KEY_AUTH, token)
				.addHeader(TOKEN_FIREBASE,tokenFirebse)
				.build();
		return chain.proceed(tokenRequest);
	}
}