package edu.lander.instagram;

import org.jinstagram.auth.InstagramAuthService;
import org.jinstagram.auth.model.Token;
import org.jinstagram.auth.model.Verifier;
import org.jinstagram.auth.oauth.InstagramService;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;







/**
 * 
 *         You can find where to get all data at https://instagram.com/developer/
 *         you have to sign in/ create an account
 *         then you can go to manage clients and register a new client
 *         fill all that out and have your call back url something accessable to public
 *         after submitting all data needed will be on https://instagram.com/developer/clients/manage/
 *         client info is all the info you need it gives you your client ID, API Secret
 */
public class InstaConnect {

    private final Token EMPTY_TOKEN = null;
    private Token accessToken = null;
    private String apiKey = null;
    private String apiSecret = null;
    private String callBackURL = null;
    private String userName = null;
    private String password = null;
    private WebDriver m_driver = new HtmlUnitDriver();

    /**
     * create a default connection with out filling in any information
     * all fields still need to be used before getting a token
     */
    public InstaConnect() {
        apiKey = null;
        apiSecret = null;
        callBackURL = null;
    }

    /**
     * Create a connection with only your api key, secret, and call back url
     * username and password still need to be accessed before getting an access token
     *
     * @param key
     * @param secret
     * @param callback
     */
    public InstaConnect(String key, String secret, String callback) {
        apiKey = key;
        apiSecret = secret;
        callBackURL = callback;
    }

    /**
     * Create a connection with all data fields entered at this time
     * to create an access token for instagram call first then AuthToken can be used
     *
     * @param key
     * @param secret
     * @param callback
     * @param username
     * @param sPassword
     */
    public InstaConnect(String key, String secret, String callback, String username, String sPassword) {
        apiKey = key;
        apiSecret = secret;
        callBackURL = callback;
        userName = username;
        password = sPassword;
    }

    /**
     * Creates an authorization token for connection to instagram
     * all fields should be filled in
     * <p>
     * ex: apiKey, apiSecret, apiCallBackURL, userName, and password all should be filled
     *
     * @return an instagram access Token
     */
    public Token AuthToken() throws MissingCall {
        if (apiKey == null)
            throw new MissingCall("apiKey");

        else if (apiSecret == null)
            throw new MissingCall("apiSecret");

        else if (callBackURL == null)
            throw new MissingCall("callBackURL");

        else if (userName == null)
            throw new MissingCall("userName");

        else if (password == null)
            throw new MissingCall("password");

        else {
            InstagramService service = new InstagramAuthService().apiKey(apiKey).apiSecret(apiSecret).callback(callBackURL)
                    .build();

            String authorizationUrl = service.getAuthorizationUrl(EMPTY_TOKEN);

            m_driver.get(authorizationUrl);
            WebElement wUsername = m_driver.findElement(By.name("username"));
            WebElement wPassword = m_driver.findElement(By.name("password"));

            wUsername.sendKeys(userName);
            wPassword.sendKeys(password);

            WebElement greenButton = m_driver.findElement(By.xpath("//*[@id=\"login-form\"]/p[3]/input"));
            greenButton.click();
            if (m_driver.getCurrentUrl().substring(0, 37).equals("https://instagram.com/oauth/authorize")) {
                WebElement authButton = m_driver.findElement(By.xpath("/html/body/div/section/div/form/ul/li[2]/input"));
                authButton.click();
            }
            int start = m_driver.getCurrentUrl().indexOf("code=") + 5;
            int end = m_driver.getCurrentUrl().indexOf("&");
            String code;
            if (end == -1)
                code = m_driver.getCurrentUrl().substring(start);
            else
                code = m_driver.getCurrentUrl().substring(start, end);


            Verifier verifier = new Verifier(code);
            accessToken = service.getAccessToken(EMPTY_TOKEN, verifier);

            return accessToken;
        }

    }

    /**
     * Set your instagram API Key
     *
     * @param key: get API Key from Instagram Developers
     */
    public void setApiKey(String key) {
        apiKey = key;
    }

    /**
     * get the current api key
     *
     * @return
     */
    public String getApiKey() {
        return apiKey;
    }

    /**
     * set current API to new secret secret that is set
     *
     * @param secret: get API Secret from Instagram Developers
     */
    public void setApiSecret(String secret) {
        apiSecret = secret;
    }

    /**
     * get current API secret that is set
     *
     * @return
     */
    public String getApiSecret() {
        return apiSecret;
    }

    /**
     * set current callback to new callback URL
     *
     * @param callBack
     */
    public void setCallBackURL(String callBack) {
        callBackURL = callBack;
    }

    /**
     * get current callback url
     *
     * @return
     */
    public String getCallBackURL() {
        return callBackURL;
    }

    /**
     * get current username being used
     *
     * @return
     */
    public String getUserName() {
        return userName;
    }

    /**
     * set new username
     *
     * @param name: should be the user's Instagram user name
     */
    public void setUserName(String name) {
        userName = name;
    }

    /**
     * get current password being used
     *
     * @return
     */
    public String getPassword() {
        return password;
    }

    /**
     * set new password
     *
     * @param pass: should be the user's Instagram user password
     */
    public void setPassword(String pass) {
        password = pass;
    }

}