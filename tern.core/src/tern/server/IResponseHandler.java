package tern.server;

public interface IResponseHandler {

	void onError(String error);

	void onSuccess(Object data, String dataAsJsonString);
}
