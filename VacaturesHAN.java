package vacaturesHAN;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

public class VacaturesHAN {

    public static void main(String[] args) {
    	String emailFrom = "MAILFROM@gmail.com";
    	String emailPass = "PASSKEY for app";
    	String emailTo = "MAILTO@gmail.com";
    	String outputFile = "LOCATION TO WRITE OUTPUT FILE TO";
    	
    	String emailHeader;
    	StringBuilder emailContent = new StringBuilder();
    	// create key:value pairs to store the title and URL of the corresponding job
        HashMap<String, String> vacaturesSaved = new HashMap<String, String>();
        HashMap<String, String> vacaturesCurrent = new HashMap<String, String>();
        HashMap<String, String> vacaturesNew = new HashMap<String, String>();
        
        //webdriver part using Selenium project
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");
        WebDriver driver = new FirefoxDriver(options);
        driver.get("https://www.han.nl/over-de-han/werken-bij-de-han/vacatures/#/");
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(500));  
        List<WebElement> vacatures = driver.findElements(By.className("finder-result-wrapper"));
        
        //extract the title and URL from the WebElement to vacaturesCurrent
        for (WebElement i : vacatures) {
            vacaturesCurrent.put(i.getText().replace("\n", " | "),i.findElement(By.tagName("a")).getAttribute("href"));
        }
        driver.quit();
        
        //read the saved data from file to vacaturesSaved, if no file is found it just prints an error
        try {
            FileInputStream inStream = new FileInputStream(outputFile);
            ObjectInputStream ois = new ObjectInputStream(inStream);
            vacaturesSaved = (HashMap) ois.readObject();
            ois.close();
            inStream.close();
        } catch (Exception e) {
            System.out.println("something went wrong with reading the file");
        }
        
        //compare vacaturesSaved and vacaturesCurrent, if not already present, add to vacaturesNew.
        for (HashMap.Entry<String, String> entry : vacaturesCurrent.entrySet()) {
            if (vacaturesSaved.containsKey(entry.getKey())) {
                ;
            } else {
                vacaturesNew.put(entry.getKey(), entry.getValue());
            }
        }
        
        //overwrite the saved data with vacaturesCurrent
        try {
            FileOutputStream outStream = new FileOutputStream(outputFile);
            ObjectOutputStream oos = new ObjectOutputStream(outStream);
            oos.writeObject(vacaturesCurrent);
            oos.close();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        //report #create header
        if (vacaturesNew.size() == 1) {
        	emailHeader = "there is " + vacaturesNew.size() + " new job listing on the HAN website";
        } else {
        	emailHeader = "there are " + vacaturesNew.size() + " new job listings on the HAN website";
        }
        
        //report #create content
        for (HashMap.Entry<String, String> entry : vacaturesNew.entrySet()) {
        	emailContent.append("--" + entry.toString() + "\n\n");
        }

        System.out.println(emailHeader);
        System.out.println(emailContent.toString());
        
        if (vacaturesNew.size() > 1) {
        	Mailer.send(emailFrom, emailPass, emailTo, emailHeader, emailContent.toString());
        }
    }
}
