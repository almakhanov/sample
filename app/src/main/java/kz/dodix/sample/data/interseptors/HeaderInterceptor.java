package kz.dodix.sample.data.interseptors;

import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class HeaderInterceptor implements Interceptor {
	@Override
	public Response intercept(Chain chain) throws IOException {
		Request original = chain.request();
		Request.Builder requestBuilder = original.newBuilder()
				.header("Content-Type", "application/json")
				.header("cookie", "TRANSLATE_LANGUAGE=" + "ru")
				.header("X-DigitalBank-device-id", "1")
				.header("Content-Type", "charset=utf-8")
				.header("User-Agent","mobile/Android");

		Request request = requestBuilder.build();
		return chain.proceed(request);
	}
}
