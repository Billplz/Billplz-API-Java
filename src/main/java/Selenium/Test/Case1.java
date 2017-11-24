package Selenium.Test;

import com.billplz.api.BillplzConnect;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class Case1 {

    public static void main(String[] args) throws IOException, ParseException {

        BillplzConnect bc = new BillplzConnect("4e49de80-1670-4606-84f8-2f1d33a38670");
        JSONParser jsonParser = new JSONParser();

        /**
         * Get FPX Banks
         */
        bc.setMode("staging").setAction("getfpxbanks").send(bc.getHttpAction());
        String jsondata = bc.getHttpOutput();
        //System.out.println(bc.getHttpStatus());
        //System.out.println(jsondata);

        JSONObject jsonObject = (JSONObject) jsonParser.parse(jsondata);
        JSONArray banks = (JSONArray) jsonObject.get("banks");

        String bank_name = null;
        boolean bank_active = false;
        for (int i = 0; i < banks.size(); i++) {
            jsonObject = (JSONObject) jsonParser.parse(banks.get(i).toString());
            bank_name = (String) jsonObject.get("name");
            bank_active = (boolean) jsonObject.get("active");
            if (bank_name.equalsIgnoreCase("TEST0022") && bank_active) {
                break;
            }

        }

        /**
         * Create A Bill
         */
        bc.httppost = null;

        List<NameValuePair> urlParameters = new ArrayList<>();
        urlParameters.add(new BasicNameValuePair("collection_id", "ohjqe1rp"));
        urlParameters.add(new BasicNameValuePair("description", "Test"));
        urlParameters.add(new BasicNameValuePair("email", "test@gmail.com"));
        urlParameters.add(new BasicNameValuePair("name", "Wan Zulkarnain"));
        urlParameters.add(new BasicNameValuePair("amount", "500"));
        urlParameters.add(new BasicNameValuePair("callback_url", "http://google.com"));
        urlParameters.add(new BasicNameValuePair("reference_1_label", "Bank Code"));
        urlParameters.add(new BasicNameValuePair("reference_1", bank_name));

        bc.setMode("staging").setAction("createbill").setPostData(urlParameters).send(bc.getHttpAction());

        jsondata = bc.getHttpOutput();
        //System.out.println(bc.getHttpStatus());
        //System.out.println(jsondata);

        jsonObject = (JSONObject) jsonParser.parse(jsondata);
        String bill_id = (String) jsonObject.get("id");
        String bill_url = (String) jsonObject.get("url");
        //System.out.println("Bill ID: " + bill_id);
        System.out.println("Bill URL: " + bill_url);

        /**
         * Selenium Web Driver
         */
        System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + File.separator + "chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        String baseUrl = bill_url + "?auto_submit=true";
        driver.get(baseUrl);

    }
}
