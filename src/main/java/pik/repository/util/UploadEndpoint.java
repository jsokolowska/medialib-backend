package pik.repository.util;

public class UploadEndpoint {
    private final String X_AUTH_Token;
    private final String url;

    public UploadEndpoint (String authToken, String url){
        X_AUTH_Token = authToken;
        this.url = url;
    }

    public String toJson(){
        return String.format("{ \"X-AUTH-TOKEN\" : \"%s\",  \"url\" : \"%s\"", X_AUTH_Token, url);
    }
}
